package comy.http.action

import org.jboss.netty.handler.codec.http._
import HttpHeaders.Names._
import HttpResponseStatus._
import org.jboss.netty.buffer._
import org.jboss.netty.util.CharsetUtil

import comy.{DB, SaveUrlResult}

/**
 * Mounted at /api/shorten?url=xxx&custom=yyy
 */
class ApiShortenPost(request: HttpRequest, response: HttpResponse, db: DB) extends Abstract(request, response) {
  def execute {
    val urls = qd.getParameters.get("url")
    if (urls == null) {
      response.setStatus(BAD_REQUEST)
    } else {
      val url = urls.get(0)
      val keys = qd.getParameters.get("key")
      val key = if (keys != null) Some(keys.get(0)) else None
      val (result, key2) = db.saveUrl(url, key)
      result match {
        case SaveUrlResult.ERROR =>
          response.setStatus(INTERNAL_SERVER_ERROR)

        case SaveUrlResult.VALID =>
          response.setContent(ChannelBuffers.copiedBuffer(key2, CharsetUtil.UTF_8))
          response.setHeader(CONTENT_TYPE, "text/plain")

        case _ =>
          response.setStatus(CONFLICT)
      }
    }
  }
}
