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

class HttpServerPipelineFactory extends ChannelPipelineFactory {
	def getPipeline(): ChannelPipeline = {
            // Create a default pipeline implementation.
            val pipeline:ChannelPipeline = Channels.pipeline();

            // Uncomment the following line if you want HTTPS
            // SSLEngine engine =
            // SecureChatSslContextFactory.getServerContext().createSSLEngine();
            // engine.setUseClientMode(false);
            // pipeline.addLast("ssl", new SslHandler(engine));

            pipeline.addLast("decoder", new HttpRequestDecoder());
            // Uncomment the following line if you don't want to handle HttpChunks.
            // pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
            pipeline.addLast("encoder", new HttpResponseEncoder());
            // Remove the following line if you don't want automatic content
            // compression.
            pipeline.addLast("deflater", new HttpContentCompressor());
            pipeline.addLast("handler", new HttpRequestHandler());
            return pipeline;
    }
  
}
