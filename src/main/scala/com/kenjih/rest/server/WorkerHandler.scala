package com.kenjih.rest.server

import java.time.format.DateTimeFormatter
import java.time.{Instant, ZoneId, ZonedDateTime}
import scala.util.Try

import com.kenjih.rest.entity.Time
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import org.apache.logging.log4j.scala.Logging

@Sharable
class WorkerHandler extends SimpleChannelInboundHandler[Time] with Logging {
  override def channelRead0(ctx: ChannelHandlerContext, msg: Time) = {
    val output = msg match {
      case Time(timeZone, None, None) =>
        val epochSeconds = Instant.now.toEpochMilli / 1000
        msg.copy(epochSeconds = Some(epochSeconds), dateTime = calcDateTime(timeZone, epochSeconds))
      case Time(timeZone, Some(epochSeconds), None) =>
        msg.copy(dateTime = calcDateTime(timeZone, epochSeconds))
      case Time(timeZone, None, Some(dateTime)) =>
        msg.copy(epochSeconds = calcEpochSeconds(timeZone, dateTime))
      case _ =>
        msg
    }
    ctx.writeAndFlush(output)
  }

  private def calcEpochSeconds(timeZone: String, dateTime: String): Option[Long] =
    Try {
      val zoneDateTime = ZonedDateTime.parse(s"$dateTime $timeZone", WorkerHandler.Formatter)
      zoneDateTime.toEpochSecond
    }.toOption

  private def calcDateTime(timeZone: String, epochSeconds: Long): Option[String] =
    Try {
      val zoneId = ZoneId.SHORT_IDS.get(timeZone)
      val zoneDateTime = ZonedDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.of(zoneId))
      WorkerHandler.Formatter.format(zoneDateTime).substring(0, 19)
    }.toOption

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    logger.catching(cause)
    ctx.close()
  }

}

object WorkerHandler {
  val Formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss zzz")
}