package com.gnt.shortenurl

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

object ShortenUrlServer {
	def main(args:Array[String]) {
    // Configure the server.
	val bootstrap:ServerBootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

  	// Set up the event pipeline factory.
	bootstrap.setPipelineFactory(new HttpServerPipelineFactory());

	// Bind and start to accept incoming connections.
	bootstrap.bind(new InetSocketAddress(8080));
 
    println("Shorten URL Server started and running ....")
  }

}