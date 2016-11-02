package io.github.benfradet.spark.ml.in.action

import org.apache.spark.sql.SparkSession

/** This is the code related to chapter 2. */
object Titanic {
  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      System.err.println("Usage: Titanic <train file> <test file> <output file>")
      System.exit(1)
    }

    val spark = SparkSession
      .builder()
      .appName("Data preparation for chapter 7")
      .getOrCreate()

    val trainFilePath = args(0)
    val titanicTrain = spark
      .read
      .format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load(trainFilePath)
    titanicTrain.printSchema()
    titanicTrain.show(5, truncate = false)

    val testFilePath = args(1)
    val titanicTest = spark
      .read
      .format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load(testFilePath)
    titanicTest.printSchema()
    titanicTest.show(5, truncate = false)

    spark.stop()
  }
}
