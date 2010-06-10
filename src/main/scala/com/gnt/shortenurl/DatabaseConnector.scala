package com.gnt.shortenurl

import scala.List
import java.util.Date
import scala.collection.mutable
import scala.collection.mutable.MutableList
import org.apache.cassandra.thrift._
import org.apache.cassandra.thrift.Cassandra
import org.apache.cassandra.thrift.Column
import org.apache.cassandra.thrift.ColumnOrSuperColumn
import org.apache.cassandra.thrift.ColumnParent
import org.apache.cassandra.thrift.ColumnPath
import org.apache.cassandra.thrift.ConsistencyLevel
import org.apache.cassandra.thrift.InvalidRequestException
import org.apache.cassandra.thrift.NotFoundException
import org.apache.cassandra.thrift.SlicePredicate
import org.apache.cassandra.thrift.SliceRange
import org.apache.cassandra.thrift.TimedOutException
import org.apache.cassandra.thrift.UnavailableException
import org.apache.thrift.TException
import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.protocol.TProtocol
import org.apache.thrift.transport.TSocket
import org.apache.thrift.transport.TTransport

import java.util.Properties

class DatabaseConnector(config:Properties) {
 
  private var keySpace: String = ""
  private var shortUrlCF: String = ""
  private var longUrlCF: String = ""
  private var accessTimeCF: String = ""  
  private var tr: TTransport = null
  
  private val CF_SHORTURL_LONG_URL = "LongURL"
  private val CF_SHORTURL_CREATED_DATE = "CreatedDate"
  private val CF_SHORTURL_LAST_ACCESS_DATE = "LastAccessDate"
  private val CF_SHORTURL_COUNTER = "Counter"
	  
  private val CF_LONGURL_SHORT_URL = "ShortURL"
  
  private var client: Cassandra.Client = _

  openConnection
  
  def openConnection() {
	try {
			//tr = new TSocket("localhost", 9160)
			tr = new TSocket(config.getProperty(Utils.DB_HOST), config.getProperty(Utils.DB_PORT).toInt)
			keySpace = config.getProperty(Utils.DB_KEYSPACE)
			shortUrlCF = config.getProperty(Utils.DB_SHORT_URL_CF)
			longUrlCF = config.getProperty(Utils.DB_LONG_URL_CF)
			accessTimeCF = config.getProperty(Utils.DB_ACCESS_TIME_CF)
			val proto: TProtocol = new TBinaryProtocol(tr)
			client = new Cassandra.Client(proto)
			tr.open()
		} 
  }

  def closeConnection() {
	try {
			tr.flush();
			tr.close();
		} catch {
		  case e: Exception => e.printStackTrace()
		}
  }

  def addURL(shortenURL: String, longURL: String): Boolean = {
	try {
		val timestamp = System.currentTimeMillis()
		val colPathName = new ColumnPath(shortUrlCF)
        colPathName.setColumn(CF_SHORTURL_LONG_URL.getBytes("UTF8"))
        client.insert(keySpace, shortenURL, colPathName, longURL.getBytes("UTF8")
        	, timestamp, ConsistencyLevel.ALL)
        	
        colPathName.setColumn(CF_SHORTURL_CREATED_DATE.getBytes("UTF8"))
        client.insert(keySpace, shortenURL, colPathName, Utils.formatDate(new Date()).getBytes("UTF8")
            , timestamp, ConsistencyLevel.ALL)
            
        colPathName.setColumn(CF_SHORTURL_LAST_ACCESS_DATE.getBytes("UTF8"))
        client.insert(keySpace, shortenURL, colPathName, "".getBytes("UTF8")
            , timestamp, ConsistencyLevel.ALL)
        
        colPathName.setColumn(CF_SHORTURL_COUNTER.getBytes("UTF8"))
        client.insert(keySpace, shortenURL, colPathName, "0".getBytes("UTF8")
            , timestamp, ConsistencyLevel.ALL)
            
        addLongURL(longURL, shortenURL)
        
        return true
    } catch {
    	case e:Exception => return false
    }
  }
  
  def addLongURL(longURL: String,  shortenURL: String): Boolean = {
	try {		
		val timestamp = System.currentTimeMillis()
		val colPathName = new ColumnPath(longUrlCF)
		val date = new Date()
		colPathName.setColumn(CF_LONGURL_SHORT_URL.getBytes("UTF8"))
        client.insert(keySpace, longURL, colPathName, shortenURL.getBytes("UTF8")
        		, timestamp, ConsistencyLevel.ALL) 
        return true
       	} catch {
		  	case e: TException => return false
		}
  }
  
  def removeRow(columnFamily: String, key: String): Boolean = {
	try {
		val columnPath: ColumnPath = new ColumnPath(columnFamily)
		client.remove(keySpace, key, columnPath, System.currentTimeMillis(), ConsistencyLevel.ALL)
		return true
	} catch {
		case e:Exception => return false
    }
  }
  
  def removeColumn(columnFamily: String, key: String, columnName: String): Boolean = {
	try {
		val columnPath: ColumnPath = new ColumnPath(columnFamily)
		columnPath.setColumn(columnName.getBytes("UTF8"));
		client.remove(keySpace, key, columnPath, System.currentTimeMillis(), ConsistencyLevel.ALL)
		return true
	} catch {
		case e:Exception => return false
    }
  }
  
  def getLongURL(shortenURL: String): String = {
	return getColumnValue(shortUrlCF, shortenURL, CF_SHORTURL_LONG_URL)
  }
  
  def getShortURL(longURL: String): String = {
	return getColumnValue(longUrlCF, longURL, CF_LONGURL_SHORT_URL)
  }

  
  def existShortURL(shortenURL: String):Boolean = {
   return (getLongURL(shortenURL) != null)
  }
  
   def updateLastAccess(shortenURL: String): Boolean = {
	 try {
		 val oldLastAccess = getColumnValue(shortUrlCF, shortenURL, CF_SHORTURL_LAST_ACCESS_DATE)
         val currentDate = Utils.formatDate(new Date())
         if(!oldLastAccess.equals(currentDate)) {
        	 removeColumn(accessTimeCF, oldLastAccess, shortenURL)
        	 addAccessTime(currentDate, shortenURL)
         }
		 
		 val counter = getColumnValue(shortUrlCF, shortenURL, CF_SHORTURL_COUNTER).toInt + 1
		 val timestamp = System.currentTimeMillis()
		 val colPathName = new ColumnPath(shortUrlCF)
         colPathName.setColumn(CF_SHORTURL_LAST_ACCESS_DATE.getBytes("UTF8"))
         client.insert(keySpace, shortenURL, colPathName, Utils.formatDate(new Date()).getBytes("UTF8")
            , timestamp, ConsistencyLevel.ALL)
         colPathName.setColumn(CF_SHORTURL_COUNTER.getBytes("UTF8"))
         client.insert(keySpace, shortenURL, colPathName, counter.toString.getBytes("UTF8")
            , timestamp, ConsistencyLevel.ALL)
		 
         return true
	 } catch {
		 case e: Exception =>  return false
	 }
  }
   
  def getColumnValue(colFamily: String, key: String, colName: String): String = {
    val colPath: ColumnPath = new ColumnPath(colFamily);
	colPath.setColumn(colName.getBytes("UTF8"));
	try {
		val colLongUrl:Column = client.get(keySpace, key, colPath,
				ConsistencyLevel.ONE).getColumn();
		return new String(colLongUrl.value)
	} catch {
		case e:Exception =>	return null
	}
  }
  
  def addAccessTime(accessTime: String, shortenURl: String): Boolean = {
    try{
    	val timestamp = System.currentTimeMillis()
    	val colPathName = new ColumnPath(accessTimeCF)
    	colPathName.setColumn(shortenURl.getBytes("UTF8"))
    	client.insert(keySpace, accessTime, colPathName, "".getBytes("UTF8")
                , timestamp, ConsistencyLevel.ALL)
        return true
    } catch {
    	case e: Exception => return false
    }
  }
  
  def getAllURLAccessOnDate(date: String): List[String] = {
	val predicate:SlicePredicate = new SlicePredicate
	val sliceRange:SliceRange = new SliceRange(){}
	sliceRange.setStart(new Array[Byte](0));
	sliceRange.setFinish(new Array[Byte](0));
	predicate.setSlice_range(sliceRange);
	
	val parent:ColumnParent = new ColumnParent(accessTimeCF);
	val results = client.get_slice(keySpace, date, parent, predicate, ConsistencyLevel.ONE);
	var urlList: List[String] = List()
	for(i <- 0 to (results.size-1)) {
		val result = results.get(i)
		val column:Column = result.column
		urlList = (new String(column.name)) :: urlList
	}
	return urlList
  }
  
}
