package com.kenjih.client

import scala.util.Try
import java.util.concurrent.TimeUnit

import org.apache.logging.log4j.scala.Logging

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.pool.{ChannelPoolHandler, FixedChannelPool}
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http._
import io.netty.util.concurrent.Future

class HttpClient extends Logging {
  def run(host: String, port: Int, group: NioEventLoopGroup): Unit =
    Try {
      val b = new Bootstrap()
      b.group(group).channel(classOf[NioSocketChannel]).remoteAddress(host, port)

      val pool = new FixedChannelPool(b, new ChannelPoolHandler() {
        override def channelCreated(ch: Channel): Unit = {
          logger.debug("channel created.")
          val pipeline = ch.pipeline
          pipeline.addLast(new HttpClientCodec)
          pipeline.addLast(new HttpContentDecompressor())
          pipeline.addLast(new HttpClientHandler())
        }

        override def channelAcquired(ch: Channel) = {
          logger.debug("channel acquired.")
        }

        override def channelReleased(ch: Channel) = {
          logger.debug("channel released.")
        }

      }, 10, 10)

      while (true) {
        val channelFuture = pool.acquire()
        if (!channelFuture.await(1, TimeUnit.SECONDS)) {
          channelFuture.cancel(false)
        } else {
          val channel = channelFuture.getNow
          if (channel != null) {
            val request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/")
            request.headers.set(HttpHeaderNames.HOST, host)
            request.headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE)
            channel.writeAndFlush(request)
              .addListener((_: Future[Channel]) => pool.release(channel))
          }
        }
      }

      pool.close()

    }.recover { case e: Throwable =>
      logger.catching(e)
    }
}

object HttpClient {
  def main(args: Array[String]): Unit = {
    val host = System.getProperty("host", "127.0.0.1")
    val port = System.getProperty("port", "8080").toInt
    val group = new NioEventLoopGroup()

    new HttpClient().run(host, port, group)

    group.shutdownGracefully()
  }

}