package com.kenjih.helloworld.client

import scala.util.Try

import org.apache.logging.log4j.scala.Logging
import io.netty.channel.nio.NioEventLoopGroup

class HttpClient extends Logging {
  def run(host: String, port: Int, group: NioEventLoopGroup): Unit =
    Try {
      val httpSender = new HttpSender(host, port, group)
      httpSender.start()
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