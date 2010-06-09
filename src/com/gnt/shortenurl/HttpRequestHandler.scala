package com.gnt.shortenurl

import org.jboss.netty.handler.codec.http.HttpHeaders._;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names._;
import org.jboss.netty.handler.codec.http.HttpResponseStatus._;
import org.jboss.netty.handler.codec.http.HttpVersion._;
import org.jboss.netty.buffer._;
import org.jboss.netty.channel._;
import org.jboss.netty.handler.codec.http._;
import org.jboss.netty.util.CharsetUtil;

class HttpRequestHandler extends SimpleChannelUpstreamHandler {
  // Configurations
  val ROOT_URL = "http://gnt.com.vn/"
  // End of configurations
  var request:HttpRequest = _;
  var readingChunks:Boolean = false;
  
  /** Buffer that stores the response content */
  var buf:StringBuilder = new StringBuilder();
  
  override def messageReceived(ctx:ChannelHandlerContext, e:MessageEvent) { //throw Exception {
    if (!readingChunks) {
      this.request = e.getMessage().asInstanceOf[HttpRequest];
      val requestUri: String = this.request.getUri()
      val clientIp: String = Utils.getClientIP(e.getRemoteAddress().toString)
      var request:HttpRequest = this.request;
      
      if (request.isChunked()) {
        readingChunks = true;
      } else { //This is the case when we want to handle
    	var responseContent: String = ""
    	if (requestUri.indexOf("/url=") == 0) {
    	  val longUrl: String = Utils.getLongUrl(requestUri)
    	  if (longUrl != "") {
    	    //TODO: Generate short key
    	    val key: String = Utils.generateKey
    	    responseContent = ROOT_URL + key
    	  } else {
    	 	//TODO: Return "Invalid Request" response
    	 	responseContent = "Invalid Request"
    	  }
    	} else {
    	  val shortUrl: String = Utils.getShortUrl(requestUri)
    	  if (shortUrl != "") {
    	 	//TODO: Lookup for long URL and redirect
    	 	responseContent = "<meta HTTP-EQUIV='REFRESH' content='0; url=http://google.com.vn'>"
    	  } else {
    	 	//TODO: Return "Invalid Request" response
    	 	responseContent = "Invalid Request"
    	  }
        
    	}
		writeResponse(e, responseContent);
	  }
    } else {
		val chunk:HttpChunk = e.getMessage().asInstanceOf[HttpChunk];
		if (chunk.isLast()) {
		  readingChunks = false;
		  writeResponse(e, "");
		} else {
		  println("CHUNK: "	+ chunk.getContent().toString(CharsetUtil.UTF_8) + "\r\n");
		}
	}
  }
  
  def writeResponse(e: MessageEvent, content: String) {
    // Decide whether to close the connection or not.
	var keepAlive:Boolean = isKeepAlive(request);

    // Build the response object.
    val response:HttpResponse = new DefaultHttpResponse(HTTP_1_1, OK);

//    buf.append("Body of content");
//	response.setContent(ChannelBuffers.copiedBuffer(buf.toString(),
//				CharsetUtil.UTF_8));
//	response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");

	 val redirectCodeHTML =	 "<meta HTTP-EQUIV='REFRESH' content='0; url=http://google.com.vn'>";
	 response.setContent(ChannelBuffers.copiedBuffer(content, CharsetUtil.UTF_8));
	 response.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");

    if (keepAlive) {
	  // Add 'Content-Length' header only for a keep-alive connection.
	  response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
	}

	// Write the response.
	val future:ChannelFuture = e.getChannel().write(response);
		
  	// Close the non-keep-alive connection after the write operation is
	// done.
	if (!keepAlive) {
      future.addListener(ChannelFutureListener.CLOSE);
	}
  }
  
  override def exceptionCaught(ctx:ChannelHandlerContext, e:ExceptionEvent ) { //throws Exception {
    e.getCause().printStackTrace();
	e.getChannel().close();
  }

}
