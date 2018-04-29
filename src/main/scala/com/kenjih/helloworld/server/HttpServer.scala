package com.kenjih.helloworld.server

import java.util.concurrent.{Executors, TimeUnit}

import com.kenjih.helloworld.metrics.Counter
import com.kenjih.helloworld.server.HttpServer.Port
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.{ChannelInitializer, ChannelOption}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.http.{HttpServerCodec, HttpServerExpectContinueHandler}
import org.apache.logging.log4j.scala.Logging

import scala.util.Try

class HttpServer() extends Logging {
  def run(boss: NioEventLoopGroup, worker: NioEventLoopGroup) =
    Try {
      val b = new ServerBootstrap()
      val requestCounter = new Counter("request")
      val httpServerHandler = new HttpServerHandler(requestCounter)

      val executor = Executors.newSingleThreadScheduledExecutor()
      executor.scheduleAtFixedRate(new Runnable {
        override def run(): Unit = {
          logger.info(requestCounter.outputAndRest())
        }
      }, 1, 1, TimeUnit.SECONDS)

      b.option[Integer](ChannelOption.SO_BACKLOG, 1024)
      b.group(boss, worker)
        .channel(classOf[NioServerSocketChannel])
        .childHandler(new ChannelInitializer[SocketChannel]() {
          override def initChannel(ch: SocketChannel) = {
            val p = ch.pipeline
            p.addLast(new HttpServerCodec)
            p.addLast(new HttpServerExpectContinueHandler)
            p.addLast(httpServerHandler)
          }
        })
      val ch = b.bind(Port).sync().channel()

      logger.info("Http Server started.")

      ch.closeFuture().sync()

    }.recover { case e: Throwable =>
      logger.catching(e)
    }
}

object HttpServer {
  val Port = System.getProperty("port", "8080").toInt

  def main(args: Array[String]): Unit = {
    val boss = new NioEventLoopGroup(1)
    val worker = new NioEventLoopGroup()

    new HttpServer().run(boss, worker)

    boss.shutdownGracefully()
    worker.shutdownGracefully()
  }
}
