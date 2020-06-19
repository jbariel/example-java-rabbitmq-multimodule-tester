# RabbitMQ Multi-module tester
Java Producer/Consumer for RabbitMQ as a multi-module pom

# Getting started
Clone the repo, and run `mvn` to build the project.

`docker-compose build && docker-compose up`

# Usage
Environment Variables:
- `RMQ_URI` => URI for RMQ instance (includes username/password, hostname, port and vhost)
- `RMQ_EXCHANGE_NAME` => name for the exchange
- `RMQ_QUEUE_NAME` => name of the queue
- `RMQ_INIT_DELAY` => number of seconds to wait for RMQ to come up before connecting

## Running as a .jar file

# Issues
Please use the [Issues tab](../../issues) to report any problems or feature requests.

# License
This is licensed under Apache-2.0
