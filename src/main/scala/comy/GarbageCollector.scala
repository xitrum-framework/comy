package com.gnt.shortenurl

object GarbageCollector {
  def main(args: Array[String]) {
    val config = Utils.getConfig
    if(config == null) {
      println("Not found or invalid config file.")
    } else {
      println("Removing expired URLs...")
      val expirationDate:String = Utils.getExpirationDateString(
        config.getProperty(Utils.LINK_EXPIRATION_DATE).toInt)
      val shortUrlCF = config.getProperty(Utils.DB_SHORT_URL_CF)
      val longUrlCF = config.getProperty(Utils.DB_LONG_URL_CF)
      val accessTimeCF = config.getProperty(Utils.DB_ACCESS_TIME_CF)
      val db:DatabaseConnector = new DatabaseConnector(config)
    val urlList: List[String] = db.getAllURLAccessOnDate(expirationDate)
    urlList.foreach(url => {
      val longUrl: String = db.getLongURL(url)
      db.removeRow(shortUrlCF, url)
      db.removeRow(longUrlCF, longUrl)
      db.removeRow(accessTimeCF, expirationDate)
    })
    println("Finished!")
  }
  }
}
