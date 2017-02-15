package io.github.benfradet.spark.ml.in.action

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature._
import org.apache.spark.ml.tuning.{CrossValidator, ParamGridBuilder}
import org.apache.spark.sql.SparkSession

object GitHubIssueClassifier {
  def main(args: Array[String]): Unit = {
    if (args.length < 1) {
      System.err.println("Usage: GitHubIssueClassifier <input file>")
      System.exit(1)
    }

    val spark = SparkSession
      .builder()
      .appName("Classifier for chapter 4")
      .getOrCreate()

    val labelCol = "label"
    val idxdLabelCol = labelCol + "Indexed"

    val inputPath = args(0)
    val issues = spark.read.json(inputPath)
    //issues.printSchema()
    //issues.show(5, truncate = false)

    val Array(training, test) = issues.randomSplit(Array(0.8, 0.2))

    val labelIndexer = new StringIndexer()
      .setInputCol(labelCol)
      .setOutputCol(idxdLabelCol)
      .fit(training)
    val tokenizer = new RegexTokenizer()
      .setInputCol("text")
      .setOutputCol("words")
      .setPattern("\\W")
      .setMinTokenLength(2)
    //tokenizer.explainParams()
    val remover = new StopWordsRemover()
      .setInputCol("words")
      .setOutputCol("filtered_words")
      .setCaseSensitive(false)
    //remover.explainParams()
    val hashingTF = new HashingTF()
      .setInputCol("filtered_words")
      .setOutputCol("hashed_words")
      .setNumFeatures(16384)
    //hashingTF.explainParams()
    val idf = new IDF()
      .setInputCol("hashed_words")
      .setOutputCol("features")
      .setMinDocFreq(10)
    //idf.explainParams()
    val dtc = new DecisionTreeClassifier()
      .setLabelCol(idxdLabelCol)
      .setFeaturesCol("features")
      .setMaxDepth(12)
    //dtc.explainParams()
    val indexToLabel = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("predictedLabel")
      .setLabels(labelIndexer.labels)
    //indexToLabel.explainParams()

    val pipeline = new Pipeline()
      .setStages(Array(labelIndexer, tokenizer, remover, hashingTF, idf, dtc, indexToLabel))

    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol(idxdLabelCol)
      .setMetricName("accuracy")

    val paramGrid = new ParamGridBuilder()
      .addGrid(dtc.maxBins, Array(5, 7))
      .build()

    val cv = new CrossValidator()
      .setEstimator(pipeline)
      .setEvaluator(evaluator)
      .setEstimatorParamMaps(paramGrid)
      .setNumFolds(5)

    val cvModel = cv.fit(training)

    val predictions = cvModel
      .transform(test)
    //predictions.printSchema()
    //predictions.show(5, truncate = false)

    val accuracy = evaluator.evaluate(predictions)
    println(s"Accuracy: $accuracy")

    // retrieving the best model's params
    val bestEstimatorParamMap = cvModel
      .getEstimatorParamMaps
      .zip(cvModel.avgMetrics)
      .maxBy(_._2)
      ._1
    println(s"Best params:\n$bestEstimatorParamMap")

    spark.stop()
  }
}
