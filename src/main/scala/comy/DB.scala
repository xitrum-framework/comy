package comy

import java.util.{Date, Calendar}
import java.text.SimpleDateFormat

import scala.collection.mutable.MutableList

import org.apache.cassandra.thrift._

import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.transport.{TSocket, TTransport}

class DB(config: Config) {
  private val KEY_CF              = "Key"
  private val URL_CF              = "URL"
  private val LAST_ACCESS_DATE_CF = "LastAccessDate"

  private val CF_KEY_URL              = "URL"
  private val CF_KEY_CREATED_DATE     = "CreatedDate"
  private val CF_KEY_LAST_ACCESS_DATE = "LastAccessDate"
  private val CF_KEY_COUNTER          = "Counter"

  private val CF_URL_KEY = "Key"

  private var tr: TTransport = null

  private var client: Cassandra.Client = _

  openConnection

  //----------------------------------------------------------------------------

  def save(url: String): String = {
    val existedShortUrl = getKey(url)
    var key: String = ""
    if (existedShortUrl == null) {	//not existed
      var doGenarate = true
      while (doGenarate) {
        key = generateKey
        if (!keyExists(key)) {
          addUrl(key, url)
          doGenarate = false
        }
      }
    } else {	//existed
      key = existedShortUrl
    }
    key
  }

  def getUrl(key: String): String = {
    val url = getColumnValue(KEY_CF, key, CF_KEY_URL)

    // TODO: update last access date & counter
    if (url != null) {
      updateLastAccess(url)
    }

    url
  }

  def removeExpiredUrls {
    val expirationDate = getExpirationDateString(config.dbExpirationDays)

    val urls = getAllURLAccessOnDate(expirationDate)
    urls.foreach(url => {
      val longUrl: String = getUrl(url)
      removeRow(KEY_CF, url)
      removeRow(URL_CF, longUrl)
      removeRow(LAST_ACCESS_DATE_CF, expirationDate)
    })
  }

  //----------------------------------------------------------------------------

  private def openConnection {
    tr = new TSocket(config.dbHost, config.dbPort)
    val proto = new TBinaryProtocol(tr)
    client = new Cassandra.Client(proto)
    tr.open
  }

  private def closeConnection {
    tr.flush
    tr.close
  }

  private def addUrl(key: String, url: String) {
    val timestamp = System.currentTimeMillis
    val colPathName = new ColumnPath(KEY_CF)

    colPathName.setColumn(CF_KEY_URL.getBytes("UTF8"))
    client.insert(
      config.dbKeyspace, key, colPathName, url.getBytes("UTF8"),
      timestamp, ConsistencyLevel.ALL)

    colPathName.setColumn(CF_KEY_CREATED_DATE.getBytes("UTF8"))
    client.insert(
      config.dbKeyspace, key, colPathName, formatDate(new Date()).getBytes("UTF8"),
      timestamp, ConsistencyLevel.ALL)

    colPathName.setColumn(CF_KEY_LAST_ACCESS_DATE.getBytes("UTF8"))
    client.insert(
      config.dbKeyspace, key, colPathName, "".getBytes("UTF8"),
      timestamp, ConsistencyLevel.ALL)

    colPathName.setColumn(CF_KEY_COUNTER.getBytes("UTF8"))
    client.insert(config.dbKeyspace, key, colPathName, "0".getBytes("UTF8"),
      timestamp, ConsistencyLevel.ALL)

    addUrl(url, key)
  }

  private def addLongURL(longURL: String,  shortenURL: String) {
    val timestamp = System.currentTimeMillis()
    val colPathName = new ColumnPath(URL_CF)
    val date = new Date()
    colPathName.setColumn(CF_URL_KEY.getBytes("UTF8"))
    client.insert(
      config.dbKeyspace, longURL, colPathName, shortenURL.getBytes("UTF8"),
      timestamp, ConsistencyLevel.ALL)
  }

  private def removeRow(columnFamily: String, key: String) {
    val columnPath: ColumnPath = new ColumnPath(columnFamily)
    client.remove(config.dbKeyspace, key, columnPath, System.currentTimeMillis(), ConsistencyLevel.ALL)
  }

  private def removeColumn(columnFamily: String, key: String, columnName: String) {
    val columnPath: ColumnPath = new ColumnPath(columnFamily)
    columnPath.setColumn(columnName.getBytes("UTF8"));
    client.remove(config.dbKeyspace, key, columnPath, System.currentTimeMillis(), ConsistencyLevel.ALL)
  }

  private def getKey(url: String): String =
    getColumnValue(URL_CF, url, CF_URL_KEY)

  def keyExists(key: String) = (getUrl(key) != null)

  private def updateLastAccess(key: String) {
     val oldLastAccess = getColumnValue(KEY_CF, key, CF_KEY_LAST_ACCESS_DATE)
     val currentDate = formatDate(new Date())
     if (!oldLastAccess.equals(currentDate)) {
       removeColumn(LAST_ACCESS_DATE_CF, oldLastAccess, key)
       addAccessTime(currentDate, key)
     }

     val counter = getColumnValue(KEY_CF, key, CF_KEY_COUNTER).toInt + 1
     val timestamp = System.currentTimeMillis
     val colPathName = new ColumnPath(KEY_CF)
     colPathName.setColumn(CF_KEY_LAST_ACCESS_DATE.getBytes("UTF8"))
     client.insert(
       config.dbKeyspace, key, colPathName, formatDate(new Date()).getBytes("UTF8"),
       timestamp, ConsistencyLevel.ALL)
     colPathName.setColumn(CF_KEY_COUNTER.getBytes("UTF8"))
     client.insert(
       config.dbKeyspace, key, colPathName, counter.toString.getBytes("UTF8"),
       timestamp, ConsistencyLevel.ALL)
  }

  private def getColumnValue(colFamily: String, key: String, colName: String): String = {
    val colPath: ColumnPath = new ColumnPath(colFamily)
    colPath.setColumn(colName.getBytes("UTF8"))

    val colLongUrlCol = client.get(
      config.dbKeyspace, key, colPath,
      ConsistencyLevel.ONE).getColumn
      new String(colLongUrlCol.value)
  }

  private def addAccessTime(accessTime: String, shortenURl: String) {
    val timestamp = System.currentTimeMillis()
    val colPathName = new ColumnPath(LAST_ACCESS_DATE_CF)
    colPathName.setColumn(shortenURl.getBytes("UTF8"))
    client.insert(
      config.dbKeyspace, accessTime, colPathName, "".getBytes("UTF8"),
      timestamp, ConsistencyLevel.ALL)
  }

  private def getAllURLAccessOnDate(date: String): List[String] = {
    val predicate:SlicePredicate = new SlicePredicate
    val sliceRange:SliceRange = new SliceRange(){}
    sliceRange.setStart(new Array[Byte](0));
    sliceRange.setFinish(new Array[Byte](0));
    predicate.setSlice_range(sliceRange);

    val parent = new ColumnParent(LAST_ACCESS_DATE_CF);
    val results = client.get_slice(config.dbKeyspace, date, parent, predicate, ConsistencyLevel.ONE);
    var urls: List[String] = List()
    for (i <- 0 to (results.size - 1)) {
      val result = results.get(i)
      val column:Column = result.column
      urls = (new String(column.name)) :: urls
    }
    urls
  }

  //----------------------------------------------------------------------------

  private def generateKey: String = {
    val codes = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890"
   var key: String = ""
   for (count <- 0 to 6){
     val randomIndex = scala.math.floor(scala.math.random * 62).toInt
     key += codes(randomIndex)
   }
   key
  }

  private def getLongUrl(uri: String): String = {
    if (uri.length > 5 && uri.indexOf("/url=") == 0) {
     var url:String = uri.substring(5)
     if (!url.contains("://")) {
       url = "http://" + url
     }
      return url
    } else return null
  }

  private def formatDate(date: Date): String = {
    if (date == null) {
      null
    } else {
      val dateFormat = new SimpleDateFormat("yyyyMMdd")
      dateFormat.format(date)
    }
  }

  private def getExpirationDateString(expirationDays: Int): String = {
    val now = new Date
    val cal = Calendar.getInstance
    cal.setTime(now)
    cal.add(Calendar.DATE, -expirationDays)
    formatDate(cal.getTime)
  }
}
