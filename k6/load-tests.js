import http from 'k6/http';
import { sleep, check } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const errorRate = new Rate('errors');
const messagePostLatency = new Trend('message_post_latency');

export const options = {
  scenarios: {
    smoke: {
      executor: 'constant-vus',
      vus: 1,
      duration: '1m',
      gracefulStop: '5s',
      tags: { test_type: 'smoke' },
    },
    
    load: {
      executor: 'constant-vus',
      vus: 10,
      duration: '5m',
      gracefulStop: '10s',
      startTime: '1m10s',
      tags: { test_type: 'load' },
    },
    
    stress: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '2m', target: 50 },
        { duration: '5m', target: 50 },
        { duration: '2m', target: 0 },
      ],
      gracefulStop: '10s',
      startTime: '6m20s',
      tags: { test_type: 'stress' },
    },
    
    spike: {
      executor: 'ramping-arrival-rate',
      startRate: 1,
      timeUnit: '1s',
      preAllocatedVUs: 100,
      maxVUs: 200,
      stages: [
        { duration: '1m', target: 1 },
        { duration: '30s', target: 100 },
        { duration: '1m', target: 100 },
        { duration: '30s', target: 1 },
      ],
      gracefulStop: '10s',
      startTime: '16m',
      tags: { test_type: 'spike' },
    },
  },
  thresholds: {
    'errors': ['rate<0.1'],
    'http_req_duration': ['p(95)<500'],
    'message_post_latency': ['p(99)<1000'],
  },
};

export default function() {
  const url = 'http://localhost:8081/api/messages/100';
  const payload = JSON.stringify({
  });
  
  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };
  
  const startTime = new Date().getTime();
  const response = http.post(url, payload, params);
  const endTime = new Date().getTime();
  
  messagePostLatency.add(endTime - startTime);
  
  const success = check(response, {
    'status is 200 or 201': (r) => r.status === 200 || r.status === 201,
    'response body has data': (r) => r.body.length > 0,
  });
  
  if (!success) {
    errorRate.add(1);
    console.log(`Error posting messages: ${response.status} - ${response.body}`);
  } else {
    errorRate.add(0);
  }
  
  sleep(1);
}

export function setup() {
  console.log('Starting load tests...');
  return {};
}

export function teardown(data) {
  console.log('Load tests completed.');
}