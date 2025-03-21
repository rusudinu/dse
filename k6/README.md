# k6 Load Tests

## Running Tests

### Basic test
```bash
k6 run post-messages-test.js
```

### Comprehensive test suite
```bash
k6 run load-tests.js
```

### Throughput benchmark
```bash
k6 run benchmark.js
```

### Run specific scenario
```bash
k6 run --tag testType=smoke load-tests.js
```

### Custom configuration
```bash
k6 run --vus 20 --duration 1m post-messages-test.js
```

### Results to InfluxDB
```bash
k6 run --out influxdb=http://localhost:8086/k6 load-tests.js
```
