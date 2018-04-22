package com.kenjih.client

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.HttpResponse
import org.apache.logging.log4j.scala.Logging

class HttpClientHandler extends SimpleChannelInboundHandler[HttpResponse] with Logging {
  override def channelRead0(ctx: ChannelHandlerContext, msg: HttpResponse) = {
    logger.debug(s"status=${msg.status.code}")
  }
}
