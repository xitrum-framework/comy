package com.gnt.shortenurl

import java.util.{Properties, Date, Calendar}
import java.io.{FileInputStream, IOException, File}
import java.text.SimpleDateFormat

object Utils {
  val CONFIG_PATH = "project" + File.separator + "config.properties"
	  
  val SERVER_PORT = "SERVER_PORT"
  val DB_HOST = "DB_HOST"
  val DB_PORT = "DB_PORT"
  val DB_KEYSPACE = "DB_KEYSPACE"
  val DB_SHORT_URL_CF = "DB_SHORT_URL_CF"
  val DB_LONG_URL_CF = "DB_LONG_URL_CF"
  val DB_ACCESS_TIME_CF = "DB_ACCESS_TIME_CF"
  val LINK_EXPIRATION_DATE = "LINK_EXPIRATION_DATE"
  val ALLOWED_IP = "ALLOWED_IP"
	
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
	  if (uri.length > 5 && uri.indexOf("/url=") == 0) {
	 	var url:String = uri.substring(5)
	 	if(!url.contains("://")) {
	 		url = "http://" + url
	 	}
	    return url
	  } else return null
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
	 	  || (properties.getProperty(DB_SHORT_URL_CF) == null || properties.getProperty(DB_SHORT_URL_CF) == "")
	 	  || (properties.getProperty(DB_LONG_URL_CF) == null || properties.getProperty(DB_LONG_URL_CF) == "")
	 	  || (properties.getProperty(DB_ACCESS_TIME_CF) == null || properties.getProperty(DB_ACCESS_TIME_CF) == "")
	 	  || (properties.getProperty(LINK_EXPIRATION_DATE) == null || properties.getProperty(LINK_EXPIRATION_DATE) == "")
	 	  || (properties.getProperty(ALLOWED_IP) == null || properties.getProperty(ALLOWED_IP) == "")
	 	  ) {
	 	 	  return null
	  } else {
	 	  return properties
	  }
	} catch {
	  case e:IOException => return null
	}
	return properties
  }
  
  def formatDate(date: Date): String = {
	if(date == null) {
	  return null
	} else {
	  val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyyMMdd")
	  return dateFormat.format(date)
	}
  }
  
  def getExpirationDateString(expirationDate: Int): String = {
	  val currentDate: Date = new Date
	  val cal:Calendar = Calendar.getInstance();
	  cal.setTime(currentDate)
	  cal.add(Calendar.DATE, - expirationDate)
	  return formatDate(cal.getTime)
  }
}