package org.jg.twittermonitor

import org.json.simple
import org.json.simple._
import scala.collection.JavaConversions._
import scala.collection.breakOut
import scala.collection.IndexedSeq

object Tweet {
  def fromJSON(json: String): Option[Tweet] = JSONValue.parse(json) match {
    case obj: JSONObject => {
      val keys = List("id", "text")
      if (keys.forall(obj.containsKey(_))) {
        val id = obj.get("id_str").asInstanceOf[String]
        val text = obj.get("text").asInstanceOf[String]
        Some(new Tweet(id, text))
      } else None
    }
    case _ => None
  }
}

class Tweet(val id: String, val text: String)

// TODO: handle failure (tweets parseErrors)
class TwitterClient(oauthConsumerKey: String,
                    oauthConsumerSecret: String,
                    oauthToken: String,
                    oauthTokenSecret: String) {
  val oauth = new OAuth(oauthConsumerKey, oauthConsumerSecret, oauthToken, oauthTokenSecret)

  val homeTimelineUrl = "https://api.twitter.com/1.1/statuses/home_timeline.json"

  def homeTimeline(headers: Map[String, String]): Seq[Tweet] =
    homeTimelineRaw(headers).flatMap(Tweet.fromJSON(_))

  // Returns list of tweets in JSON
  private def homeTimelineRaw(headers: Map[String, String]): Seq[String] = {
    val response = get(homeTimelineUrl, headers)
    val jsonArray: JSONArray = JSONValue.parse(response).asInstanceOf[JSONArray]
    (for (i <- 0 to jsonArray.size-1)
      yield jsonArray.get(i).asInstanceOf[JSONObject].toJSONString())(breakOut)
  }


  // currently handles only GET requests
  private def oauthRequest(method: String, url: String, headers: Map[String, String]): String = {
    val c = new HttpClient()
    val headerString = oauth.headerString(method, url, headers)
    val query = headers.map{case (k,v) => k + "=" + v}.mkString("&")

    c.get(url + "?" + query, Map[String, String]("Authorization" -> headerString) ++ headers)
  }

  private def get(url: String, headers: Map[String, String]): String =
    oauthRequest("GET", url, headers)

}
}
object Main {
  import scalax.io._


  val apiTokens = new ApiTokens("config/api_tokens.yml")
  val client: TwitterClient = new TwitterClient(
    apiTokens.get("oauthConsumerKey"),
    apiTokens.get("oauthConsumerSecret"),
    apiTokens.get("oauthToken"),
    apiTokens.get("oauthTokenSecret"))

  def main(args: Array[String]) = {
  }

}
