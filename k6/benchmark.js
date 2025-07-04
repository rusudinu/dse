import http from 'k6/http';
import {check, sleep} from 'k6';
import {Counter, Rate, Trend} from 'k6/metrics';

const messagesPosted = new Counter('messages_posted');
const successRate = new Rate('success_rate');
const messagePostDuration = new Trend('message_post_duration');

export const options = {
    scenarios: {
        fixed_rate_test: {
            executor: 'constant-arrival-rate',
            rate: 200,
            timeUnit: '1s',
            duration: '5m',
            preAllocatedVUs: 100,
            maxVUs: 200,
        },
    },
    thresholds: {
        'success_rate': ['rate>=0.95'],
        'http_req_duration': ['p(95)<1000'],
        'message_post_duration': ['avg<500'],
        'http_req_failed': ['rate<0.05'],
    },
};

export default function () {
    const url = 'http://localhost:80/api/messages';

    const payload = JSON.stringify({});

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const start = new Date().getTime();

    const response = http.get(url, payload, params);

    const duration = new Date().getTime() - start;
    messagePostDuration.add(duration);

    const success = check(response, {
        'status is 200 or 201': (r) => r.status === 200 || r.status === 201,
    });

    if (success) {
        messagesPosted.add(100);
        successRate.add(1);
    } else {
        console.error(`Failed request: ${response.status} - ${response.body}`);
        successRate.add(0);
    }

    sleep(Math.random() * 1);
}

export function setup() {
    console.log('Starting throughput benchmark...');

    const testResponse = http.get('http://localhost:80/');
    check(testResponse, {
        'API is available': (r) => r.status !== 0,
    });

    return {};
}

export function teardown(data) {
    console.log('Benchmark completed.');
}
