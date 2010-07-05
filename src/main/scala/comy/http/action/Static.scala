package comy.http.action

import org.jboss.netty.handler.codec.http._
import HttpHeaders.Names._
import HttpResponseStatus._
import org.jboss.netty.buffer._
import org.jboss.netty.util.CharsetUtil

/**
 * Mounted at /, /admin, /static/xxx
 */
class Static(request: HttpRequest, response: HttpResponse) extends Abstract(request, response) {
  def execute {
    StaticCache(path) match {
      case Some((bytes, contentType)) =>
        response.setHeader(CONTENT_TYPE, contentType)
        response.setContent(ChannelBuffers.copiedBuffer(bytes))

      case None =>
        response.setStatus(NOT_FOUND)
        response.setContent(ChannelBuffers.copiedBuffer("Not found", CharsetUtil.UTF_8))
    }
  }
}
