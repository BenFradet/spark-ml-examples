#!/bin/bash

spark-submit \
  --master local[*] \
  --driver-memory 4G \
  --class io.github.benfradet.smia.chapter4.GitHubIssueClassifier \
  chapter4-assembly-1.0.jar \
  /data/2016-01-01.json
