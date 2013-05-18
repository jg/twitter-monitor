import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers

import org.jg.twittermonitor._

class OAuthSuite extends FunSpec with ShouldMatchers {
  // example from https://dev.twitter.com/docs/auth/authorizing-request

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
