package org.lolhens.akka.gpio

import akka.actor.{Actor, ActorSystem, Props}
import akka.io.IO
import org.lolhens.akka.gpio.Gpio._

import scala.language.postfixOps

/**
  * Created by pierr on 04.11.2016.
  */
object Main {
  def main(args: Array[String]): Unit = {
    println("starting")

    implicit val actorSystem = ActorSystem()

    println("started actor system")

    class GpioTestActor extends Actor {
      println("connecting")

      IO(Gpio) ! ConnectDigital(GpioHeader.Raspberry)

      override def receive: Receive = {
        case Connected(pins) =>
          println("connected")
          println(pins)

          val connection = sender()
          connection ! Register(self)

          context become {
            case StateChanged(1, state) =>
              connection ! SetState(2, Some(state))

            case StateChanged(pin, state) =>
              println(pin + " = " + state)
          }

          //Thread.sleep(2000)

          connection ! SetState(Map(1 -> None, 2 -> Some(true), 3 -> Some(false), 4 -> None, 5 -> None))
      }
    }

    println("creating actor")

    actorSystem.actorOf(Props(new GpioTestActor()))

    println("done")
  }
}
