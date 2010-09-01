package comy

import java.util.Properties
import java.io.FileInputStream

class Config(path: String) {
  private val properties = new Properties
  properties.load(new FileInputStream(path))

  val serverPort = properties.getProperty("SERVER_PORT").toInt
  val apiIps     = properties.getProperty("API_IPS").split(",").map(ip => ip.trim)
  val adminIps   = properties.getProperty("ADMIN_IPS").split(",").map(ip => ip.trim)

  val logFile = properties.getProperty("LOG_FILE")

  val dbName               = properties.getProperty("DB_NAME")
  val dbConnectionsPerHost = properties.getProperty("DB_CONNECTIONS_PER_HOST").toInt
  val dbExpirationDays     = properties.getProperty("DB_EXPIRATION_DAYS").toInt
  val dbAddrs              = properties.getProperty("DB_ADDRS").split(",").map(addr => addr.trim)

  def isApiAllowed(ip: String) = apiIps.exists { ip2 =>
    (ip2 == "*") || (ip2 == ip)
  }

  def isAdminAllowed(ip: String) = adminIps.exists { ip2 =>
    (ip2 == "*") || (ip2 == ip)
  }
}
