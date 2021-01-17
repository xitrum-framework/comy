package comy

import com.typesafe.config.{ConfigFactory, Config => TConfig}

import java.util

class AllowedIps(config: TConfig) {
  val api  : util.List[String] = config.getStringList("api")
  val admin: util.List[String] = config.getStringList("admin")

  def isApiAllowed(ip: String): Boolean =
    api.contains("*") || api.contains(ip)

  def isAdminAllowed(ip: String): Boolean =
    admin.contains("*") || admin.contains(ip)
}

class Db(config: TConfig) {
  val addresses: util.List[String] = config.getStringList("addresses")
  val connectionsPerHost: Int = config.getInt("connectionsPerHost")
  val name: String = config.getString("name")
  val expirationDays: Int = config.getInt("expirationDays")
}

object Config {
  private val config = ConfigFactory.load("comy.conf")

  val allowedIps = new AllowedIps(config.getConfig("allowedIps"))
  val db         = new Db(config.getConfig("db"))
}
