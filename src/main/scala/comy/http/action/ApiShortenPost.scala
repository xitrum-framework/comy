package comy.http.action

import org.jboss.netty.handler.codec.http._
import HttpHeaders.Names._
import HttpResponseStatus._
import org.jboss.netty.buffer._
import org.jboss.netty.util.CharsetUtil

import comy.{DB, SaveUrlResult}

/**
 * Mounted at /api/shorten?url=URL[&key=KEY]
 */
class ApiShortenPost(request: HttpRequest, response: HttpResponse, db: DB) extends Abstract(request, response) {
  def execute {
    val urls = qd.getParameters.get("url")
    if (urls == null) {
      response.setStatus(BAD_REQUEST)
      response.setContent(ChannelBuffers.copiedBuffer("url parameter must be specified", CharsetUtil.UTF_8))
    } else {
      val url  = urls.get(0)
      val keys = qd.getParameters.get("key")
      val key  = if (keys != null) Some(keys.get(0)) else None
      val (resultCode, resultString) = db.saveUrl(url, key)

      val status = resultCode match {
        case SaveUrlResult.VALID     => OK
        case SaveUrlResult.INVALID   => BAD_REQUEST
        case SaveUrlResult.DUPLICATE => CONFLICT
        case SaveUrlResult.ERROR     => INTERNAL_SERVER_ERROR
      }
      response.setStatus(status)

      response.setContent(ChannelBuffers.copiedBuffer(resultString, CharsetUtil.UTF_8))
    }
  }
}
