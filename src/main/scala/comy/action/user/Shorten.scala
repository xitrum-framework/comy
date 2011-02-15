package comy.action.user

import xitrum._
import comy.model.{DB, SaveUrlResult}

@POST2("/")
class Shorten extends Action {
  def execute {
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
