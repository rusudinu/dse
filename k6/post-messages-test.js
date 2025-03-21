import http from 'k6/http';
import { sleep, check } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

export const options = {
  vus: 10,
  duration: '30s',
  thresholds: {
    'errors': ['rate<0.1'],
    'http_req_duration': ['p(95)<500'],
  },
};

export default function() {
  const url = 'http://localhost:8081/api/messages/100';
  
  const payload = JSON.stringify({});
  
  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };
  
  const response = http.post(url, payload, params);
  
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
  
  sleep(1);
} 