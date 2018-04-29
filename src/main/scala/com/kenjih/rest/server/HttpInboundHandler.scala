package com.kenjih.rest.server

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.handler.codec.http._
import org.apache.logging.log4j.scala.Logging
import com.kenjih.utility.metrics.Counter

@Sharable
class HttpInboundHandler(requestCounter: Counter) extends ChannelInboundHandlerAdapter with Logging {

  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {

    val request = msg.asInstanceOf[FullHttpRequest]
    if (!request.content.isReadable) {
      throw new IllegalArgumentException("content is null")
    }

    requestCounter.increment()
    ctx.fireChannelRead(request.content)
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    logger.catching(cause)
    ctx.close()
  }

}
