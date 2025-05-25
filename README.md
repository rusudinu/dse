### Accessing the UIs

- **RabbitMQ Management UI**:
  - Node 1: http://localhost:15672 
  - Node 2: http://localhost:15673
  - Node 3: http://localhost:15674
  - Credentials: guest/guest

- **Grafana UI**:
  - URL: http://localhost:3000
  - Credentials: admin/admin

- **InfluxDB UI**:
  - URL: http://localhost:8086
  - Credentials: admin/adminpassword

### Running Load Test

```bash
k6 run k6/benchmark.js
```

### Restart cluster
```bash
docker compose down && docker compose down -v && docker compose up --build --force-recreate
```
```bash
docker-compose exec rabbitmq1 rabbitmqctl delete_queue messages
```
