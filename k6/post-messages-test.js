import http from 'k6/http';
import { sleep, check } from 'k6';
import { Rate } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');

export const options = {
  // Test configuration for posting messages
  vus: 10,               // 10 virtual users
  duration: '30s',       // Run for 30 seconds
  thresholds: {
    'errors': ['rate<0.1'],               // Error rate should be less than 10%
    'http_req_duration': ['p(95)<500'],   // 95% of requests should be below 500ms
  },
};

export default function() {
  // Endpoint to post 100 messages
  const url = 'http://localhost:8081/api/messages/100';
  
  // You can customize the payload if needed
  const payload = JSON.stringify({});
  
  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };
  
  // Make the POST request
  const response = http.post(url, payload, params);
  
  // Check if the request was successful
  const success = check(response, {
    'status is 200 or 201': (r) => r.status === 200 || r.status === 201,
  });
  
  if (!success) {
    errorRate.add(1);
    console.log(`Error posting messages: ${response.status} - ${response.body}`);
  } else {
    errorRate.add(0);
    console.log(`Successfully posted 100 messages, response size: ${response.body.length} bytes`);
  }
  
  // Add a small pause between requests
  sleep(1);
} 