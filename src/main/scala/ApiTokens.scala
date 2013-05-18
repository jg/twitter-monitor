package org.jg.twittermonitor

/**
 * Helper class for reading out the config values from a file
 *
 * @constructor create ApiTokens from a file path
 * @param file path
 */
class ApiTokens(fileName: String) {
  val lines = scala.io.Source.fromFile(fileName).getLines
  /** map from yml keys to values, written for simple string values in mind */
  val map: Map[String, String] = Map() ++ lines.map(line => {
    val array = line.split(":").take(2).map(_.trim())
    array(0) -> array(1).split("\"")(1)
  })

  /** return the value of a given key*/
  def get(key: String): String = map.get(key).get
}
