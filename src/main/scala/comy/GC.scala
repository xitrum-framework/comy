package comy

import xitrum.Log
import comy.model.DB

/**
 * This Gabage Collector should be run periodically to remove old (expired) URL
 * entries.
 */
object GC extends Log {
  def main(args: Array[String]): Unit = {
    log.info("GC started")
    DB.removeExpiredUrls()
    log.info("GC stopped")
  }
}
