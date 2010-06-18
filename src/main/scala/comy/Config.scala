package comy

import java.util.Properties
import java.io.FileInputStream

class Config(path: String) {
  private val properties = new Properties
  properties.load(new FileInputStream(path))

  val serverPort = properties.getProperty("SERVER_PORT").toInt
  val apiAllowedIps = properties.getProperty("API_ALLOWED_IPS").split(",").map(ip => ip.trim)

  val logFile = properties.getProperty("LOG_FILE")

  val dbName               = properties.getProperty("DB_NAME")
  val dbConnectionsPerHost = properties.getProperty("DB_CONNECTIONS_PER_HOST").toInt
  val dbExpirationDays     = properties.getProperty("DB_EXPIRATION_DAYS").toInt

  val dbHostLeft  = properties.getProperty("DB_HOST_LEFT")
  val dbPortLeft  = properties.getProperty("DB_PORT_LEFT").toInt
  val dbHostRight = properties.getProperty("DB_HOST_RIGHT")
  val dbPortRight = properties.getProperty("DB_PORT_RIGHT").toInt

  def isApiAllowed(ip: String) = apiAllowedIps.exists { ip2 =>
    (ip2 == "*") || (ip2 == ip)
  }
}
