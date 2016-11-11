package io.github.benfradet.spark.ml.in.action

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.feature.{VectorAssembler, StringIndexer}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.avg
import org.apache.spark.sql.types.{IntegerType, DoubleType}

/** This is the code related to chapter 2. */
object Titanic {
  def main(args: Array[String]): Unit = {
    if (args.length < 3) {
      System.err.println("Usage: Titanic <train file> <test file> <output file>")
      System.exit(1)
    }

    val spark = SparkSession
      .builder()
      .appName("Chapter 2")
      .getOrCreate()
    import spark.implicits._

    val trainFilePath = args(0)
    val titanicTrain = spark
      .read
      .format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load(trainFilePath)
      .withColumn("Survived", $"Survived".cast(DoubleType))
      .cache()
    titanicTrain.printSchema()
    titanicTrain.show(5, truncate = false)
    titanicTrain.describe("Age").show()
    titanicTrain.where("Fare").show()

    val avgAge = titanicTrain.select(avg($"Age")).first().getDouble(0)
    val avgFare = titanicTrain.select(avg($"Fare")).first().getDouble(0)
    val imputedMap = Map("Age" -> avgAge, "Fare" -> avgFare)
    val imputedTitanicTrain = titanicTrain.na.fill(imputedMap)

    val categoricalCols = Seq("Pclass", "Sex", "Embarked")
    val indexers = categoricalCols.map { colName =>
      new StringIndexer()
        .setInputCol(colName)
        .setOutputCol(colName + "Indexed")
    }

    val numericalCols = Seq("Age", "SibSp", "Parch", "Fare")
    val featuresCol = "features"
    val assembler = new VectorAssembler()
      .setInputCols(Array(numericalCols ++ categoricalCols.map(_ + "Indexed"): _*))
      .setOutputCol(featuresCol)

    val labelCol = "Survived"
    val decisionTree = new DecisionTreeClassifier()
      .setLabelCol(labelCol)
      .setFeaturesCol(featuresCol)

    val pipeline = new Pipeline().setStages(Array(indexers :+ assembler :+ decisionTree: _*))

    val model = pipeline.fit(imputedTitanicTrain)

    val testFilePath = args(1)
    val titanicTest = spark
      .read
      .format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load(testFilePath)
    titanicTest.printSchema()
    titanicTest.show(5, truncate = false)
    println(titanicTest.where($"Age".isNull).count())
    println(titanicTest.where($"Fare".isNull).count())

    val imputedTitanicTest = titanicTest.na.fill(imputedMap)

    val predictions = model.transform(imputedTitanicTest)

    val outputPath = args(2)
    predictions
      .select($"PassengerId", $"prediction".cast(IntegerType).alias("Survived"))
      .coalesce(1)
      .write
      .format("csv")
      .option("header", "true")
      .save(outputPath)

    spark.stop()
  }
}
