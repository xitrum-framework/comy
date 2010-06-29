package comy.http.action

import org.jboss.netty.handler.codec.http._
import HttpHeaders.Names._
import HttpResponseStatus._
import org.jboss.netty.buffer._
import org.jboss.netty.util.CharsetUtil

import comy.DB

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
      val customs = qd.getParameters.get("custom")
      //println(customs)
      if (customs != null) { //Save custom url instead of random key
        val custom =  customs.get(0)
        //println(custom)
        db.saveCustomUrl(url,custom) match {
          case Some(key) =>
            response.setContent(ChannelBuffers.copiedBuffer(key, CharsetUtil.UTF_8))
            response.setHeader(CONTENT_TYPE, "text/plain")

          case None =>
            response.setStatus(INTERNAL_SERVER_ERROR)
        }
      } else {
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
}
