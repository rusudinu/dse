## Components

- **RabbitMQ Cluster**: Three node RabbitMQ cluster
- **InfluxDB**: Time-series database to store the metrics
- **Grafana**: Visualization platform with pre-configured dashboards for RabbitMQ

## Getting Started

### Prerequisites

- Docker and Docker Compose

### Running the Stack

Start the entire stack using Docker Compose:

```bash
docker compose up --build --force-recreate
```
This will start:
- Three RabbitMQ nodes in a cluster
- InfluxDB for storing metrics
- Grafana for visualization
- Producer and Consumer applications (if included in your setup)

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
