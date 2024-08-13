# IoT Data Processing and Alert System

This project sets up an alert system using Flume, Kafka, and Spark Streaming. It processes IoT data from JSON files and sends it to Kafka topics based on specified conditions.

## Setup Instructions

### Prerequisites

- Hadoop
- Apache Flume
- Apache Kafka
- Apache Spark

### Setup Steps

1. **Create the log file:**

    ```bash
    sh createlog.sh
    ```

2. **Configure and start Flume:**

    ```bash
    flume-ng agent --conf /path/to/flume/conf/ -f /path/to/flume/basic-flume.conf -Dflume.root.logger=DEBUG,console -n agent
    ```

3. **Copy JSON file to HDFS:**

    ```bash
    hadoop fs -copyFromLocal iotdata.json /BigDataProject/localcopied
    ```

4. **Start Zookeeper:**

    ```bash
    bin/zkServer.sh start
    ```

5. **Start Kafka broker:**

    ```bash
    nohup bin/kafka-server-start etc/kafka/server.properties > /dev/null 2>&1 &
    ```

6. **Create Kafka topics:**

    ```bash
    bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic idle
    bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 3 --topic action
    ```

7. **Run Spark Streaming code:**

    ```bash
    spark-shell --master local --packages org.apache.spark:spark-sql-kafka-0-10_2.11:2.4.0
    ```

### File Descriptions

- **`createlog.sh`**: Script to create and continuously update a log file from JSON files.
- **`basic-flume.conf`**: Configuration file for Flume to transfer data to HDFS.
- **`spark-streaming.scala`**: Scala code for Spark Streaming to process and send data to Kafka topics.
- **`setup.sh`**: Script to initialize Zookeeper and Kafka.

### Appendix

For detailed output and screenshots of the system in action, refer to the `docs/` directory.
