import http from 'k6/http';
import { sleep, check } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');
const messagePostLatency = new Trend('message_post_latency');

// Default options
export const options = {
  scenarios: {
    // Smoke test - low load to verify functionality
    smoke: {
      executor: 'constant-vus',
      vus: 1,
      duration: '1m',
      gracefulStop: '5s',
      tags: { test_type: 'smoke' },
    },
    
    // Load test - moderate constant load 
    load: {
      executor: 'constant-vus',
      vus: 10,
      duration: '5m',
      gracefulStop: '10s',
      startTime: '1m10s', // Start after smoke test
      tags: { test_type: 'load' },
    },
    
    // Stress test - ramping up to a high load
    stress: {
      executor: 'ramping-vus',
      startVUs: 0,
      stages: [
        { duration: '2m', target: 50 },  // Ramp up to 50 VUs over 2 minutes
        { duration: '5m', target: 50 },  // Stay at 50 VUs for 5 minutes
        { duration: '2m', target: 0 },   // Ramp down to 0 VUs over 2 minutes
      ],
      gracefulStop: '10s',
      startTime: '6m20s', // Start after load test
      tags: { test_type: 'stress' },
    },
    
    // Spike test - sudden spike in traffic
    spike: {
      executor: 'ramping-arrival-rate',
      startRate: 1,
      timeUnit: '1s',
      preAllocatedVUs: 100,
      maxVUs: 200,
      stages: [
        { duration: '1m', target: 1 },    // Baseline
        { duration: '30s', target: 100 }, // Spike to 100 requests per second
        { duration: '1m', target: 100 },  // Hold spike
        { duration: '30s', target: 1 },   // Back to baseline
      ],
      gracefulStop: '10s',
      startTime: '16m', // Start after stress test
      tags: { test_type: 'spike' },
    },
  },
  thresholds: {
    'errors': ['rate<0.1'], // Error rate should be less than 10%
    'http_req_duration': ['p(95)<500'], // 95% of requests should be below 500ms
    'message_post_latency': ['p(99)<1000'], // 99% of message posts should be below 1s
  },
};

export default function() {
  // Send 100 messages
  const url = 'http://localhost:8081/api/messages/100';
  const payload = JSON.stringify({
    // You can add any required payload here
    // Since no specific payload was mentioned, we're using an empty object
  });
  
  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };
  
  const startTime = new Date().getTime();
  const response = http.post(url, payload, params);
  const endTime = new Date().getTime();
  
  // Record latency
  messagePostLatency.add(endTime - startTime);
  
  // Check response
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
  
  // Additional small tests could be added here
  
  sleep(1); // Brief pause between iterations
}

// Optional: helper functions for additional test scenarios
export function setup() {
  // Setup code - runs once at the beginning
  console.log('Starting load tests...');
  return {};
}

export function teardown(data) {
  // Teardown code - runs once at the end
  console.log('Load tests completed.');
} 