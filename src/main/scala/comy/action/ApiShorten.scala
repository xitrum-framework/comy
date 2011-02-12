package comy.action

import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import xt._

import comy.{Config => ComyConfig}
import comy.model.{DB, SaveUrlResult}

class ApiShorten extends Action {
  beforeFilters("checkIpForShorten") = () => {
    if (ComyConfig.isApiAllowed(remoteIp)) {
      true
    } else {
      response.setStatus(FORBIDDEN)
      false
    }
  }

  @POST("/api/shorten")  // ?url=URL[&key=KEY]
  def execute {
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