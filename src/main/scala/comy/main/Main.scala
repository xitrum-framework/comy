package comy

object Main extends Logger {
  def main(args: Array[String]) {
    val mode       = args(0)
    val configPath = args(1)

    val config = new Config(configPath)
    setLogPath(config.logFile)
    mode match {
      case "server" => HttpServer.start(config)
      case "gc"     => GC.start(config)
    }
  }
}
