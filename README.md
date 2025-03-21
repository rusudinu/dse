# RabbitMQ Monitoring with Grafana and InfluxDB

This project sets up monitoring for a RabbitMQ cluster using Grafana and InfluxDB (without Prometheus).

## Components

- **RabbitMQ Cluster**: Three node RabbitMQ cluster
- **Telegraf**: Collects metrics from the RabbitMQ nodes using the Management API
- **InfluxDB**: Time-series database to store the metrics
- **Grafana**: Visualization platform with pre-configured dashboards for RabbitMQ

## Getting Started

### Prerequisites

- Docker and Docker Compose

### Running the Stack

Start the entire stack using Docker Compose:

```bash
docker-compose up -d
```

This will start:
- Three RabbitMQ nodes in a cluster
- InfluxDB for storing metrics
- Telegraf for collecting RabbitMQ metrics
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

## Monitoring Dashboards

The following dashboards are available in Grafana:

1. **RabbitMQ Overview**: General metrics about the RabbitMQ cluster, including queue messages, publish rates, consumer counts, memory usage, connections, and channels.

2. **RabbitMQ Nodes**: Detailed metrics for each RabbitMQ node, including CPU usage, memory usage, file descriptors, and Erlang processes.

3. **RabbitMQ Queues**: Queue-specific metrics including message counts, ready messages, unacknowledged messages, and message rates.

## How It Works

1. Telegraf collects metrics from RabbitMQ Management API endpoints.
2. Metrics are stored in InfluxDB.
3. Grafana visualizes the metrics using pre-configured dashboards.

This approach eliminates the need for Prometheus while still providing comprehensive monitoring for your RabbitMQ cluster.

## Troubleshooting

### No metrics showing in Grafana

1. Verify that Telegraf is connecting to RabbitMQ by checking logs:
   ```bash
   docker-compose logs telegraf
   ```

2. Verify that InfluxDB is receiving and storing data:
   ```bash
   docker-compose logs influxdb
   ```

3. Make sure that the Grafana InfluxDB data source is correctly configured. Go to Configuration > Data Sources in Grafana UI to check.

### RabbitMQ nodes not clustering properly

1. Check the logs for RabbitMQ nodes:
   ```bash
   docker-compose logs rabbitmq1 rabbitmq2 rabbitmq3
   ```

2. Verify that the Erlang cookie is consistent across all nodes.

## Customization

To customize the metrics collected by Telegraf or add more dashboards to Grafana, you can:

1. Modify the `telegraf/telegraf.conf` file to change what metrics are collected.
2. Create or import additional Grafana dashboards and save them in the `grafana/provisioning/dashboards/rabbitmq` directory. 