package io.github.benfradet.spark.ml.in.action

import org.apache.spark.sql.{SparkSession, Row, SaveMode}
import org.apache.spark.sql.functions.udf

object DataPreparation {
  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      System.err.println("Usage: DataPreparation <input file> <output file>")
      System.exit(1)
    }

    val spark = SparkSession
      .builder()
      .appName("Data preparation for chapter 7")
      .getOrCreate()
    import spark.implicits._

    val inputPath = args(0)
    val events = spark.read.json(inputPath)
    events.printSchema()

    val splitEventUDF = udf(splitEvent)
    val projectedEvents = events.select(
      $"actor.login".alias("username"),
      splitEventUDF($"type", $"payload").alias("type")
    )
    projectedEvents.printSchema()

    val groupedEvents = projectedEvents.groupBy("username", "type").count()
    groupedEvents.printSchema()

    val distinctEventTypes = groupedEvents
      .select("type")
      .distinct()
      .map(_.getString(0))
      .collect()
    val pivotedEvents = groupedEvents
      .groupBy("username")
      .pivot("type", distinctEventTypes)
      .sum("count")
      .na.fill(0L)
    pivotedEvents.printSchema()

    val outputPath = args(1)
    pivotedEvents
      .drop("username")
      .write
      .format("csv")
      .option("header", "true")
      .mode(SaveMode.Overwrite)
      .save(outputPath)

    spark.stop()
  }

  val splitEvent = (t: String, p: Row) => {
    val getEvent =
      (evt: String, subEvt: String) => subEvt.capitalize + evt

    val refTypeEvents = Set("CreateEvent", "DeleteEvent")
    val actionEvents = Set("IssuesEvent", "MembershipEvent", "PullRequestEvent",
      "PullRequestReviewComment", "RepositoryEvent")

    t match {
      case s if refTypeEvents.contains(s) =>
        getEvent(s, p.getAs[String]("ref_type"))
      case s if actionEvents.contains(s) =>
        getEvent(s, p.getAs[String]("action"))
      case "WatchEvent" => "StarEvent"
      case other => other
    }
  }
}
