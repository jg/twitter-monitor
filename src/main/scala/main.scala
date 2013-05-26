package org.jg.twittermonitor

import org.json.simple._
import scala.collection.JavaConversions._
import scala.collection.breakOut

object Tweet {
  def fromJSON(json: String): Option[Tweet] = JSONValue.parse(json) match {
    case obj: JSONObject => {
      val keys = List("id_str", "text", "user")
      if (keys.forall(obj.containsKey(_)) &&
          User.fromJSON(obj.get("user").asInstanceOf[JSONObject].toJSONString()).isDefined) {
        val id = BigInt(obj.get(keys(0)).asInstanceOf[String])
        val text = obj.get(keys(1)).asInstanceOf[String]
        val user = User.fromJSON(obj.get("user").asInstanceOf[JSONObject].toJSONString()).get
        Some(new Tweet(id, text, user))
      } else None
    }
    case _ => None
  }
}

object User {
  def fromJSON(json: String): Option[User] = JSONValue.parse(json) match {
    case obj: JSONObject => {
      val keys = List("screen_name", "profile_image_url")
      if (keys.forall(obj.containsKey(_))) {
        val name = obj.get(keys(0)).asInstanceOf[String]
        val avatarUrl = obj.get(keys(1)).asInstanceOf[String]
        Some(new User(name, avatarUrl))
      } else None
    }
    case _ => None
  }
}


case class User(name: String, avatarUrl: String)
case class Tweet(id: BigInt, text: String, user: User)

case class TweetSet(tweets: Seq[Tweet]) {
  def foreach(f: (Tweet) => Unit) = tweets.foreach(f)
/*
  val minId: BigInt = tweets.minBy((tweet) => tweet.id).id
  val maxId: BigInt = tweets.maxBy((tweet) => tweet.id).id
  val size = tweets.size
  val received_at = System.currentTimeMillis/1000
  */
}

// TODO: handle failure (tweets parseErrors)
class TwitterClient(oauthConsumerKey: String,
                    oauthConsumerSecret: String,
                    oauthToken: String,
                    oauthTokenSecret: String) {
  val oauth = new OAuth(oauthConsumerKey, oauthConsumerSecret, oauthToken, oauthTokenSecret)

  val homeTimelineUrl = "https://api.twitter.com/1.1/statuses/home_timeline.json"

  def homeTimeline(headers: Map[String, String]): TweetSet =
    TweetSet(homeTimelineRaw(headers).flatMap(Tweet.fromJSON(_)))

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

/*
create table if not exists tweets(
  id serial,
  twitter_id bigint not null,
  text string not null,
  user_screen_name string,
  user_avatar string,
  created_at timestamp with time zone not null default current_timestamp,
  updated_at timestamp with time zone not null default current_timestamp
  unique(twitter_id)
)
*/

object DB {
  import scala.slick.driver.PostgresDriver._
  import scala.slick.session.Database
  import Database.threadLocalSession
  import scala.slick.jdbc.{GetResult, StaticQuery => Q}
  import Q.interpolation

    def insert(t: Tweet) = t match {case Tweet(id, text, User(name, avatar_url)) => {
      Database.forURL("jdbc:postgresql:twitter_monitor", driver = "org.postgresql.Driver", user = "twitter_monitor") withSession {
        (Q.u + "insert into tweets (twitter_id, text, user_screen_name, user_avatar) values" + 
          "($$" + id.toString + "$$,$$" + text + "$$,$$" + name + "$$,$$" + avatar_url + "$$)").execute
      }
    }}

    def maxTweetId: BigInt = Database.forURL("jdbc:postgresql:twitter_monitor", driver = "org.postgresql.Driver", user = "twitter_monitor") withSession {
      BigInt(Q.query[Unit, (String)]("select max(twitter_id) from tweets").first())
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
    val maxId = DB.maxTweetId
    val headers = Map("count" -> "100",
                      "since_id" -> maxId.toString,
                      "exclude_replies" -> "true",
                      "include_entities" -> "false")
    val tweets = client.homeTimeline(headers)
    tweets.foreach(DB.insert(_))
  }
}
