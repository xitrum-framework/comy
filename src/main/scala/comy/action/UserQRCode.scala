package comy.action

import org.jboss.netty.handler.codec.http.HttpHeaders.Names._
import xt._

import comy.model.QRCode

class UserQRCode extends Action {
  /** See: http://www.hascode.com/2010/05/playing-around-with-qr-codes/ */
  @GET("/user/qrcode")  // ?url=xxx
  def execute {
    val url   = param("url")
    val bytes = QRCode.render(url)
    response.setHeader(CONTENT_TYPE, "image/png")
    renderBinary(bytes)
  }
}
