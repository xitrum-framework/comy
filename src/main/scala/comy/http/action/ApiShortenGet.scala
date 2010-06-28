package comy.http.action

import org.jboss.netty.handler.codec.http._
import HttpHeaders.Names._
import HttpResponseStatus._
import org.jboss.netty.buffer._

import comy.DB

/**
 * Mounted at /key
 */
class ApiShortenGet(request: HttpRequest, response: HttpResponse, db: DB) extends Abstract(request, response) {
  def execute {
    val key = path.substring(1)  // Skip "/" prefix
    db.getUrl(key) match {
      case Some(url) =>
        // Use 302 instead of 301 because:
        // * Some KDDI AU mobiles display annoying dialog for 301
        // * Not all browsers support HTTP/1.1
        response.setStatus(FOUND)
        response.setHeader(LOCATION, url)

      case None =>
        response.setStatus(NOT_FOUND)
    }
  }
}
