package comy.http

import org.jboss.netty.handler.codec.http._
import HttpHeaders._
import HttpHeaders.Names._
import HttpResponseStatus._
import HttpVersion._
import HttpMethod._
import org.jboss.netty.buffer._
import org.jboss.netty.channel._

import comy._
import comy.http.action._

class FrontController(config: Config, db: DB) extends SimpleChannelUpstreamHandler with Logger {
  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {
    val request  = e.getMessage.asInstanceOf[HttpRequest]
    val response = new DefaultHttpResponse(HTTP_1_1, OK)

    val notClosed = route(e, request, response)
    if (notClosed) respond(e, request, response)
  }

  override def exceptionCaught(ctx:ChannelHandlerContext, e:ExceptionEvent) {
    error(e.toString)
    e.getChannel.close
  }

  //----------------------------------------------------------------------------

  /**
   * Does the routing based on request path and method.
   * IP is checked based on config.
   *
   * @return false if the connection has been closed
   */
  private def route(e: MessageEvent, request: HttpRequest, response: HttpResponse): Boolean = {
    val method = request.getMethod
    val uri    = request.getUri
    val qd     = new QueryStringDecoder(uri)
    val path   = qd.getPath

    // Static files
    if ((path == "/" || path == "/admin" || path.startsWith("/static/")) && method == GET) {
      val action = new Static(request, response)
      action.execute
    }

    // Shorten URL
    else if (path == "/api" && method == POST) {
      if (!isApiAllowed(e)) {
        e.getChannel.close
        return false
      }

      val action = new ApiPost(request, response, db)
      action.execute
    }

    // TODO: add admin feature
    else if (path.startsWith("/admin/")) {
      if (!isAdminAllowed(e)) {
        e.getChannel.close
        return false
      }
    }

    // ApiGet is at the lowest level
    else {
      val action = new ApiGet(request, response, db)
      action.execute
    }

    true
  }

  private def respond(e: MessageEvent, request: HttpRequest, response: HttpResponse) {
    val keepAlive = isKeepAlive(request)

    // Add 'Content-Length' header only for a keep-alive connection.
    // Close the non-keep-alive connection after the write operation is done.
    if (keepAlive) {
      response.setHeader(CONTENT_LENGTH, response.getContent.readableBytes)
    }
    val future = e.getChannel.write(response)
    if (!keepAlive) {
      future.addListener(ChannelFutureListener.CLOSE)
    }
  }

  private def isApiAllowed(e: MessageEvent): Boolean = {
    val remoteAddress = e.getRemoteAddress().toString
    val ip = remoteAddress.substring(1, remoteAddress.indexOf(':'))
    config.isApiAllowed(ip)
  }

  private def isAdminAllowed(e: MessageEvent): Boolean = {
    val remoteAddress = e.getRemoteAddress().toString
    val ip = remoteAddress.substring(1, remoteAddress.indexOf(':'))
    config.isAdminAllowed(ip)
  }
}
