package comy

import xt.server.Server
import xt.vc.Router
import xt.vc.helper.Scalate

import comy.controller._

object Boot {
  def main(args: Array[String]) {
    // Avoid error when creating QRCode when running in console-only environment
    System.setProperty("java.awt.headless", "true")

    val routes          = Admin.routes ++ Api.routes
    val controllerPaths = List("comy.controller")
    val viewPaths       = List("comy.view")

    Router(routes, controllerPaths)
    Scalate(viewPaths)

    val s = new Server
    s.start
  }
}
