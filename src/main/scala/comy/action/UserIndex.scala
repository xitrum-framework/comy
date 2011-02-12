package comy.action

import java.net.URLEncoder
import xt._

import comy.model.{DB, SaveUrlResult}

@GET("/")
class UserIndex extends Application with Postback {
  def execute {
    renderView(
      <form id="form" postback="submit">
        <label for="url">URL:</label>
        <input type="text" name="url" value="http://mobion.jp/" tabindex="1" />
        <br />

        <label for="key">Key:</label>
        <input type="text" name="key" tabindex="2" />
        <span>(optional, a-z A-Z 0-9 _ -)</span>
        <br />

        <input id="submit" type="submit" value="Shorten" tabindex="3" />
      </form>

      <hr />

      <label>Result:</label>
      <br />
      <span id="result"></span>
      <br />

      <div id="qrcode"></div>
    )
  }

  def postback {
    val url = param("url").trim
    if (url.isEmpty) {
      renderUpdate("result", "URL must not be empty")
    }

    val keyo = {
      val ret = param("key").trim
      if (ret.isEmpty) None else Some(ret)
    }

    val (resultCode, resultString) = DB.saveUrl(url, keyo)

    resultCode match {
      case SaveUrlResult.VALID     =>
        val absoluteUrl = "http://localhost:8364/" + resultString
        renderJS(
          jsUpdate("result", absoluteUrl),
          jsUpdate("qrcode", <a href={absoluteUrl} target="_blank"><img src={urlFor[UserQRCode]("url" -> absoluteUrl)} /></a>)
        )

      case SaveUrlResult.INVALID   =>
        renderJS(
          jsUpdate("result", "Bad request"),
          jsUpdate("qrcode", "")
        )

      case SaveUrlResult.DUPLICATE =>
        renderJS(
          jsUpdate("result", "Key has been chosen"),
          jsUpdate("qrcode", "")
        )

      case SaveUrlResult.ERROR     =>
        renderJS(
          jsUpdate("result", "Server error"),
          jsUpdate("qrcode", "")
        )
    }
  }
}
