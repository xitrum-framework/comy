package comy

import java.io.IOException

import org.apache.log4j._
import org.apache.log4j.Level._

trait Logger {
  private[this] val logger = org.apache.log4j.Logger.getLogger(getClass.getName)

  def debug(message: => String) = if (logger.isEnabledFor(DEBUG)) logger.debug(message)
  def debug(message: => String, ex: Throwable) = if (logger.isEnabledFor(DEBUG)) logger.debug(message,ex)
  def debugValue[T](valueName: String, value: => T): T = {
    val result: T = value
    debug(valueName + " == " + result.toString)
    result
  }

  def info(message: => String) = if (logger.isEnabledFor(INFO)) logger.info(message)
  def info(message: => String, ex: Throwable) = if (logger.isEnabledFor(INFO)) logger.info(message, ex)

  def warn(message: => String) = if (logger.isEnabledFor(WARN)) logger.warn(message)
  def warn(message: => String, ex: Throwable) = if (logger.isEnabledFor(WARN)) logger.warn(message, ex)

  def error(ex: Throwable) = if (logger.isEnabledFor(ERROR)) logger.error(ex.toString,ex)
  def error(message: => String) = if (logger.isEnabledFor(ERROR)) logger.error(message)
  def error(message: => String, ex:Throwable) = if (logger.isEnabledFor(ERROR)) logger.error(message,ex)

  def fatal(ex: Throwable) = if (logger.isEnabledFor(FATAL)) logger.fatal(ex.toString, ex)
  def fatal(message: => String) = if (logger.isEnabledFor(FATAL)) logger.fatal(message)
  def fatal(message: => String, ex: Throwable) = if (logger.isEnabledFor(FATAL)) logger.fatal(message, ex)

  def setLogPath(logPath: String) {
    configureDefaultSettings(logPath)
  }

  def configureDefaultSettings(logPath: String) {
    var rootLogger = Logger.getRootLogger
    rootLogger.setLevel(Level.INFO)

    val layout = new PatternLayout("%d [%t] %-5p %c - %m%n")
    rootLogger.addAppender(new ConsoleAppender(layout))

    try {
      val rfa = new RollingFileAppender(layout, logPath)
      rfa.setMaximumFileSize(1000000)
      rfa.setMaxBackupIndex(1)
      rootLogger.addAppender(rfa)
    } catch {
      case e: IOException =>
    }
  }
}
