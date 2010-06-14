package comy

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

object HttpServer {
  def start(config: Config) {
    val bootstrap = new ServerBootstrap(
      new NioServerSocketChannelFactory(
        Executors.newCachedThreadPool, Executors.newCachedThreadPool))
    bootstrap.setPipelineFactory(new HttpServerPipelineFactory(config))
    bootstrap.bind(new InetSocketAddress(config.serverPort))

    // FIXME: log to file
    println("Comy started")
  }
}
