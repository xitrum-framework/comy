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
  private var columnFamily: String = ""
  private var tr: TTransport = null
  
  private var client: Cassandra.Client = _

  def openConnection() {
	try {
			//tr = new TSocket("localhost", 9160)
			tr = new TSocket(config.getProperty(Utils.DB_HOST), config.getProperty(Utils.DB_PORT).toInt)
			keySpace = config.getProperty(Utils.DB_KEYSPACE)
			columnFamily = config.getProperty(Utils.DB_COLFAM)
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
		  case e: TException => e.printStackTrace()
		}
  }

  def addURL(shortenURL: String, longURL: String): Boolean = {
	try {
		val timestamp = System.currentTimeMillis()
		val colPathName = new ColumnPath(columnFamily)
        colPathName.setColumn("Long URL".getBytes("UTF8"))
        client.insert(keySpace, shortenURL, colPathName, longURL.getBytes("UTF8")
        	, timestamp, ConsistencyLevel.ONE)  
        return true
    } catch {
    	case e:TException => return false
    }
  }

  def removeURL(shortenURL: String): Boolean = {
	try {
		val columnPath: ColumnPath = new ColumnPath(columnFamily)
		client.remove(keySpace, shortenURL, columnPath, System
					.currentTimeMillis(), ConsistencyLevel.ALL)
		return true
	} catch {
		case e:TException => return false
    }
  }
  
  def getLongURL(shortenURL: String): String = {
	var longURL: String = null
	val predicate: SlicePredicate = new SlicePredicate()
    val sliceRange: SliceRange = new SliceRange()
    sliceRange.setStart(new Array(0))
    sliceRange.setFinish(new Array(0))
    predicate.setSlice_range(sliceRange)
    val parent: ColumnParent = new ColumnParent(columnFamily)
    val results = client.get_slice(keySpace, shortenURL, parent, predicate, ConsistencyLevel.ONE)
   
    try {
    	var col: Column = results.get(0).column
    	longURL = new String(col.value, "UTF8")
    	return longURL
    } catch {
    	case e: Exception => return null
    }
  }
  
  def getShortURL(longURL: String): String = {
	return null
  }

  def existShortURL(shortenURL: String):Boolean = {
	val predicate: SlicePredicate = new SlicePredicate()
    val sliceRange: SliceRange = new SliceRange()
    sliceRange.setStart(new Array(0))
    sliceRange.setFinish(new Array(0))
    predicate.setSlice_range(sliceRange)
    val parent: ColumnParent = new ColumnParent(columnFamily)
    val results = client.get_slice(keySpace, shortenURL, parent, predicate, ConsistencyLevel.ONE)
    return results.size != 0
  }
  
  def printShorten(shortenURL: String) {
	try {
	      val predicate: SlicePredicate = new SlicePredicate()
	      val sliceRange: SliceRange = new SliceRange()
	      sliceRange.setStart(new Array(0))
	      sliceRange.setFinish(new Array(0))
	      predicate.setSlice_range(sliceRange)
	      val parent: ColumnParent = new ColumnParent(columnFamily)
	      val results = client.get_slice(keySpace, shortenURL, parent, predicate, ConsistencyLevel.ONE)
	      println("Shorten URL: " +  shortenURL)  
	      for(i <- 0 to results.size - 1) {
	        var col: Column = results.get(i).column
	        println(new String(col.name, "UTF8") + " -> "
	        		 + new String(col.value, "UTF8"))
	      } 
	} 
  }

}
