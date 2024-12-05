#!/bin/bash

echo "Start script creat-topics.sh"
while ! nc -z localhost 9092; do
  echo "Waiting for Kafka..."
  sleep 3
done

echo "Creating topic..."
kafka-topics --create --bootstrap-server kafka:9092 --replication-factor 1 --partitions 1 --topic myTopic --if-not-exists
echo "Topic created with success"