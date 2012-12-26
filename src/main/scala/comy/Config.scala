package comy

import com.typesafe.config.{Config => TConfig, ConfigFactory}

class AllowedIps(config: TConfig) {
  val api   = config.getStringList("api")
  val admin = config.getStringList("admin")

  def isApiAllowed(ip: String) =
    api.contains("*") || api.contains(ip)

  def isAdminAllowed(ip: String) =
    admin.contains("*") || admin.contains(ip)
}

class Db(config: TConfig) {
  val addresses          = config.getStringList("addresses")
  val connectionsPerHost = config.getInt("connectionsPerHost")
  val name               = config.getString("name")
  val expirationDays     = config.getInt("expirationDays")
}

object Config {
  private val config = ConfigFactory.load("comy.conf")

  val allowedIps = new AllowedIps(config.getConfig("allowedIps"))
  val db         = new Db(config.getConfig("db"))
}
