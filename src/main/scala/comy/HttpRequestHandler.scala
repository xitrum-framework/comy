package comy

import org.jboss.netty.handler.codec.http.HttpHeaders._;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names._;
import org.jboss.netty.handler.codec.http.HttpResponseStatus._;
import org.jboss.netty.handler.codec.http.HttpVersion._;
import org.jboss.netty.buffer._
import org.jboss.netty.channel._
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.util.CharsetUtil

class HttpRequestHandler(config: Config) extends SimpleChannelUpstreamHandler with Logger {
  private val db = new DB(config)

  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {
    if (!isAllowed(e)) {
      e.getChannel.close
      return
    }

    val request  = e.getMessage.asInstanceOf[HttpRequest]
    val response = new DefaultHttpResponse(HTTP_1_1, OK)

    val uri: String = request.getUri
    if (uri.indexOf("/api") == 0) {
      val url = uri.substring(5)
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
    } else {
      val key = uri.substring(1)  // Skip "/" prefix
      db.getUrl(key) match {
        case Some(url) =>
          response.setStatus(MOVED_PERMANENTLY)
          response.setHeader(LOCATION, url)
        case None =>
          response.setStatus(NOT_FOUND)
      }
    }

    val keepAlive = isKeepAlive(request)

    // Add 'Content-Length' header only for a keep-alive connection.
    // Close the non-keep-alive connection after the write operation is done.
    if (keepAlive) {
      response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes())
    }
    val future = e.getChannel.write(response)
    if (!keepAlive) {
      future.addListener(ChannelFutureListener.CLOSE)
    }
  }

  override def exceptionCaught(ctx:ChannelHandlerContext, e:ExceptionEvent ) {
    error(e.toString)

    e.getChannel.close
  }

  private def isAllowed(e: MessageEvent): Boolean = {
    val remoteAddress = e.getRemoteAddress().toString
    val ip = remoteAddress.substring(1, remoteAddress.indexOf(':'))
    config.isAllowed(ip)
  }
}
