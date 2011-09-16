package comy.model

import java.util.{ArrayList, Date, Calendar}
import java.text.SimpleDateFormat

import com.mongodb._
import xitrum.{I18n, Logger}

import comy.Config

object DBUrlColl {
  val COLL         = "urls"
  val KEY          = "key"
  val URL          = "url"
  val ACCESS_COUNT = "access_count"

  // The difference, measured in DAYS, between today and January 1, 1970 UTC.
  val CREATED_ON   = "created_on"
  val UPDATED_ON   = "updated_on"  // The date when ACCESS_COUNT is incremented
}

// TODO: for storing API IPs
object DBApiIpColl {
  val COLL = "api_ips"
}

// TODO: for storing admin IPs
object DBAdminColl {
  val COLL = "admin_ips"
}

object SaveUrlResult extends Enumeration {
  type SaveUrlResult = Value
  val VALID     = Value
  val INVALID   = Value
  val DUPLICATE = Value
  val ERROR     = Value
}

/**
 * See: http://www.mongodb.org/display/DOCS/Java+Language+Center
 * Only one instance of this class should be used for the whole application.
 */
object DB extends Logger {
  import DBUrlColl._
  import SaveUrlResult._

  private val coll  = {
    val addrs = new ArrayList[ServerAddress]
    for (addr <- Config.dbAddrs) addrs.add(new ServerAddress(addr))

    val options = new MongoOptions
    options.connectionsPerHost = Config.dbConnectionsPerHost
    options.autoConnectRetry   = true

    val mongo = new Mongo(addrs, options)
    val db    = mongo.getDB(Config.dbName)

    db.getCollection(COLL)
  }

  ensureIndexes

  def saveUrl(i18n: I18n, url: String, key: Option[String]) = key match {
    case Some(key2) => saveUrlWithKey(i18n, url, key2)
    case None       => saveUrlWithRandomKey(url)
  }

  /**
   * @return None if there is error (DB is down etc.)
   */
  def getUrl(key: String): Option[String] = {
    try {
      getUrlFromKey(key, true)
    } catch {
      case e: Exception =>
        logger.error("getUrl", e)
        None
    }
  }

  /**
   * Removes all URLs that have not been accessed within the last number of days.
   *
   * @return false if there is error (DB is down etc.)
   */
  def removeExpiredUrls: Boolean = {
    try {
      val expirationDate = today - Config.dbExpirationDays
      val query = new BasicDBObject
      query.put(ACCESS_COUNT, 0)
      query.put(UPDATED_ON,   new BasicDBObject("$lte", expirationDate))
      val result = coll.find(query)
      while (result.hasNext) {
        coll.remove(result.next)
      }
      true
    } catch {
      case e: Exception =>
        logger.error("removeExpiredUrls", e)
        false
    }
  }

  //----------------------------------------------------------------------------

  private def ensureIndexes {
    // Index each column separately (3 indexes in total) because we will search
    // based on each one separately
    coll.ensureIndex(new BasicDBObject(KEY,        1))
    coll.ensureIndex(new BasicDBObject(URL,        1))
    coll.ensureIndex(new BasicDBObject(UPDATED_ON, 1))
  }

    /**
   * @return None if there is error (DB is down etc.)
   */
  private def saveUrlWithKey(i18n: I18n, url: String, key: String): (SaveUrlResult, String) = {
    val (result, msg) = validateKey(i18n, key)
    if (!result) {
      (SaveUrlResult.INVALID, msg)
    } else {
      try {
        getUrlFromKey(key, false) match {
          case None =>
            addNewUrl(key, url)
            (SaveUrlResult.VALID, key)

          case Some(url2) =>
            if (url2 == url)
              (SaveUrlResult.VALID, key)
            else
              (SaveUrlResult.DUPLICATE, i18n.t("The key has been chosen"))
        }
      } catch {
        case e: Exception =>
          logger.error("saveUrlWithKey", e)
          (SaveUrlResult.ERROR, i18n.t("Server error"))
      }
    }
  }

  private def validateKey(i18n: I18n, key: String): (Boolean, String) = {
    val ALLOW_CHARS = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890_-"
    val length = key.length
    if (length == 0) {
       (false, i18n.t("Key must not be blank"))
    } else if (length > 32) {
       (false, i18n.t("Key must not be longer than 32 characters"))
    } else {
      val invalidCharIncluded = key.exists(c => ALLOW_CHARS.indexOf(c) == -1)
      if (invalidCharIncluded)
        (false, i18n.t("Key contains invalid character"))
      else
        (true, "")
    }
  }

  /**
   * @return None if there is error (DB is down etc.)
   */
  private def saveUrlWithRandomKey(url: String): (SaveUrlResult, String) = {
    try {
      getKeyFromUrl(url) match {
        case Some(key) =>
          (SaveUrlResult.VALID, key)

        case None =>
          var key = ""
          var keyDuplicated = true
          while (keyDuplicated) {
            key = KeyGenerator.generateKey
            if (getUrlFromKey(key, false) == None) {
              addNewUrl(key, url)
              keyDuplicated = false
            }
          }
          (SaveUrlResult.VALID, key)
      }
    } catch {
      case e: Exception =>
        logger.error("saveUrlWithRandomKey", e)
        (SaveUrlResult.ERROR, "")
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
        // This may not be accurate when there are many concurrent requests to the same key!
        val resultUpdate = new BasicDBObject("$inc", new BasicDBObject(ACCESS_COUNT, 1))

        resultUpdate.append("$set", new BasicDBObject(UPDATED_ON, today))
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
    val d = today
    doc.put(KEY,          key)
    doc.put(URL,          url)
    doc.put(ACCESS_COUNT, 0)
    doc.put(CREATED_ON,   d)
    doc.put(UPDATED_ON,   d)
    coll.insert(doc)
  }

  private def today = (System.currentTimeMillis/1000/(24*60*60)).asInstanceOf[Int]
}
