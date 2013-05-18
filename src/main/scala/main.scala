package org.jg.twittermonitor

class TwitterClient(oauthConsumerKey: String,
                    oauthConsumerSecret: String,
                    oauthToken: String,
                    oauthTokenSecret: String) {
  val oauth = new OAuth(oauthConsumerKey, oauthConsumerSecret, oauthToken, oauthTokenSecret)

  val homeTimelineUrl = "https://api.twitter.com/1.1/statuses/home_timeline.json"

  class Tweet

  // currently handles only GET requests
  def request(method: String, url: String, headers: Map[String, String]): String = {
    val c = new HttpClient()
    val headerString = oauth.headerString(method, url, headers)
    val query = headers.map{case (k,v) => k + "=" + v}.mkString("&")

    c.get(url + "?" + query, Map[String, String]("Authorization" -> headerString) ++ headers)
  }
}
object Main {
  import scalax.io._

  val homeTimelineUrl = "https://api.twitter.com/1.1/statuses/home_timeline.json"

  val apiTokens = new ApiTokens("config/api_tokens.yml")
  val client: TwitterClient = new TwitterClient(
    apiTokens.get("oauthConsumerKey"),
    apiTokens.get("oauthConsumerSecret"),
    apiTokens.get("oauthToken"),
    apiTokens.get("oauthTokenSecret"))

  def main(args: Array[String]) = {
    val headers = Map("count" -> "1")
    val tweet = client.request("GET", homeTimelineUrl, headers)
    val output: Output = Resource.fromFile("/home/jg/tweet.txt")
    output.write(tweet)(Codec.UTF8)

  /*
    println(apiTokens.get("oauthConsumerKey"))
    println(apiTokens.get("oauthConsumerSecret"))
    println(apiTokens.get("oauthToken"))
    println(apiTokens.get("oauthTokenSecret"))
  */
  }

}
