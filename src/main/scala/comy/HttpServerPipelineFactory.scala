package comy

import org.jboss.netty.channel.{Channels, ChannelPipeline, ChannelPipelineFactory}
import org.jboss.netty.handler.codec.http.{HttpRequestDecoder, HttpResponseEncoder}

class HttpServerPipelineFactory(config: Config) extends ChannelPipelineFactory {
  def getPipeline: ChannelPipeline = {
    val pipeline: ChannelPipeline = Channels.pipeline
    pipeline.addLast("decoder", new HttpRequestDecoder)
    pipeline.addLast("encoder", new HttpResponseEncoder)
    pipeline.addLast("handler", new HttpRequestHandler(config))
    pipeline
  }
}
