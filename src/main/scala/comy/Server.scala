package comy

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 * Start the server with the config file option.
 */
object Server {
  def main(args: Array[String]) {
    val configPath = args(1)
    val config = new Config(configPath)

    val bootstrap = new ServerBootstrap(
      new NioServerSocketChannelFactory(
        Executors.newCachedThreadPool, Executors.newCachedThreadPool))
    bootstrap.setPipelineFactory(new HttpServerPipelineFactory(config))
    bootstrap.bind(new InetSocketAddress(config.serverPort))

    // FIXME: log to file
    println("Comy started")
  }
}
