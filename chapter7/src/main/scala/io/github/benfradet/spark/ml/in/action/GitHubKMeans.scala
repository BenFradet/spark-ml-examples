package io.github.benfradet.spark.ml.in.action

import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.SparkSession

/**
 * This is the code related to the "Building our pipeline naively" and "Tuning our pipeline"
 * sections of chapter 7.
 */
object GitHubKMeans {
  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      System.err.println("Usage: GitHubKMeans <input file>")
      System.exit(1)
    }

    val spark = SparkSession
      .builder()
      .appName("KMeans for chapter 7")
      .getOrCreate()

    val inputPath = args(0)
    val userActions = spark
      .read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(inputPath)
    //userActions.printSchema()
    //userActions.show(5, truncate = false)

    val assembler = new VectorAssembler()
      .setInputCols(userActions.columns)
      .setOutputCol("features")
    //assembler.explainParams()

    val formattedUserActions = assembler.transform(userActions).cache()
    //formattedUserActions.printSchema()
    //formattedUserActions.show(5, truncate = false)

    (3 to 8).foreach { k =>
      val kmeans = new KMeans()
        .setK(k)
        .setMaxIter(20)
        .setTol(1e-4)
      //kmeans.explainParams()

      val kmeansModel = kmeans.fit(formattedUserActions)

      val wcss = kmeansModel.computeCost(formattedUserActions)
      println(s"Within-cluster sum of squares for $k clusters = $wcss")

      println("Cluster centers:")
      kmeansModel.clusterCenters.foreach(println)

      val userActionsWithCenters = kmeansModel
        .transform(formattedUserActions)
        .select("features", "prediction")
      //userActionsWithCenters.printSchema()
      //userActionsWithCenters.show(5, truncate = false)
    }

    spark.stop()
  }
}
