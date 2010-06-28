package comy.main

import comy._
import comy.http.Server

object Http extends Logger {
  def main(args: Array[String]) {
    val configPath = args(0)
    val config = new Config(configPath)
    setLogPath(config.logFile)
    Server.start(config)
    info("HTTP started")
  }
}
