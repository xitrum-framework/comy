package com.gnt.shortenurl

import org.jboss.netty.handler.codec.http.HttpHeaders._;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names._;
import org.jboss.netty.handler.codec.http.HttpResponseStatus._;
import org.jboss.netty.handler.codec.http.HttpVersion._;
import org.jboss.netty.buffer._;
import org.jboss.netty.channel._;
import org.jboss.netty.handler.codec.http._;
import org.jboss.netty.util.CharsetUtil;

import org.apache.cassandra.thrift.Cassandra

import java.util.Properties

class HttpRequestHandler(inputConfig: Properties) extends SimpleChannelUpstreamHandler {
  
  var request:HttpRequest = _;
  var readingChunks:Boolean = false;
  
  /** Buffer that stores the response content */
  var buf:StringBuilder = new StringBuilder();
  
  private var config: Properties = inputConfig
  
  val db = new DatabaseConnector(inputConfig)
  db.openConnection
  
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
    	  if (longUrl != null) {
    	 	//TODO: Check longURL is existed
    	 	val existedShortUrl = db.getShortURL(longUrl)
    	 	var key: String = ""
    	 	if (existedShortUrl == null) {	//not existed
    	 		//TODO: Generate short key & add to DB
    	 		var doGenarate = true
    	 		while(doGenarate) {
    	 			key = Utils.generateKey
    	 			if(!db.existShortURL(key)) {
    	 				db.addURL(key, longUrl)
    	 				doGenarate = false
    	 			}
    	 		}
    	 	} else {	//existed
    	 		key = existedShortUrl
    	 	}
    	 	responseContent = key
    	  } else {
    	 	responseContent = "Invalid Request"
    	  }
    	  writeResponse(e, responseContent);
    	} else {
    	  val shortUrl: String = Utils.getShortUrl(requestUri)
    	  
    	  if (shortUrl != null) {
    	 	 val longUrl = db.getLongURL(shortUrl)
    	 	 if(longUrl != null) {
    	 		 //TODO: Lookup for long URL and redirect
    	 		 redirectResponse(e, longUrl)
    	 		 
    	 	 } else {
    	 		responseContent = "Invalid URL"
    	 		writeResponse(e, responseContent);
    	 	 }
    	  } else {
    	 	responseContent = "Invalid Request"
    	 	writeResponse(e, responseContent);
    	  }
    	}
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
  
  def redirectResponse(e: MessageEvent, redirectUrl: String) {
	var keepAlive:Boolean = isKeepAlive(request);
	val response:HttpResponse = new DefaultHttpResponse(HTTP_1_1, TEMPORARY_REDIRECT);
	response.setHeader(LOCATION, redirectUrl);
	 
	if (keepAlive) {
	  // Add 'Content-Length' header only for a keep-alive connection.
	  response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
	}

	// Write the response.
	val future:ChannelFuture = e.getChannel().write(response);
		
  	// Close the non-keep-alive connection after the write operation is done.
	if (!keepAlive) {
      future.addListener(ChannelFutureListener.CLOSE);
	}
  }
  
  def writeResponse(e: MessageEvent, content: String) {
    // Decide whether to close the connection or not.
	var keepAlive:Boolean = isKeepAlive(request);

    // Build the response object.
    val response:HttpResponse = new DefaultHttpResponse(HTTP_1_1, OK);

	 response.setContent(ChannelBuffers.copiedBuffer(content, CharsetUtil.UTF_8));
	 response.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");

    if (keepAlive) {
	  // Add 'Content-Length' header only for a keep-alive connection.
	  response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
	}

	// Write the response.
	val future:ChannelFuture = e.getChannel().write(response);
		
  	// Close the non-keep-alive connection after the write operation is done.
	if (!keepAlive) {
      future.addListener(ChannelFutureListener.CLOSE);
	}
  }
  
  override def exceptionCaught(ctx:ChannelHandlerContext, e:ExceptionEvent ) {
    e.getCause().printStackTrace();
	e.getChannel().close();
  }

}
