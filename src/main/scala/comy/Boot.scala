package comy

import xt.server.Server

object Boot {
  def main(args: Array[String]) {
    // Avoid error when creating QRCode when running in console-only environment
    System.setProperty("java.awt.headless", "true")

    val s = new Server
    s.start
  }
}
