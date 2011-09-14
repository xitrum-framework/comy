package comy.action.user

import xitrum.Action

import comy.action.api.Lengthen
import comy.model.{DB, SaveUrlResult}

class Shorten extends Action {
  override def postback {
    val url = param("url").trim
    if (url.isEmpty) {
      jsRenderFormat("$('#result').html(%s)", jsEscape(<p class="error">{t("URL must not be empty")}</p>))
      return
    }

    val keyo = {
      val ret = param("key").trim
      if (ret.isEmpty) None else Some(ret)
    }
    val (resultCode, resultString) = DB.saveUrl(url, keyo)
    val html = resultCode match {
      case SaveUrlResult.VALID =>
        val absoluteUrl = absoluteUrlFor[Lengthen]("key" -> resultString)
        <xml:group>
          <hr />
          {absoluteUrl}<br />
          <a href={absoluteUrl} target="_blank"><img src={urlFor[QRCode]("url" -> absoluteUrl)} /></a>
        </xml:group>

      case SaveUrlResult.INVALID =>
        <p class="error">{resultString}</p>

      case SaveUrlResult.DUPLICATE =>
        <p class="error">{t("Key has been chosen")}</p>

      case SaveUrlResult.ERROR =>
        <p class="error">{t("Server error")}</p>
    }
    jsRenderFormat("$('#result').html(%s)", jsEscape(html))
  }
}
