package comy.action.user

import xitrum._
import xitrum.vc.validator._

import comy.action.Application
import comy.model.{DB, SaveUrlResult}

@GET("/")
class Index extends Application with Postback {
  def execute {
    renderView(
      <form postback="submit">
        <table>
          <tr>
            <td><label for="url">URL:</label></td>
            <td>{<input id="url" type="text" name="url" value="http://mobion.jp/" tabindex="1" />.validate(new Required, new URL)}</td>
          </tr>
          <tr>
            <td><label for="key">Key:</label></td>
            <td>
              {<input type="text" name="key" tabindex="2" />.validate(new KeyValidator, new MaxLength(32))}
              <span>(optional, a-z A-Z 0-9 _ -)</span>
            </td>
          </tr>
        </table>

        <input type="submit" value="Shorten" tabindex="3" />
      </form>

      <div id="result"></div>
    )
  }

  def postback {
    val url = param("url").trim
    if (url.isEmpty) {
      jsRenderUpdate("result", <p class="error">URL must not be empty</p>)
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
        jsRenderUpdate("result",
          <div>
            <hr />
            <div>{absoluteUrl}</div>
            <a href={absoluteUrl} target="_blank"><img src={urlFor[QRCode]("url" -> absoluteUrl)} /></a>
          </div>
        )

      case SaveUrlResult.INVALID =>
        jsRenderUpdate("result", <p class="error">{resultString}</p>)

      case SaveUrlResult.DUPLICATE =>
        jsRenderUpdate("result", <p class="error">Key has been chosen</p>)

      case SaveUrlResult.ERROR =>
        jsRenderUpdate("result", <p class="error">Server error</p>)
    }
  }
}
