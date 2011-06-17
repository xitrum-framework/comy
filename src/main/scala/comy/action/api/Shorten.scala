package comy.action.api

import org.jboss.netty.handler.codec.http.HttpResponseStatus._

import xitrum.Action
import xitrum.annotation.POST

import comy.{Config => ComyConfig}
import comy.model.{DB, SaveUrlResult}

@POST("/api/shorten")  // ?url=URL[&key=KEY]
class Shorten extends Action {
  beforeFilters("checkIpForShorten") = () => {
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
    val (resultCode, resultString) = DB.saveUrl(url, keyo)

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
