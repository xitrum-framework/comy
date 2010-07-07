package comy.http.action

import java.net.URLConnection

/**
 * Loads static files and caches them in memory.
 */
object StaticCache {
  private val cache = Map(
    "/"                              -> load("/static/index.html").get,
    "/admin"                         -> load("/static/admin.html").get,

    "/static/css/960_24_col.css"     -> load("/static/css/960_24_col.css").get,
    "/static/css/reset.css"          -> load("/static/css/reset.css").get,
    "/static/css/text.css"           -> load("/static/css/text.css").get,
    "/static/css/smart_corners.css"  -> load("/static/css/smart_corners.css").get,
    "/static/css/index.css"          -> load("/static/css/index.css").get,

    "/static/js/jquery-1.4.2.min.js" -> load("/static/js/jquery-1.4.2.min.js").get,
    "/static/js/index.js"            -> load("/static/js/index.js").get,
    "/static/js/admin.js"            -> load("/static/js/admin.js").get,

    "/static/img/csc_tl.png"         -> load("/static/img/csc_tl.png").get,
    "/static/img/csc_tr.png"         -> load("/static/img/csc_tr.png").get,
    "/static/img/csc_bl.png"         -> load("/static/img/csc_bl.png").get,
    "/static/img/csc_br.png"         -> load("/static/img/csc_br.png").get
  )

  def apply(path: String) = cache.get(path)

  private def load(path: String): Option[(Array[Byte], String)] = {
    val klass = classOf[comy.http.FrontController]
    val stream = klass.getResourceAsStream(path)
    if (stream == null) {
      None
    } else {
      val available = stream.available
      val bytes = new Array[Byte](available)

      // Pitfall: All available bytes are not always returned in one read!
      var totalRead = 0
      var thisRead  = 0
      while (totalRead < available) {
        thisRead = stream.read(bytes, totalRead, available - totalRead)
        totalRead += thisRead
      }

      stream.close

      val contentType = URLConnection.guessContentTypeFromName(path)
      Some((bytes, contentType))
    }
  }
}
