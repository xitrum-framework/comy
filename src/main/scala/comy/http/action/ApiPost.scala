package comy.http.action

import org.jboss.netty.handler.codec.http._
import HttpHeaders.Names._
import HttpResponseStatus._
import org.jboss.netty.buffer._
import org.jboss.netty.util.CharsetUtil

import comy.DB

class ApiPost(request: HttpRequest, response: HttpResponse, db: DB) extends Abstract(request, response) {
  def execute {
    val url = qd.getParameters.get("url").get(0)
    if (url != null) {
      db.saveUrl(url) match {
        case Some(key) =>
          response.setContent(ChannelBuffers.copiedBuffer(key, CharsetUtil.UTF_8))
          response.setHeader(CONTENT_TYPE, "text/plain")

        case None =>
          response.setStatus(INTERNAL_SERVER_ERROR)
      }
    } else {
      response.setStatus(BAD_REQUEST)
    }
  }
}
