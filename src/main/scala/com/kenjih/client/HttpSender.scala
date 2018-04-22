package com.kenjih.client

import java.util.concurrent.LinkedBlockingQueue

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.pool.{ChannelPoolHandler, FixedChannelPool}
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http._
import io.netty.util.concurrent.Future
import org.apache.logging.log4j.scala.Logging

class HttpSender(host: String, port: Int, group: NioEventLoopGroup, poolSize: Int = 20) extends Logging {

  private val bootstrap = new Bootstrap()
    .group(group)
    .channel(classOf[NioSocketChannel])
    .remoteAddress(host, port)

  private val pool = new FixedChannelPool(bootstrap, new ChannelPoolHandler() {
    override def channelCreated(ch: Channel): Unit = {
      ch.pipeline
        .addLast(new HttpClientCodec)
        .addLast(new HttpContentDecompressor())
        .addLast(new HttpClientHandler())
      logger.info("channel created.")
    }

    override def channelAcquired(ch: Channel) = {}

    override def channelReleased(ch: Channel) = {}

  }, poolSize, 5 * poolSize)

  def start(): Unit = {
    val queue = new LinkedBlockingQueue[DefaultFullHttpRequest](3 * poolSize)
    while (true) {
      queue.put(makeRequest())
      pool
        .acquire()
        .addListener { (f: Future[Channel]) =>
          val request = queue.take()
          if (f.isSuccess) {
            val channel = f.getNow
            channel.writeAndFlush(request)
              .addListener((_: Future[Channel]) => pool.release(channel))
          }
        }
    }
  }

  def stop(): Unit = pool.close()

  private def makeRequest(): DefaultFullHttpRequest = {
    val request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/")
    request.headers.set(HttpHeaderNames.HOST, host)
    request.headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
    request
  }

}
