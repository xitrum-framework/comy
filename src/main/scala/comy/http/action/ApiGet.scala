package comy.http.action

import org.jboss.netty.handler.codec.http._
import HttpHeaders.Names._
import HttpResponseStatus._
import org.jboss.netty.buffer._

import comy.DB

class ApiGet(request: HttpRequest, response: HttpResponse, db: DB) extends Abstract(request, response) {
  def execute {
    val key = path.substring(1)  // Skip "/" prefix
    db.getUrl(key) match {
      case Some(url) =>
        response.setStatus(MOVED_PERMANENTLY)
        response.setHeader(LOCATION, url)

      case None =>
        response.setStatus(NOT_FOUND)
    }
  }
}
