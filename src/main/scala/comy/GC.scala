package comy

/**
 * This Gabage Collector should be run periodically to remove old (expired) URL
 * entries.
 */
object GC extends Logs {
  def start(config: Config) {
    info("GC started")
    val db = new DB(config)
    db.removeExpiredUrls
    info("GC stopped")
  }
}
