package comy

import org.jboss.netty.channel.{Channels, ChannelPipeline, ChannelPipelineFactory}
import org.jboss.netty.handler.codec.http.{HttpRequestDecoder, HttpResponseEncoder}

class HttpChannelPipelineFactory(config: Config) extends ChannelPipelineFactory {
  private val db = new DB(config)

  def getPipeline: ChannelPipeline = {
    val pipeline = Channels.pipeline
    pipeline.addLast("decoder", new HttpRequestDecoder)
    pipeline.addLast("encoder", new HttpResponseEncoder)
    pipeline.addLast("handler", new HttpRequestHandler(config, db))
    pipeline
  }
}
