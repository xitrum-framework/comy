package com.gnt.shortenurl

object Utils {
	
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
	    case e: Exception => return ""
	  }
  }
  
  def getLongUrl(uri: String): String = {
	  if (uri.length > 5 && uri.indexOf("/url=") == 0)
	    return uri.substring(5)
	  else
		return ""
  }
  
  def getShortUrl(uri: String): String = {
	  if (uri.length == 8)
	    return uri.substring(1)
	  else
	    return ""
  }

}