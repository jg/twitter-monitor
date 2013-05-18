package org.jg.twittermonitor

import org.apache.http.client.{HttpClient => ApacheHttpClient}
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.impl.client.BasicResponseHandler

class HttpClient {
  def get(url: String, headers: Map[String, String] = Map()): String = {
    val httpClient: ApacheHttpClient = new DefaultHttpClient()
    try {
      val httpGet: HttpGet = new HttpGet(url)
      headers.foreach{case (name, value) => httpGet.addHeader(name, value)}
      val responseHandler: ResponseHandler[String] = new BasicResponseHandler()
      httpClient.execute(httpGet, responseHandler)
    } finally {
      httpClient.getConnectionManager().shutdown()
    }
  }
}
