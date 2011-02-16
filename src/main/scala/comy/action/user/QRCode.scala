package comy.action.user

import org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE
import xitrum.action.Action
import xitrum.action.annotation.GET

import comy.action.Application
import comy.model.{QRCode => MQRCode}

@GET("/user/qrcode")  // ?url=xxx
class QRCode extends Action {
  /** See: http://www.hascode.com/2010/05/playing-around-with-qr-codes/ */
  def execute {
    val url   = param("url")
    val bytes = MQRCode.render(url)
    response.setHeader(CONTENT_TYPE, "image/png")
    renderBinary(bytes)
  }
}
