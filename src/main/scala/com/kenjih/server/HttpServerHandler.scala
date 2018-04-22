package com.kenjih.server

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.handler.codec.http._
import io.netty.util.AsciiString
import org.apache.logging.log4j.scala.Logging

@Sharable
class HttpServerHandler extends ChannelInboundHandlerAdapter with Logging {

  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = {
    if (msg.isInstanceOf[HttpRequest]) {
      val request = msg.asInstanceOf[HttpRequest]
      val keepAlive = HttpUtil.isKeepAlive(request)

      val response = new DefaultFullHttpResponse(
        HttpVersion.HTTP_1_1,
        HttpResponseStatus.OK,
        Unpooled.wrappedBuffer(HttpServerHandler.Content)
      )

      response.headers().set(HttpServerHandler.ContentType, "text/plain")
      response.headers().setInt(HttpServerHandler.ContentLength, response.content().readableBytes())
      if (keepAlive) {
        response.headers().set(HttpServerHandler.Connection, HttpServerHandler.KeepAlive)
        ctx.write(response)
      } else {
        ctx.write(response).addListener(ChannelFutureListener.CLOSE)
      }
    }
  }

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = {
    ctx.flush()
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    logger.catching(cause)
    ctx.close()
  }
  
}

object HttpServerHandler {
  val Content = Array[Byte]('H', 'e', 'l', 'l', 'o')
  val ContentType = AsciiString.cached("Content-Type")
  val ContentLength = AsciiString.cached("Content-Length")
  val Connection = AsciiString.cached("Connection")
  val KeepAlive = AsciiString.cached("keep-alive")

}