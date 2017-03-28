### Spark ML in action [![Build Status](https://travis-ci.org/BenFradet/spark-ml-in-action.svg?branch=master)](https://travis-ci.org/BenFradet/spark-ml-in-action)

#### Building a chapter

Assuming you have [sbt](http://www.scala-sbt.org/) installed and launched in
the directory and you want to build chapter X:

```bash
$ sbt "project chapterX" "assembly"
```

#### Launching a class for a chapter

Assuming you've downloaded [Spark](http://spark.apache.org/downloads.html),
uncompressed the archive somewhere and put the `spark-shell` command,
which is located in the `bin/` folder, on your PATH:

```bash
spark-submit \
  --master local[*] \
  --class io.github.benfradet.spark.ml.in.action.SomeClass \
  chapterX-assembly-1.0.jar \
  arguments
```

You can have a look at the submit scripts in the `resources/` folder for each
chapter ([chapter 4 for example](chapter4/src/main/resources/)).
