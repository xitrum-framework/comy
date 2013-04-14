package comy

import xitrum.Server

object Boot {
  def main(args: Array[String]) {
    // Avoid error when creating QRCode when running in console-only environment
    System.setProperty("java.awt.headless", "true")

    Server.start()
  }
}
