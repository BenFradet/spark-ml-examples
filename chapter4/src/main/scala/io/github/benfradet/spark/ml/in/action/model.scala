package io.github.benfradet.spark.ml.in.action

object model {
  case class Issue(
    text: String,
    label: String
  )

  case class GHLabel(
    url: String,
    name: String,
    color: String
  )

  case class GHIssue(
    text: String,
    labels: Seq[GHLabel]
  )
}
