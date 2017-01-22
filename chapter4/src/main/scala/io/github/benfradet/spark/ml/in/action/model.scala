package io.github.benfradet.spark.ml.in.action

/** Model case classes for chapter 4. */
object model {
  /**
  * Case class representing an issue.
  * @param text Text describing the issue (title and body)
  * @param label Label affected to the issue, can be "enhancement", "bug" or "question"
  */
  case class Issue(
    text: String,
    label: String
  )

  /**
  * Case class representing a label in a GitHub payload for an IssuesEvent.
  * Note that the default field is omitted since it's a reserved keyword and can't be turned from
  * a DataFrame to a Dataset.
  * @param url URL of the issue
  * @param name Name of the label
  * @param color Hex color code for the label
  */
  case class GHLabel(
    url: String,
    name: String,
    color: String
  )

  /**
  * Case class representing a simplified issue in a GitHub payload for an IssuesEvent.
  * @param text Text describing the issue (title and body)
  * @param labels List of labels affected to this issue
  */
  case class GHIssue(
    text: String,
    labels: Seq[GHLabel]
  )
}
