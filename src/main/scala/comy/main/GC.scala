package comy.main

import comy._

/**
 * This Gabage Collector should be run periodically to remove old (expired) URL
 * entries.
 */
object GC extends Logger {
  def main(args: Array[String]) {
    val configPath = args(0)
    val config = new Config(configPath)
    setLogPath(config.logFile)

    info("GC started")
    val db = new DB(config)
    db.removeExpiredUrls
    info("GC stopped")
  }
}
