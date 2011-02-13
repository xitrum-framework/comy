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

        <input type="submit" value="Shorten" tabindex="3" />
      </form>

      <div id="result"></div>
    )
  }

  def postback {
    val url = param("url").trim
    if (url.isEmpty) {
      renderJS(
        jsUpdate("result", <p class="error">URL must not be empty</p>),
        jsUpdate("qrcode", "")
      )
      return
    }

    val keyo = {
      val ret = param("key").trim
      if (ret.isEmpty) None else Some(ret)
    }

    val (resultCode, resultString) = DB.saveUrl(url, keyo)

    resultCode match {
      case SaveUrlResult.VALID =>
        val absoluteUrl = "http://localhost:8364/" + resultString
        renderUpdate("result",
          <div>
            <hr />
            <div>{absoluteUrl}</div>
            <a href={absoluteUrl} target="_blank"><img src={urlFor[UserQRCode]("url" -> absoluteUrl)} /></a>
          </div>
        )

      case SaveUrlResult.INVALID =>
        renderUpdate("result", <p class="error">{resultString}</p>)

      case SaveUrlResult.DUPLICATE =>
        renderUpdate("result", <p class="error">Key has been chosen</p>)

      case SaveUrlResult.ERROR =>
        renderUpdate("result", <p class="error">Server error</p>)
    }
  }
}
