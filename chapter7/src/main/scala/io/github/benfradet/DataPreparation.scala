package io.github.benfradet

import org.apache.spark.sql.{SparkSession, Row}
import org.apache.spark.sql.functions.udf

object DataPreparation {
  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      System.err.println("Usage: DataPreparation <json file>")
      System.exit(1)
    }

    val spark = SparkSession
      .builder()
      .appName("Data preparation chapter 7")
      .getOrCreate()
    import spark.implicits._

    val path = args(0)
    val events = spark.read.json(path)
    events.printSchema()

    val splitEventUDF = udf(splitEvent)
    val projectedEvents = events.select(
      $"actor.login".alias("username"),
      splitEventUDF($"type", $"payload").alias("type")
    )

    val groupedEvents = projectedEvents.groupBy("username", "type").count()

    val reshapedEvents = groupedEvents
      .map(r => (r.getAs[String]("username"),
        (r.getAs[String]("type"), r.getAs[Long]("count"))))
      .groupByKey(_._1)
  }

  val makeRow = (kvs: (String, (String, (String, Long)))) => { case (k, vs) =>

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
