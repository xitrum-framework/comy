package comy.action

import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import xitrum.annotation.{GET, POST, Last}

import comy.{Config => ComyConfig}
import comy.model.{DB, SaveUrlResult}

@Last
@GET(":key")
class ApiLengthen extends SetLanguage {
  def execute() {
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
}

@POST("api/shorten")  // ?url=URL[&key=KEY]
class ApiShorten extends SetLanguage {
//  beforeFilter { () =>
//    if (ComyConfig.isApiAllowed(remoteIp)) {
//      true
//    } else {
//      response.setStatus(FORBIDDEN)
//      false
//    }
//  }

  def execute() {  
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
