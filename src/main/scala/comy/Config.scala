package comy

import java.util.Properties

object Config {
  private val properties = {
    val stream = getClass.getClassLoader.getResourceAsStream("comy.properties")
    val ret = new Properties
    ret.load(stream)
    ret
  }

  val serverPort = properties.getProperty("SERVER_PORT", "8364").toInt
  val apiIps     = properties.getProperty("API_IPS",     "*").split(",").map(ip => ip.trim)
  val adminIps   = properties.getProperty("ADMIN_IPS",   "*").split(",").map(ip => ip.trim)

  val dbName               = properties.getProperty("DB_NAME",                 "comy")
  val dbConnectionsPerHost = properties.getProperty("DB_CONNECTIONS_PER_HOST", "100").toInt
  val dbExpirationDays     = properties.getProperty("DB_EXPIRATION_DAYS",      "90").toInt
  val dbAddrs              = properties.getProperty("DB_ADDRS",                "localhost:27017, localhost:27017, localhost:27017").split(",").map(addr => addr.trim)

  def isApiAllowed(ip: String) = apiIps.exists { ip2 =>
    (ip2 == "*") || (ip2 == ip)
  }

  def isAdminAllowed(ip: String) = adminIps.exists { ip2 =>
    (ip2 == "*") || (ip2 == ip)
  }
}
