package comy.http.action

import org.jboss.netty.handler.codec.http._
import HttpMethod._

/**
 * Caveat:
 * POST request content is not parsed, thus POST parameters are not supported.
 */
abstract class Abstract(request: HttpRequest, response: HttpResponse) {
  protected val method = request.getMethod

  protected val uri  = request.getUri
  protected val qd   = new QueryStringDecoder(uri)
  protected val path = qd.getPath

  def execute
}
