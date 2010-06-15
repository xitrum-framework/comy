package comy

class DB(config: Config) {
  /**
   * @return None if there is error (DB is down etc.)
   */
  def saveUrl(url: String): Option[String] = {
    None
  }

  /**
   * @return None if there is error (DB is down etc.)
   */
  def getUrl(key: String): Option[String] = {
    None
  }

  /**
   * @return false if there is error (DB is down etc.)
   */
  def removeExpiredUrls: Boolean = {
    false
  }
}
