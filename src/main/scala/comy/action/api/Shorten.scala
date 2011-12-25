package comy.action.api

import io.netty.handler.codec.http.HttpResponseStatus._

import xitrum.Action
import xitrum.annotation.POST

import comy.action.SetLanguage
import comy.{Config => ComyConfig}
import comy.model.{DB, SaveUrlResult}

@POST("/api/shorten")  // ?url=URL[&key=KEY]
class Shorten extends Action with SetLanguage {
  beforeFilter { () =>
    if (ComyConfig.isApiAllowed(remoteIp)) {
      true
    } else {
      response.setStatus(FORBIDDEN)
      false
    }
  }

  override def execute {
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
    renderText(resultString)
  }
}
