package com.kenjih.rest.server

import com.kenjih.helloworld.server.HttpServerHandler
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelHandlerContext, ChannelOutboundHandlerAdapter, ChannelPromise}
import io.netty.handler.codec.http.{DefaultFullHttpResponse, HttpResponseStatus, HttpVersion}
import org.apache.logging.log4j.scala.Logging

@Sharable
class HttpOutboundHandler extends ChannelOutboundHandlerAdapter with Logging {

  override def write(ctx: ChannelHandlerContext, msg: scala.Any, promise: ChannelPromise): Unit = {
    val response = new DefaultFullHttpResponse(
      HttpVersion.HTTP_1_1,
      HttpResponseStatus.OK,
      msg.asInstanceOf[ByteBuf]
    )
    response.headers().set(HttpServerHandler.ContentType, "text/plain")
    response.headers().setInt(HttpServerHandler.ContentLength, response.content().readableBytes())
    response.headers().set(HttpServerHandler.Connection, HttpServerHandler.KeepAlive)

    ctx.write(response, promise)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    logger.catching(cause)
    ctx.close()
  }

}
