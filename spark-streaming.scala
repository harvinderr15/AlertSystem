import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._

// Create Spark session
val spark = SparkSession.builder().appName("IoT Data Processing").getOrCreate()

// Define schema
val userSchema = new StructType()
  .add("Arrival_Time", "string")
  .add("Device", "string")
  .add("gt", "string")

// Read data from HDFS
val iot = spark.readStream.format("json")
  .schema(userSchema)
  .option("path", "hdfs:///BigDataProject/flume/basic/*").load()

val localcopied = spark.readStream.format("json")
  .schema(userSchema)
  .option("path", "hdfs:///BigDataProject/localcopied/").load()

// Add source column
val iot_with_source = iot.withColumn("source", lit("iot"))
val localcopied_with_source = localcopied.withColumn("source", lit("localcopied"))

// Union dataframes
val union_df = iot_with_source.union(localcopied_with_source)

// Filter and send to Kafka topics
val idle_df = union_df.filter(col("gt") === "stand" || col("gt") === "sit")
val action_df = union_df.filter(!(col("gt") === "stand" || col("gt") === "sit"))

val idle_key_val = idle_df.withColumn("key", lit(100))
  .select(col("key").cast("string"), concat(col("Arrival_Time"), lit(" "), col("Device"), lit(" "), col("gt"), lit(" "), col("source")).alias("value"))

val action_key_val = action_df.withColumn("key", lit(100))
  .select(col("key").cast("string"), concat(col("Arrival_Time"), lit(" "), col("Device"), lit(" "), col("gt"), lit(" "), col("source")).alias("value"))

// Write to Kafka
val idle_stream = idle_key_val.writeStream
  .format("kafka")
  .option("kafka.bootstrap.servers", "localhost:9092")
  .option("topic", "idle")
  .option("checkpointLocation", "file:///home/iharvinder15/chkpt/idle")
  .outputMode("append")
  .start()

val action_stream = action_key_val.writeStream
  .format("kafka")
  .option("kafka.bootstrap.servers", "localhost:9092")
  .option("topic", "action")
  .option("checkpointLocation", "file:///home/iharvinder15/chkpt/action")
  .outputMode("append")
  .start()

idle_stream.awaitTermination()
action_stream.awaitTermination()
