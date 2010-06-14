package comy

object Main {
  def main(args: Array[String]) {
    val mode       = args(1)
    val configPath = args(2)

    val config = new Config(configPath)
    mode match {
      case "server" => HttpServer.start(config)
      case "gc"     => GC.start(config)
    }
  }
}
