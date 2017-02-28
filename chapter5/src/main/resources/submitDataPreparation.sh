#!/bin/bash

spark-submit \
  --master local[*] \
  --class io.github.benfradet.smia.chapter5.DataPreparation \
  chapter5-assembly-1.0.jar \
  /data/2016-01-01.json.gz /data/2016-01-01.json
