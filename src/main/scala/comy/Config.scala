package comy

import xitrum.util.Loader

case class AllowedIps(api: List[String], admin: List[String])
case class Db(addresses: List[String], connectionsPerHost: Int, name: String, expirationDays: Int)
case class Config(allowedIps: AllowedIps, db: Db)

object Config {
  private val config = Loader.jsonFromClasspath[Config]("comy.json")

  val allowedIps = config.allowedIps
  val db         = config.db

  def isApiAllowed(ip: String) = allowedIps.api.exists { ip2 =>
    (ip2 == "*") || (ip2 == ip)
  }

  def isAdminAllowed(ip: String) = allowedIps.admin.exists { ip2 =>
    (ip2 == "*") || (ip2 == ip)
  }
}
