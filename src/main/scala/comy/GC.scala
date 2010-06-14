package comy

/**
 * This Gabage Collector should be run periodically to remove old (expired) URL
 * entries.
 */
object GC {
  def main(args: Array[String]) {
    val configPath = args(1)
    val config = new Config(configPath)

    // FIXME: log to file
    println("GC started")
    val db = new DB(config)
    db.removeExpiredUrls
    println("GC stopped")
  }
}
