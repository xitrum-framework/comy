package comy.action.user

import xitrum.Action

import comy.action.api.Lengthen
import comy.model.{DB, SaveUrlResult}

class Shorten extends Action {
  override def postback {
    val url = param("url").trim
    if (url.isEmpty) {
      jsRender(js$id("result") + ".html(" + jsEscape(<p class="error">URL must not be empty</p>) + ")")
      return
    }

    val keyo = {
      val ret = param("key").trim
      if (ret.isEmpty) None else Some(ret)
    }

    val (resultCode, resultString) = DB.saveUrl(url, keyo)

    resultCode match {
      case SaveUrlResult.VALID =>
        val absoluteUrl = absoluteUrlFor[Lengthen]("key" -> resultString)
        jsRender(js$id("result") + ".html(" +
          jsEscape(<xml:group>
            <hr />
            {absoluteUrl}<br />
            <a href={absoluteUrl} target="_blank"><img src={urlFor[QRCode]("url" -> absoluteUrl)} /></a>
          </xml:group>) + ")")

      case SaveUrlResult.INVALID =>
        jsRender(js$id("result") + ".html(" + jsEscape(<p class="error">{resultString}</p>) + ")")

      case SaveUrlResult.DUPLICATE =>
        jsRender(js$id("result") + ".html(" + jsEscape(<p class="error">Key has been chosen</p>) + ")")

      case SaveUrlResult.ERROR =>
        jsRender(js$id("result") + ".html(" + jsEscape(<p class="error">Server error</p>) + ")")
    }
  }
}
