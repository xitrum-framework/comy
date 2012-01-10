package comy.controller

import io.netty.handler.codec.http.HttpResponseStatus._

import comy.{Config => ComyConfig}
import comy.model.{DB, SaveUrlResult}

object Api extends Api

class Api extends SetLanguage {
  def lengthen = last.GET(":key") {
    val key = param("key")
    DB.getUrl(key) match {
      case Some(url) =>
        // Some KDDI AU mobiles display annoying dialog for 301
        redirectTo(url)

      case None =>
        response.setStatus(NOT_FOUND)
        respondText(t("URL Not Found"))
    }
  }

  def shorten = POST("api/shorten") {  // ?url=URL[&key=KEY]
//  beforeFilter { () =>
//    if (ComyConfig.isApiAllowed(remoteIp)) {
//      true
//    } else {
//      response.setStatus(FORBIDDEN)
//      false
//    }
//  }

    val url  = param("url")
    val keyo = paramo("key")
    val (resultCode, resultString) = DB.saveUrl(this, url, keyo)

    val status = resultCode match {
      case SaveUrlResult.VALID     => OK
      case SaveUrlResult.INVALID   => BAD_REQUEST
      case SaveUrlResult.DUPLICATE => CONFLICT
      case SaveUrlResult.ERROR     => INTERNAL_SERVER_ERROR
    }
    response.setStatus(status)
    respondText(resultString)
  }
}
