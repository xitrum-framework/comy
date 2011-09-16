package comy.action.api

import org.jboss.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND

import xitrum.Action
import xitrum.annotation.{GET, Last}

import comy.action.SetLanguage
import comy.model.DB

@Last
@GET("/:key")
class Lengthen extends Action with SetLanguage {
  override def execute {
    val key = param("key")
    DB.getUrl(key) match {
      case Some(url) =>
        // Some KDDI AU mobiles display annoying dialog for 301
        redirectTo(url)

      case None =>
        response.setStatus(NOT_FOUND)
        renderText(t("URL Not Found"))
    }
  }
}
