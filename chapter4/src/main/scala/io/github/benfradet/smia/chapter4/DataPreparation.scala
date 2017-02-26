package io.github.benfradet.smia.chapter4

import io.github.benfradet.smia.chapter4.model._
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
    //events.show(5)

    val issueEvents = events.filter($"type" === "IssuesEvent")
    val projectedIssues = issueEvents.select(
      $"payload.issue.title",
      $"payload.issue.body",
      $"payload.issue.labels"
    )
    // Replace null bodies by empty strings
    val noNullBodyIssues = projectedIssues.na.fill("")
    val concatIssues = noNullBodyIssues.select(
      concat($"title", lit(" "), $"body").as("text"),
      $"labels"
    )
    // No label default
    //projectedIssues.printSchema()
    //projectedIssues.show(5)

    val ghIssues = concatIssues.as[GHIssue]
    //ghIssues.show(5)

    // Consider only the bug, enhancement and question default labels because of
    //import org.apache.spark.sql.functions.desc
    //ghIssues
    //  .flatMap(_.labels.map(_.name))
    //  .groupBy("value")
    //  .count()
    //  .orderBy(desc("count"))
    //  .show(5)
    val possibleLabels = Set("enhancement", "bug", "question")
    val issues = ghIssues
      .flatMap { i =>
        val labels = i.labels
          .map(_.name)
          .filter(possibleLabels.contains)
        labels.map(n => Issue(i.text.replaceAll("[\n\r]", " "), n))
      }
    //isues.show(5)
    //issues.groupBy("label").count().show()

    val outputPath = args(1)
    issues
      .write
      .json(outputPath)

    spark.stop()
  }
}
