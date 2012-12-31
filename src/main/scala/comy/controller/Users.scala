package comy.controller

import org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE
import xitrum.validator.{Required, MaxLength, URL}

import comy.model.{DB, SaveUrlResult, QRCode}

object Users extends Users

class Users extends AppController {
  def index = GET {
    respondView()
  }

  def qrcode = GET("user/qrcode") {  // ?url=xxx
    // See: http://www.hascode.com/2010/05/playing-around-with-qr-codes/
    val url   = param("url")
    val bytes = QRCode.render(url)
    response.setHeader(CONTENT_TYPE, "image/png")
    respondBinary(bytes)
  }

  def shorten = POST("/user/shorten") {
    val url = param("url").trim
    if (url.isEmpty) {
      jsRespond("$('#result').html('%s')".format(jsEscape(<p class="error">{t("URL must not be empty")}</p>)))
    } else {
      val keyo = {
        val ret = param("key").trim
        if (ret.isEmpty) None else Some(ret)
      }
      val (resultCode, resultString) = DB.saveUrl(this, url, keyo)
      val html/*: scala.xml.Node*/ = resultCode match {
        case SaveUrlResult.VALID =>
          val absoluteUrl = Api.lengthen.absoluteUrl("key" -> resultString)
          <xml:group>
            <hr />
            {absoluteUrl}<br />
            <a href={absoluteUrl} target="_blank"><img src={qrcode.url("url" -> absoluteUrl)} /></a>
          </xml:group>

        case SaveUrlResult.INVALID =>
          <p class="error">{resultString}</p>

        case SaveUrlResult.DUPLICATE =>
          <p class="error">{t("Key has been chosen")}</p>

        case SaveUrlResult.ERROR =>
          <p class="error">{t("Server error")}</p>
      }
      jsRespond("$('#result').html('%s')".format(jsEscape(html)))
    }
  }
}
