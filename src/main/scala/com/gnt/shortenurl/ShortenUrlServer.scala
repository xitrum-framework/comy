package com.gnt.shortenurl

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

object ShortenUrlServer {
  def main(args:Array[String]) {
	//check configuration
	val config = Utils.getConfig
	if(config == null) {
		println("Not found or invalid config file.")
	} else {
		val bootstrap:ServerBootstrap = new ServerBootstrap(
			new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()))
		bootstrap.setPipelineFactory(new HttpServerPipelineFactory(config))
		bootstrap.bind(new InetSocketAddress(config.getProperty(Utils.SERVER_PORT).toInt))
		println("Shorten URL Server started and running ....")
	}
  }

}