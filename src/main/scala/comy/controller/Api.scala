package comy.controller

import xt._

import org.jboss.netty.handler.codec.http._
import HttpHeaders.Names._
import HttpResponseStatus._

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.ByteMatrix
import com.google.zxing.qrcode.QRCodeWriter

import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream

import comy.Config
import comy.model.{DB, SaveUrlResult}

object Api {
  val QR_CODE_WIDTH  = 150
  val QR_CODE_HEIGHT = 150
}

class Api extends Application {
  import Api._

  beforeFilter("checkIpForShorten", Except("lengthen"))

  @GET
  @Path("/")
  def index {
    renderLayout(
      <script type="text/javascript" src="/public/js/index.js"></script>

      <label for="url">URL:</label>
      <input id="url" type="text" value="http://mobion.jp/" tabindex="1" />
      <br />

      <label for="key">Key:</label>
      <input id="key" type="text" tabindex="2" />
      <span>(optional, a-z A-Z 0-9 _ -)</span>
      <br />

      <input id="submit" type="submit" value="Shorten" tabindex="3" />
      <br />
      <br />

      <hr />

      <label>Result:</label>
      <br />
      <span id="result"></span>
      <a id="open" href="" target="_blank">Open ↑</a>
      <br />

      <img id="qrcode" alt="QR code" width="150" height="150" src="" />)
  }

  @POST
  @Path("/api/shorten")  // ?url=URL[&key=KEY]
  def shorten {
    val url = param("url")

    val keyo = paramo("key")
    val (resultCode, resultString) = DB.saveUrl(url, keyo)

    val status = resultCode match {
      case SaveUrlResult.VALID     => OK
      case SaveUrlResult.INVALID   => BAD_REQUEST
      case SaveUrlResult.DUPLICATE => CONFLICT
      case SaveUrlResult.ERROR     => INTERNAL_SERVER_ERROR
    }
    response.setStatus(status)
    renderText(resultString, None)
  }

  /** See: http://www.hascode.com/2010/05/playing-around-with-qr-codes/ */
  @GET
  @Path("/api/qrcode")  // ?url=xxx
  def qrcode {
    val url = param("url")

    val writer = new QRCodeWriter
    val mtx    = writer.encode(url, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT)
    invertImage(mtx)
    val image  = MatrixToImageWriter.toBufferedImage(mtx)

    val baos = new ByteArrayOutputStream
    ImageIO.write(image, "png", baos)
    response.setHeader(CONTENT_TYPE, "image/png")
    renderBinary(baos.toByteArray)
  }

  @GET
  @Path(value="/:key", last=true)
  def lengthen {
    val key = param("key")
    DB.getUrl(key) match {
      case Some(url) =>
        // Use 302 instead of 301 because:
        // * Some KDDI AU mobiles display annoying dialog for 301
        // * Not all browsers support HTTP/1.1
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

  private def invertImage(mtx: ByteMatrix) {
    for (w <- 0 until mtx.getWidth; h <- 0 until mtx.getHeight) {
      val inverted = if (mtx.get(w, h) == 0x00) 0xFF else 0x00
      mtx.set(w, h, inverted)
    }
  }
}
