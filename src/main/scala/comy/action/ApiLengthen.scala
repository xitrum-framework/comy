package comy.action

import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import xt._

import comy.model.DB

class ApiLengthen extends Action {
  @GET(value="/:key", last=true)
  def execute {
    val key = param("key")
    DB.getUrl(key) match {
      case Some(url) =>
        // Some KDDI AU mobiles display annoying dialog for 301
        redirectTo(url)

      case None =>
        response.setStatus(NOT_FOUND)
        renderText("Not Found")
    }
  }
}
