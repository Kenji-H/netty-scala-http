package com.kenjih.metrics

import java.util.concurrent.atomic.LongAdder

class Counter(name: String,
              adder: LongAdder = new LongAdder()) {

  def add(x: Long): Unit = adder.add(x)
  def increment(): Unit = adder.increment()
  def outputAndRest(): String = s"counter: $name = ${adder.sumThenReset()}"
}
