package com.kenjih.server

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.{HttpServerCodec, HttpServerExpectContinueHandler}

class HttpServerInitializer extends ChannelInitializer[SocketChannel] {
  override def initChannel(ch: SocketChannel) = {
    val p = ch.pipeline
    p.addLast(new HttpServerCodec)
    p.addLast(new HttpServerExpectContinueHandler)
    p.addLast(new HttpServerHandler)
  }
}
