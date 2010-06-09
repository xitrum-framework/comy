package com.gnt.shortenurl;

import org.jboss.netty.channel.Channels;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import java.util.Properties

class HttpServerPipelineFactory(inputConfig: Properties) extends ChannelPipelineFactory {
	private var config: Properties = inputConfig
	
	def getPipeline(): ChannelPipeline = {
            val pipeline:ChannelPipeline = Channels.pipeline();
            pipeline.addLast("decoder", new HttpRequestDecoder());
            pipeline.addLast("encoder", new HttpResponseEncoder());
            pipeline.addLast("deflater", new HttpContentCompressor());
            pipeline.addLast("handler", new HttpRequestHandler(config));
            return pipeline;
    }
  
}
