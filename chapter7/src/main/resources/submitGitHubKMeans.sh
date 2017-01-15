#!/bin/bash

spark-submit \
  --master local[*] \
  --class io.github.benfradet.spark.ml.in.action.GitHubKMeans \
  chapter7-assembly-1.0.jar \
  /data/2016-01-01.csv
