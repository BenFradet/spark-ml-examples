#!/bin/bash

spark-submit \
  --master local[*] \
  --class io.github.benfradet.smia.chapter2.Titanic \
  chapter2-assembly-1.0.jar \
  /data/train.csv /data/test.csv /data/result.csv
