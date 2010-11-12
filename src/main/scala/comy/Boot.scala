package comy

import xt.server.Server
import xt.vc.Router

import comy.controller._

object Boot {
  def main(args: Array[String]) {
    // Avoid error when creating QRCode when running in console-only environment
    System.setProperty("java.awt.headless", "true")

    val s = new Server
    s.start
  }
}
