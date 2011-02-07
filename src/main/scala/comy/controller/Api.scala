package comy.controller

import xt._

import org.jboss.netty.handler.codec.http._
import HttpHeaders.Names._
import HttpResponseStatus._

import comy.Config
import comy.model.{DB, QRCode, SaveUrlResult}
import comy.view.api._

class Api extends Application {
  beforeFilter("checkIpForShorten", Except("lengthen"))

  @GET
  @Path("/")
  def index {
    renderView(Index)
  }

  @POST
  @Path("/api/shorten")  // ?url=URL[&key=KEY]
  def shorten {
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

  /** See: http://www.hascode.com/2010/05/playing-around-with-qr-codes/ */
  @GET
  @Path("/api/qrcode")  // ?url=xxx
  def qrcode {
    val url   = param("url")
    val bytes = QRCode.render(url)
    response.setHeader(CONTENT_TYPE, "image/png")
    renderBinary(bytes)
  }

  @GET
  @Path(value="/:key", last=true)
  def lengthen {
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

  //----------------------------------------------------------------------------

  protected def checkIpForShorten = {
    if (Config.isApiAllowed(remoteIp)) {
      true
    } else {
      response.setStatus(FORBIDDEN)
      false
    }
  }
}
