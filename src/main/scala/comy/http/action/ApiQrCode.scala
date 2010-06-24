package comy.http.action

import org.jboss.netty.handler.codec.http._
import HttpHeaders.Names._
import org.jboss.netty.buffer._

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.ByteMatrix
import com.google.zxing.qrcode.QRCodeWriter

import javax.imageio.ImageIO
import java.io.ByteArrayOutputStream

object ApiQrCode {
  val WIDTH  = 150
  val HEIGHT = 150
}

/**
 * See: http://www.hascode.com/2010/05/playing-around-with-qr-codes/
 *
 * This action should is mounted at /api/qrcode?url=xxx
 */
class ApiQrCode(request: HttpRequest, response: HttpResponse) extends Abstract(request, response) {
  import ApiQrCode._

  def execute {
    val url    = qd.getParameters.get("url").get(0)
    val writer = new QRCodeWriter
    val mtx    = writer.encode(url, BarcodeFormat.QR_CODE, WIDTH, HEIGHT)
    invertImage(mtx)
    val image  = MatrixToImageWriter.toBufferedImage(mtx)

    val baos = new ByteArrayOutputStream
    ImageIO.write(image, "png", baos)
    response.setContent(ChannelBuffers.copiedBuffer(baos.toByteArray))
    response.setHeader(CONTENT_TYPE, "image/png")
  }

  private def invertImage(mtx: ByteMatrix) {
    for (w <- 0 until mtx.getWidth; h <- 0 until mtx.getHeight) {
      val inverted = if (mtx.get(w, h) == 0x00) 0xFF else 0x00
      mtx.set(w, h, inverted)
    }
  }
}
