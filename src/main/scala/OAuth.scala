package org.jg.twittermonitor


class OAuth(oauthConsumerKey: String,
            oauthConsumerSecret: String,
            oauthToken: String,
            oauthTokenSecret: String) {
  import scala.collection.immutable.TreeMap

  def headerString(method: String, baseUrl: String, headers: Map[String, String]): String = {
    val sig = signature(signatureBaseString(method, baseUrl, baseOAuthHeaders ++ headers), signingKey)

    val signedHeaders = sortedMap(baseOAuthHeaders ++ Map("oauth_signature" -> sig))
    "OAuth " + signedHeaders
                 .map { case (k, v) => percentEncode(k) + "=\"" + percentEncode(v) + "\"" }
                 .mkString(", ")
  }

  def baseOAuthHeaders: Map[String, String] = Map(
    "oauth_consumer_key" -> oauthConsumerKey,
    "oauth_signature_method" -> "HMAC-SHA1",
    "oauth_nonce" -> (System.currentTimeMillis / 1000).toString,
    "oauth_timestamp" -> (System.currentTimeMillis / 1000).toString,
    "oauth_token" -> oauthToken,
    "oauth_version" -> "1.0"
  )

  private def parameterString(headers: Map[String, String]): String =
    // percentEncode headers, sort by key (in TreeMap), join kvs with '=' and
    // map els with '&'
    (for ((k, v) <- sortedMap(baseOAuthHeaders ++ headers))
      yield (percentEncode(k) + "=" + percentEncode(v)))
      .mkString("&")

  private def sortedMap[A, B](m: Map[A, B])(implicit ordering: Ordering[A]): TreeMap[A, B] = new TreeMap[A, B]() ++ m

  private def signatureBaseString(method: String, baseUrl: String, headers: Map[String, String]) =
    List(method.toUpperCase(), percentEncode(baseUrl), percentEncode(parameterString(headers))).mkString("&")

  private def signingKey =
    List(oauthConsumerSecret, oauthTokenSecret)
      .map(percentEncode(_))
      .mkString("&")

  private def percentEncode(s: String): String = {
    import java.net.URLEncoder
    if (s == null) return ""
    URLEncoder.encode(s, "UTF-8")
      .replace("+", "%20").replace("*", "%2A")
      .replace("%7E", "~");
  }

  private def hmacSha1(value: String, key: String): String = {
    import java.security.SignatureException
    import javax.crypto.Mac
    import javax.crypto.spec.SecretKeySpec
    val secret = new SecretKeySpec(key.getBytes, "HmacSHA1")
    val mac = Mac.getInstance("HmacSHA1")
    mac.init(secret)
    val result = mac.doFinal(value.getBytes())
    new sun.misc.BASE64Encoder().encode(result)
  }

  private def signature(signatureBaseString: String, signingKey: String): String =
    hmacSha1(signatureBaseString, signingKey)
}

