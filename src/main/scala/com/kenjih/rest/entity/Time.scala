package com.kenjih.rest.entity

import com.fasterxml.jackson.annotation.{JsonCreator, JsonIgnoreProperties, JsonProperty}
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

@JsonIgnoreProperties(ignoreUnknown = true)
case class Time(timeZone: String, epochSeconds: Option[Long], dateTime: Option[String])

object Time {
  @JsonCreator
  def create(@JsonProperty("timeZone") timeZone: String,
             @JsonProperty("epochSeconds") @JsonDeserialize(contentAs = classOf[java.lang.Long]) epochSeconds: Long,
             @JsonProperty("dateTime") dateTime: String): Time =
    Time(timeZone, Some(epochSeconds), Some(dateTime))

  implicit val singleton: Time = Time("", None, None)
}
