package comy.http.action

/**
 * Loads static files and caches them in memory.
 */
object StaticCache {
  private val cache = Map(
    "/"                              -> load("/static/index.html").get,
    "/admin"                         -> load("/static/admin.html").get,

    "/static/css/960.css"            -> load("/static/css/960.css").get,
    "/static/css/reset.css"          -> load("/static/css/reset.css").get,
    "/static/css/text.css"           -> load("/static/css/text.css").get,
    "/static/css/smart_corners.css"  -> load("/static/css/smart_corners.css").get,
    "/static/css/api.css"            -> load("/static/css/api.css").get,

    "/static/js/jquery-1.4.2.min.js" -> load("/static/js/jquery-1.4.2.min.js").get,
    "/static/js/api.js"              -> load("/static/js/api.js").get,
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
      val bytes = new Array[Byte](stream.available)
      stream.read(bytes, 0, stream.available)
      stream.close

      // NOTE: add more type when adding static files
      val contentType = path.split("\\.").last match {
        case "css"  => "text/css"
        case "js"   => "text/javascript"
        case "html" => "text/html"
        case "png"  => "image/png"
      }
      Some((bytes, contentType))
    }
  }
}
