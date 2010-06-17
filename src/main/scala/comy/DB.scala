package comy

import java.util.{Date, Calendar}
import java.text.SimpleDateFormat

import com.mongodb._

object DB {
  val COLLECTION   = "comy"
  val KEY          = "key"
  val URL          = "url"
  val ACCESS_COUNT = "access_count"
  val CREATED_ON   = "created_on"
  val UPDATED_ON   = "updated_on"  // The date when ACCESS_COUNT is incremented
}

/**
 * See: http://www.mongodb.org/display/DOCS/Java+Language+Center
 * Only one instance of this class should be used for the whole application.
 */
class DB(config: Config) extends Logger {
  import DB._

  val left  = new ServerAddress(config.dbHostLeft,  config.dbPortLeft)
  val right = new ServerAddress(config.dbHostRight, config.dbPortRight)

  val options = new MongoOptions
  options.connectionsPerHost = config.dbConnectionsPerHost
  options.autoConnectRetry   = true

  val mongo = new Mongo(left, right, options)
  val db = mongo.getDB(config.dbName)
  val coll = db.getCollection(COLLECTION)

  /**
   * @return None if there is error (DB is down etc.)
   */
  def saveUrl(url: String): Option[String] = {
    try {
      val existedKey = getKeyFromUrl(url)
      if (existedKey == None) {
        var key = ""
        var keyDuplicated = true
        while (keyDuplicated) {
          key = KeyGenerator.generateKey
          if (getUrlFromKey(key, false) == None) {
            addNewUrl(key, url)
            keyDuplicated = false
          }
        }
        Some(key)
      } else {
        existedKey
      }
    } catch {
      case e: Exception =>
        error(e)
        None
    }
  }

  /**
   * @return None if there is error (DB is down etc.)
   */
  def getUrl(key: String): Option[String] = {
    try {
      getUrlFromKey(key, true)
    } catch {
      case e: Exception =>
        error(e)
        None
    }
  }

  /**
   * @return false if there is error (DB is down etc.)
   */
  def removeExpiredUrls: Boolean = {
    try {
      val expirationDate = getFormattedExpirationDate(config.dbExpirationDays)
      val query = new BasicDBObject(UPDATED_ON, new BasicDBObject("$lte", expirationDate))
      val result = coll.find(query)
      while (result.hasNext) {
        coll.remove(result.next)
      }
      true
    } catch {
      case e: Exception =>
        error(e)
        false
    }
  }

  /**
   * @return None if URL is not existed, or otherwise the associated key
   */
  private def getKeyFromUrl(url: String): Option[String] = {
    val result = coll.findOne(new BasicDBObject(URL, url))
    if (result != null) {
      Some(result.get(KEY).toString)
    } else {
      None
    }
  }

  /**
   * @return None if Key is not existed.
   * Otherwise, return the associated URL
   * Also update last_access and access_counter if specified
   */
  private def getUrlFromKey(key: String, updateAccess: Boolean): Option[String] = {
    val result = coll.findOne(new BasicDBObject(KEY, key))
    if (result != null) {
      if (updateAccess) {
        val resultUpdate = new BasicDBObject
        resultUpdate.put(KEY,          result.get(KEY))
        resultUpdate.put(URL,          result.get(URL))
        resultUpdate.put(ACCESS_COUNT, result.get(ACCESS_COUNT).toString.toInt + 1)
        resultUpdate.put(CREATED_ON,   result.get(CREATED_ON))
        resultUpdate.put(UPDATED_ON,   formatDate(new Date))
        coll.update(result, resultUpdate)
      }
      Some(result.get(URL).toString)
    } else {
      None
    }
  }

  /**
   * Add a new URL to the database
   */
  private def addNewUrl(key: String, url: String) {
    val doc = new BasicDBObject
    val today = new Date
    doc.put(KEY,          key)
    doc.put(URL,          url)
    doc.put(ACCESS_COUNT, 0)
    doc.put(CREATED_ON,   formatDate(today))
    doc.put(UPDATED_ON,   formatDate(today))
    coll.insert(doc)
  }

  private def formatDate(date: Date): String = {
    val format = new SimpleDateFormat("yyyyMMdd")
    format.format(date)
  }

  private def getFormattedExpirationDate(days: Int): String = {
    val today = new Date
    val cal = Calendar.getInstance
    cal.setTime(today)
    cal.add(Calendar.DATE, -days)
    formatDate(cal.getTime)
  }
}
