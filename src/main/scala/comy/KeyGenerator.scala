package comy

object KeyGenerator {
  def generateKey(): String = {
    val codes = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890"
 	var key: String = ""
 	for (count <- 0 to 6){
 	  val randomIndex = scala.math.floor(scala.math.random * 62).toInt
 	  key += codes(randomIndex)
 	}
 	key
  }
}