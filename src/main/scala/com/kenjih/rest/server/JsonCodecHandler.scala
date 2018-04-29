package com.kenjih.rest.server

import java.util
import java.io.{InputStream, OutputStream}

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import io.netty.buffer.{ByteBuf, ByteBufInputStream, ByteBufOutputStream}
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec
import org.apache.logging.log4j.scala.Logging

class JsonCodecHandler[T <: AnyRef](implicit instance: T)
  extends ByteToMessageCodec[T](instance.getClass) with Logging {

  override def encode(ctx: ChannelHandlerContext, msg: T, out: ByteBuf) = {
    val bos = new ByteBufOutputStream(out)
    JsonCodecHandler.Mapper.writeValue(bos.asInstanceOf[OutputStream], msg)
  }

  override def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: util.List[AnyRef]) = {
    val bis = new ByteBufInputStream(in)
    out.add(JsonCodecHandler.Mapper.readValue(bis.asInstanceOf[InputStream], instance.getClass))
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    logger.catching(cause)
    ctx.close()
  }

}

object JsonCodecHandler {
  val Mapper = (new ObjectMapper).registerModule(DefaultScalaModule)
}