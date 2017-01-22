package io.github.benfradet.spark.ml.in.action

import io.github.benfradet.spark.ml.in.action.model._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{concat, lit}

/** This is the code related to the "Preparing the data" section of chapter 4. */
object DataPreparation {
  def main(args: Array[String]): Unit = {
    if (args.length < 2) {
      System.err.println("Usage: DataPreparation <input file> <output file>")
      System.exit(1)
    }

    val spark = SparkSession
      .builder()
      .appName("Data preparation for chapter 4")
      .getOrCreate()
    import spark.implicits._

    val inputPath = args(0)
    val events = spark.read.json(inputPath)
    //events.printSchema()
    //events.show(5, truncate = false)

    val issueEvents = events
      .filter($"type" === "IssuesEvent" &&
        !$"payload.issue.title".isNull && !$"payload.issue.body".isNull)
    val projectedIssues = issueEvents.select(
      concat($"payload.issue.title", lit(" "), $"payload.issue.body").as("text"),
      $"payload.issue.labels"
    )
    // No label default
    //projectedIssues.printSchema()
    //projectedIssues.show(5, truncate = false)

    // Consider only the bug, enhancement and question default labels
    val possibleLabels = Set("enhancement", "bug", "question")
    val issues = projectedIssues
      .as[GHIssue]
      .flatMap { i =>
        val labels = i.labels.map(_.name).filter(possibleLabels.contains)
        labels.map(n => Issue(i.text.replaceAll("[\n\r]|```.*```", " "), n))
      }
    //issues.groupBy("label").count().show()

    val outputPath = args(1)
    issues
      .coalesce(1)
      .write
      .json(outputPath)

    spark.stop()
  }
}
