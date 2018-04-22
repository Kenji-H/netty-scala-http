package com.kenjih.server

import com.kenjih.server.HttpServer.Port
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import org.apache.logging.log4j.scala.Logging

import scala.util.Try

class HttpServer() extends Logging {
  def run(boss: NioEventLoopGroup, worker: NioEventLoopGroup) =
    Try {
      val b = new ServerBootstrap()
      b.option[Integer](ChannelOption.SO_BACKLOG, 1024)
      b.group(boss, worker)
        .channel(classOf[NioServerSocketChannel])
        .childHandler(new HttpServerInitializer())
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
