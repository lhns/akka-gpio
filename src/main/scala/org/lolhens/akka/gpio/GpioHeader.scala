package org.lolhens.akka.gpio

import com.pi4j.io.gpio._
import com.pi4j.system.SystemInfo

import scala.util.Try

/**
  * Created by pierr on 12.04.2017.
  */
case class GpioHeader(name: String)(_pins: => Seq[Pin]) {
  def pins: Try[Map[Int, Pin]] = Try(_pins.map(e => e.getAddress -> e).toMap)
}

object GpioHeader {

  object Raspberry extends GpioHeader("Raspberry Pi")(RaspiPin.allPins(SystemInfo.getBoardType))

  object BananaPi extends GpioHeader("BananaPi")(BananaPiPin.allPins())

  object BananaPro extends GpioHeader("BananaPro")(BananaProPin.allPins())

  object OdroidC1 extends GpioHeader("Odroid C1")(OdroidC1Pin.allPins())

  object OdroidXU4 extends GpioHeader("Odroid XU4")(OdroidXU4Pin.allPins())

}
