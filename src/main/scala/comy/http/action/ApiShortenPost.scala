package comy.http.action

import org.jboss.netty.handler.codec.http._
import HttpHeaders.Names._
import HttpResponseStatus._
import org.jboss.netty.buffer._
import org.jboss.netty.util.CharsetUtil

import comy.DB

/**
 * Mounted at /api/shorten?url=xxx
 */
class ApiShortenPost(request: HttpRequest, response: HttpResponse, db: DB) extends Abstract(request, response) {
  def execute {
    val urls = qd.getParameters.get("url")
    if (urls == null) {
      response.setStatus(BAD_REQUEST)
    } else {
      val url = urls.get(0)
      db.saveUrl(url) match {
        case Some(key) =>
          response.setContent(ChannelBuffers.copiedBuffer(key, CharsetUtil.UTF_8))
          response.setHeader(CONTENT_TYPE, "text/plain")

        case None =>
          response.setStatus(INTERNAL_SERVER_ERROR)
      }
    }
  }
}
