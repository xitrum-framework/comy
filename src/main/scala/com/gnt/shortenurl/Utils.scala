package com.gnt.shortenurl


import java.util.Properties
import java.io.{FileInputStream, IOException, File}

object Utils {
  val CONFIG_PATH = "project" + File.separator + "config.properties"
	  
  val SERVER_PORT = "SERVER_PORT"
  val DB_HOST = "DB_HOST"
  val DB_PORT = "DB_PORT"
  val DB_KEYSPACE = "DB_KEYSPACE"
  val DB_COLFAM = "DB_COLFAM"
	
  def generateKey():String = {
    val codes = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890"
 	var key: String = ""
 	for (count <- 0 to 6){
 	  val randomIndex = scala.math.floor(scala.math.random * 62).toInt
 	  key += codes(randomIndex)
 	}
 	return key
  }
  
  def getClientIP(remoteAddress:String): String = {
	  try {
	    return remoteAddress.substring(1, remoteAddress.indexOf(':'))
	  }
	  catch {
	    case e: Exception => return null
	  }
  }
  
  def getLongUrl(uri: String): String = {
	  if (uri.length > 5 && uri.indexOf("/url=") == 0)
	    return uri.substring(5)
	  else
		return null
  }
  
  def getShortUrl(uri: String): String = {
	  if (uri.length == 8)
	    return uri.substring(1)
	  else
	    return null
  }

  def getConfig: Properties = {
	val properties:Properties = new Properties();
	try { 
	  properties.load(new FileInputStream(CONFIG_PATH))
	  if( (properties.getProperty(SERVER_PORT) == null || properties.getProperty(SERVER_PORT) == "")
	 	  || (properties.getProperty(DB_HOST) == null || properties.getProperty(DB_HOST) == "")
	 	  || (properties.getProperty(DB_PORT) == null || properties.getProperty(DB_PORT) == "")
	 	  || (properties.getProperty(DB_KEYSPACE) == null || properties.getProperty(DB_KEYSPACE) == "")
	 	  || (properties.getProperty(DB_COLFAM) == null || properties.getProperty(DB_COLFAM) == "")) {
	 	 	  return null
	  } else {
	 	  return properties
	  }
	} catch {
	  case e:IOException => return null
	}
	return properties
  }
}