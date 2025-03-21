import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

// Custom metrics
const messagesPosted = new Counter('messages_posted');
const successRate = new Rate('success_rate');
const messagePostDuration = new Trend('message_post_duration');

export const options = {
  scenarios: {
    // Throughput benchmark using ramp-up pattern
    throughput_test: {
      executor: 'ramping-arrival-rate',
      startRate: 1,        // Starting at 1 iteration per second
      timeUnit: '1s',      // 1 second
      preAllocatedVUs: 50,
      maxVUs: 100,
      stages: [
        { duration: '30s', target: 1 },    // Warm up with 1 iteration per second
        { duration: '1m', target: 10 },    // Ramp up to 10 iterations per second
        { duration: '1m', target: 20 },    // Ramp up to 20 iterations per second
        { duration: '2m', target: 20 },    // Stay at 20 iterations per second
        { duration: '30s', target: 0 },    // Ramp down to 0
      ],
    },
  },
  thresholds: {
    'success_rate': ['rate>=0.95'],                // 95% success rate
    'http_req_duration': ['p(95)<1000'],           // 95% of requests below 1s
    'message_post_duration': ['avg<500'],          // Average post duration below 500ms
    'http_req_failed': ['rate<0.05'],              // Less than 5% failed requests
  },
};

export default function() {
  // Endpoint for posting 100 messages
  const url = 'http://localhost:8081/api/messages/100';
  
  // Optional payload (modify as needed for your API)
  const payload = JSON.stringify({
    // timestamp: new Date().toISOString(),
    // clientId: 'k6-benchmark-client'
  });
  
  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };
  
  // Capture start time to measure duration
  const start = new Date().getTime();
  
  // Make the POST request
  const response = http.post(url, payload, params);
  
  // Capture end time and calculate duration
  const duration = new Date().getTime() - start;
  messagePostDuration.add(duration);
  
  // Verify the response
  const success = check(response, {
    'status is 200 or 201': (r) => r.status === 200 || r.status === 201,
  });
  
  if (success) {
    // If successful, add to successful messages metric
    messagesPosted.add(100); // Each call posts 100 messages
    successRate.add(1);
  } else {
    // Log errors for debugging
    console.error(`Failed request: ${response.status} - ${response.body}`);
    successRate.add(0);
  }
  
  // Small random sleep to add some variability
  sleep(Math.random() * 1); 
}

export function setup() {
  console.log('Starting throughput benchmark...');
  
  // Make a test request to ensure the API is available
  const testResponse = http.get('http://localhost:8081/');
  check(testResponse, {
    'API is available': (r) => r.status !== 0,
  });
  
  return {};
}

export function teardown(data) {
  console.log('Benchmark completed.');
} 