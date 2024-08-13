#!/bin/bash

# Start Zookeeper
bin/zkServer.sh start

# Start Kafka broker
nohup bin/kafka-server-start etc/kafka/server.properties > /dev/null 2>&1 &
