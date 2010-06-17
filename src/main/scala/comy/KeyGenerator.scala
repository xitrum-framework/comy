package comy

object KeyGenerator {
  def generateKey: String = {
    val codes = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890"
    val key = new StringBuilder
    for (count <- 0 to 6) {
      val randomIndex = scala.math.floor(scala.math.random * 62).toInt
      key.append(codes(randomIndex))
    }
    key.toString
  }
}
