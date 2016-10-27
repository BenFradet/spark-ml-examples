package io.github.benfradet.spark.ml.in.action

import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.SparkSession

object KMeans {
  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      System.err.println("Usage: KMeans <input file>")
      System.exit(1)
    }

    val spark = SparkSession
      .builder()
      .appName("Data preparation for chapter 7")
      .getOrCreate()

    val inputPath = args(0)
    val userActions = spark
      .read
      .format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load(inputPath)
    userActions.printSchema()

    val assembler = new VectorAssembler()
      .setInputCols(userActions.columns)
      .setOutputCol("features")

    val formattedUserActions = assembler.transform(userActions).cache()

    (2 to 15).foreach { k =>
      val kmeans = new KMeans()
        .setK(k)
        .setSeed(1L)

      val model = kmeans.fit(formattedUserActions)

      val wsse = model.computeCost(formattedUserActions)
      println(s"Within set sum of squared errors for $k clusters = $wsse")

      //println("Cluster centers:")
      //model.clusterCenters.foreach(println)
    }

    spark.stop()
  }
}
