package comy.http.action

import org.jboss.netty.handler.codec.http._
import HttpHeaders.Names._
import HttpResponseStatus._
import org.jboss.netty.buffer._

class Static(request: HttpRequest, response: HttpResponse) extends Abstract(request, response) {
  def execute {
    StaticCache(path) match {
      case Some((bytes, contentType)) =>
        response.setContent(ChannelBuffers.copiedBuffer(bytes))
        response.setHeader(CONTENT_TYPE, contentType)

      case None =>
        response.setStatus(NOT_FOUND)
    }
  }
}
