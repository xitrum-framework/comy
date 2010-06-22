package comy

import org.jboss.netty.handler.codec.http._
import HttpHeaders._
import HttpHeaders.Names._
import HttpResponseStatus._
import HttpVersion._
import HttpMethod._
import org.jboss.netty.buffer._
import org.jboss.netty.channel._
import org.jboss.netty.util.CharsetUtil

class HttpRequestHandler(config: Config, db: DB) extends SimpleChannelUpstreamHandler with Logger {
  override def messageReceived(ctx: ChannelHandlerContext, e: MessageEvent) {
    val request  = e.getMessage.asInstanceOf[HttpRequest]
    val response = new DefaultHttpResponse(HTTP_1_1, OK)

    val uri = request.getUri
    val qd = new QueryStringDecoder(uri)
    val path = qd.getPath
    val method = request.getMethod
    if (path == "/") {
      if (!isApiAllowed(e)) {
        e.getChannel.close
        return
      }
      if (method == GET)
        processHumanGet(response)
      else
        processHumanPost(response)
    } else if (path == "/api" && method == POST) {
      if (!isApiAllowed(e)) {
        e.getChannel.close
        return
      }
      processApiPost(qd, response)
    } else {
      processApiGet(uri, response)
    }

    respond(e, request, response)
  }

  override def exceptionCaught(ctx:ChannelHandlerContext, e:ExceptionEvent) {
    error(e.toString)
    e.getChannel.close
  }

  //----------------------------------------------------------------------------

  private def processHumanGet(response: HttpResponse) {
    val content = index("x", "y", "")
    response.setContent(ChannelBuffers.copiedBuffer(content, CharsetUtil.UTF_8))
    response.setHeader(CONTENT_TYPE, "text/html")
  }

  private def processHumanPost(response: HttpResponse) {
    val content = index("x", "y", "z")
    response.setContent(ChannelBuffers.copiedBuffer(content, CharsetUtil.UTF_8))
    response.setHeader(CONTENT_TYPE, "text/html")
  }

  private def index(title: String, url: String, result: String) =
    <html>
      <head>
        <title>{title}</title>
      </head>

      <body>
        <form method="post" action="/">
          <label>URL:</label>
          <input type="text" name="url" value={url} size="83" />
          <input type="submit" value="OK" />
        </form>

        <h1>{result}</h1>

      </body>
    </html>.toString

  //----------------------------------------------------------------------------

  private def processApiPost(qd: QueryStringDecoder, response: HttpResponse) {
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

  private def processApiGet(uri: String, response: HttpResponse) {
    val key = uri.substring(1)  // Skip "/" prefix
    db.getUrl(key) match {
      case Some(url) =>
        response.setStatus(MOVED_PERMANENTLY)
        response.setHeader(LOCATION, url)
      case None =>
        response.setStatus(NOT_FOUND)
    }
  }

  //----------------------------------------------------------------------------

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
}
