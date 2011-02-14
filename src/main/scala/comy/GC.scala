package comy

import xitrum.Logger
import comy.model.DB

/**
 * This Gabage Collector should be run periodically to remove old (expired) URL
 * entries.
 */
object GC extends Logger {
  def main(args: Array[String]) {
    logger.info("GC started")
    DB.removeExpiredUrls
    logger.info("GC stopped")
  }
}
