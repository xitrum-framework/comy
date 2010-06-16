package comy

import com.mongodb._
import java.util.Date

class DB(config: Config) extends Logger {
  private val KEY = "key"
  private val URL = "url"
  private val CREATED_AT = "created_at"
  private val LAST_ACCESS = "last_access"
  private val ACCESS_COUNTER = "access_counter"

	  
  val mongo: Mongo = new Mongo(config.dbHost, config.dbPort)
  val db: com.mongodb.DB = mongo.getDB(config.dbDB)
  val coll: DBCollection = db.getCollection("comy")
  /**
   * @return None if there is error (DB is down etc.)
   */
  def saveUrl(url: String): Option[String] = {
	try {
	  val existedKey = getKeyFromUrl(url)
	  if (existedKey == None) {
	 	var doGenarateKey: Boolean = true
	 	var key: String = ""
	    while(doGenarateKey) {
	      key = KeyGenerator.generateKey
	      if(getUrlFromKey(key, false) == None) {
	        addNewUrl(key, url)
	    	doGenarateKey = false
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
	  val expirationDate: String = Utils.getExpirationDateString(config.dbExpirationDays)
	  val query: BasicDBObject = new BasicDBObject(LAST_ACCESS, new BasicDBObject("$lte", expirationDate))
	  val result = coll.find(query)
	  while(result.hasNext()) {
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
   * @return None if URL is not existed.
   * Otherwise, return the associated Key
   */
  private def getKeyFromUrl(url: String): Option[String] = {
    val result: DBObject = coll.findOne(new BasicDBObject(URL, url))
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
    val result: DBObject = coll.findOne(new BasicDBObject(KEY, key))
    if (result != null) {
      if (updateAccess) {
        val resultUpdate = new BasicDBObject()
        resultUpdate.put(KEY, result.get(KEY))
	    resultUpdate.put(URL, result.get(URL))
	    resultUpdate.put(CREATED_AT, result.get(CREATED_AT))
	    resultUpdate.put(LAST_ACCESS, Utils.formatDate(new Date()))
	    resultUpdate.put(ACCESS_COUNTER, result.get(ACCESS_COUNTER).toString.toInt + 1)
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
  private def addNewUrl(key: String, url: String){
    val doc: BasicDBObject = new BasicDBObject()
    doc.put(KEY, key)
    doc.put(URL, url)
    doc.put(CREATED_AT, Utils.formatDate(new Date()))
    doc.put(LAST_ACCESS, Utils.formatDate(new Date()))
    doc.put(ACCESS_COUNTER, 0)
    coll.insert(doc)
  }

}
