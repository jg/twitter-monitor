import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

import org.jg.twittermonitor._

class OAuthSuite extends FunSpec with ShouldMatchers {
  // example from https://dev.twitter.com/docs/auth/authorizing-request

  describe("Tweet") {
    val tweetJSON = """{"created_at":"Sun May 19 04:48:23 +0000 2013","id":335980230080622592,"id_str":"335980230080622592","text":"RT @cdixon: Autodesk buying Tinkercad and making it free is a win for users http:\/\/t.co\/ikgW8VPZqQ","source":"\u003ca href=\"http:\/\/www.linkedin.com\/\" rel=\"nofollow\"\u003eLinkedIn\u003c\/a\u003e","truncated":false,"in_reply_to_status_id":null,"in_reply_to_status_id_str":null,"in_reply_to_user_id":null,"in_reply_to_user_id_str":null,"in_reply_to_screen_name":null,"user":{"id":14344469,"id_str":"14344469","name":"Peter Skomoroch","screen_name":"peteskomoroch","location":"Silicon Valley","description":"My mission is to create intelligent systems that help people make better decisions. Principal Data Scientist @LinkedIn. Machine Learning, Hadoop, Big Data.","url":"http:\/\/t.co\/coIHrIxtTb","entities":{"url":{"urls":[{"url":"http:\/\/t.co\/coIHrIxtTb","expanded_url":"http:\/\/datawrangling.com","display_url":"datawrangling.com","indices":[0,22]}]},"description":{"urls":[]}},"protected":false,"followers_count":12321,"friends_count":902,"listed_count":858,"created_at":"Wed Apr 09 19:05:55 +0000 2008","favourites_count":1694,"utc_offset":-28800,"time_zone":"Pacific Time (US & Canada)","geo_enabled":true,"verified":false,"statuses_count":9316,"lang":"en","contributors_enabled":false,"is_translator":false,"profile_background_color":"1A1B1F","profile_background_image_url":"http:\/\/a0.twimg.com\/profile_background_images\/4230724\/Quantumfoam.jpg","profile_background_image_url_https":"https:\/\/si0.twimg.com\/profile_background_images\/4230724\/Quantumfoam.jpg","profile_background_tile":true,"profile_image_url":"http:\/\/a0.twimg.com\/profile_images\/3276454686\/8f8493dfc040e56ef7ff8f59f9474774_normal.jpeg","profile_image_url_https":"https:\/\/si0.twimg.com\/profile_images\/3276454686\/8f8493dfc040e56ef7ff8f59f9474774_normal.jpeg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/14344469\/1357265856","profile_link_color":"2FC2EF","profile_sidebar_border_color":"181A1E","profile_sidebar_fill_color":"252429","profile_text_color":"666666","profile_use_background_image":true,"default_profile":false,"default_profile_image":false,"following":true,"follow_request_sent":null,"notifications":null},"geo":null,"coordinates":null,"place":null,"contributors":null,"retweet_count":0,"favorite_count":0,"entities":{"hashtags":[],"symbols":[],"urls":[{"url":"http:\/\/t.co\/ikgW8VPZqQ","expanded_url":"http:\/\/blog.tinkercad.com\/2013\/05\/18\/autodesk_tinkercad\/","display_url":"blog.tinkercad.com\/2013\/05\/18\/aut\u2026","indices":[76,98]}],"user_mentions":[{"screen_name":"cdixon","name":"chris dixon","id":2529971,"id_str":"2529971","indices":[3,10]}]},"favorited":false,"retweeted":false,"possibly_sensitive":false,"lang":"en"}"""

    describe("fromJSON") {
      val tweetOpt = Tweet.fromJSON(tweetJSON)
      for (tweet <- tweetOpt) {
        tweet.id should equal(BigInt("335980230080622592"))
        tweet.text should equal("""RT @cdixon: Autodesk buying Tinkercad and making it free is a win for users http://t.co/ikgW8VPZqQ""")
      }
    }

  }
  describe("User") {
    val userJSON = """{"id":14344469,"id_str":"14344469","name":"Peter Skomoroch","screen_name":"peteskomoroch","location":"Silicon Valley","description":"My mission is to create intelligent systems that help people make better decisions. Principal Data Scientist @LinkedIn. Machine Learning, Hadoop, Big Data.","url":"http:\/\/t.co\/coIHrIxtTb","entities":{"url":{"urls":[{"url":"http:\/\/t.co\/coIHrIxtTb","expanded_url":"http:\/\/datawrangling.com","display_url":"datawrangling.com","indices":[0,22]}]},"description":{"urls":[]}},"protected":false,"followers_count":12321,"friends_count":902,"listed_count":858,"created_at":"Wed Apr 09 19:05:55 +0000 2008","favourites_count":1694,"utc_offset":-28800,"time_zone":"Pacific Time (US & Canada)","geo_enabled":true,"verified":false,"statuses_count":9316,"lang":"en","contributors_enabled":false,"is_translator":false,"profile_background_color":"1A1B1F","profile_background_image_url":"http:\/\/a0.twimg.com\/profile_background_images\/4230724\/Quantumfoam.jpg","profile_background_image_url_https":"https:\/\/si0.twimg.com\/profile_background_images\/4230724\/Quantumfoam.jpg","profile_background_tile":true,"profile_image_url":"http:\/\/a0.twimg.com\/profile_images\/3276454686\/8f8493dfc040e56ef7ff8f59f9474774_normal.jpeg","profile_image_url_https":"https:\/\/si0.twimg.com\/profile_images\/3276454686\/8f8493dfc040e56ef7ff8f59f9474774_normal.jpeg","profile_banner_url":"https:\/\/pbs.twimg.com\/profile_banners\/14344469\/1357265856","profile_link_color":"2FC2EF","profile_sidebar_border_color":"181A1E","profile_sidebar_fill_color":"252429","profile_text_color":"666666","profile_use_background_image":true,"default_profile":false,"default_profile_image":false,"following":true,"follow_request_sent":null,"notifications":null}"""
    describe("fromJSON") {
      val userOpt = User.fromJSON(userJSON)
      for (user <- userOpt) {
        user.name should equal("peteskomoroch")
        user.avatarUrl should equal("http://a0.twimg.com/profile_images/3276454686/8f8493dfc040e56ef7ff8f59f9474774_normal.jpeg")
      }
    }
  }

  describe("headerString") {
    val oauthConsumerKey = "xvz1evFS4wEEPTGEFPHBog"
    val oauthToken = "370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb"
    val oauthConsumerSecret = "kAcSOqF21Fu85e7zjz7ZN2U4ZRhfV3WpwPAoE3Z7kBw"
    val oauthTokenSecret = "LswwdoUaIvS8ltyTt5jkRh4J50vUPVVHtR2YPi5kE"

    val method = "POST"
    val url = "https://api.twitter.com/1/statuses/update.json"
    val headers = Map("status" -> "Hello Ladies + Gentlemen, a signed OAuth request!", "include_entities" -> "true")

    val oauth = new OAuth(oauthConsumerKey, oauthConsumerSecret, oauthToken, oauthTokenSecret) {
      override def baseOAuthHeaders = Map(
        "oauth_consumer_key" -> oauthConsumerKey,
        "oauth_nonce" -> "kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg",
        "oauth_signature_method" -> "HMAC-SHA1",
        "oauth_timestamp" -> "1318622958",
        "oauth_token" -> oauthToken,
        "oauth_version" -> "1.0"
      )
    }

    it("headerString") {
      oauth.headerString(method, url, headers) should equal("""OAuth oauth_consumer_key="xvz1evFS4wEEPTGEFPHBog", oauth_nonce="kYjzVBB8Y0ZFabxSWbWovY3uYSQ2pTgmZeNu2VS4cg", oauth_signature="tnnArxj06cWHq44gCs1OSKk%2FjLY%3D", oauth_signature_method="HMAC-SHA1", oauth_timestamp="1318622958", oauth_token="370773112-GmHxMAgYyLbNEtIKZeRNFsMKPR9EyMZeS9weJAEb", oauth_version="1.0"""")
    }
  }

  // generated using OAuth tool https://dev.twitter.com/apps/194943/oauth?nid=9713
  describe("headerString") {
    val oauthConsumerKey = "UYlUXs8vVcpwYNuOtIenA"
    val oauthConsumerSecret = "Gw4XBrk52DkOfTGLXsN4BceKo8mz56Kh8o8zuRdHvw"
    val oauthToken =  "7047482-yOCCULhWHeTiMaM19eYPTtAFOvT92CE7AzAZq2Mrw"
    val oauthTokenSecret = "gYooSn4NH6yEDE1QSJZAzj5l4l3fgIqOP0Du6vLcZI"
    val oauth = new OAuth(oauthConsumerKey, oauthConsumerSecret, oauthToken, oauthTokenSecret) {
      override def baseOAuthHeaders = Map(
        "oauth_consumer_key" -> oauthConsumerKey,
        "oauth_nonce" -> "f31683969b6cd1a80894e78618f1142c",
        "oauth_signature_method" -> "HMAC-SHA1",
        "oauth_timestamp" -> "1367601109",
        "oauth_token" -> oauthToken,
        "oauth_version" -> "1.0"
      )
    }
    val method = "GET"
    val url = "https://api.twitter.com/1.1/statuses/home_timeline.json"

    it ("should work") {
      val s = """OAuth oauth_consumer_key="UYlUXs8vVcpwYNuOtIenA", oauth_nonce="f31683969b6cd1a80894e78618f1142c", oauth_signature="8KRVSRY%2F%2FB40Ka4kXfrlac0IXVk%3D", oauth_signature_method="HMAC-SHA1", oauth_timestamp="1367601109", oauth_token="7047482-yOCCULhWHeTiMaM19eYPTtAFOvT92CE7AzAZq2Mrw", oauth_version="1.0""""
      oauth.headerString(method, url, Map()) should equal(s)
    }
  }

}
