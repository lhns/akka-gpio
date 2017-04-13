package org.lolhens.akka.gpio

import akka.actor.{Actor, ActorRef, ActorRefFactory, Props}
import com.pi4j.io.gpio.{GpioController, GpioFactory}
import org.lolhens.akka.gpio.Gpio._

import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

/**
  * Created by pierr on 07.04.2017.
  */
class GpioManager extends Actor {
  var gpioConnections: Map[GpioHeader, ActorRef] = Map.empty

  lazy val gpioControllerTry: Try[GpioController] = try {
    Success(GpioFactory.getInstance())
  } catch {
    case NonFatal(exception) => Failure(exception)
    case exception: UnsatisfiedLinkError => Failure(exception)
  }

  override def receive: Receive = {
    case connect@ConnectDigital(gpioHeader) =>
      val listener = sender()

      {
        for {
          gpioController <- gpioControllerTry
          pins <- gpioHeader.pins
          connection = gpioConnections.getOrElse(gpioHeader, {
            val connection: ActorRef = GpioConnection.actor(gpioController, pins)
            gpioConnections += (gpioHeader -> connection)
            connection
          })
        } yield (connection, pins)
      } match {
        case Success((connection, pins)) =>
          listener tell(Connected(pins.keySet), connection)

        case Failure(exception) =>
          listener ! CommandFailed(connect, exception)

      }
  }

  override def postStop(): Unit = gpioControllerTry.foreach(_.shutdown())
}

object GpioManager {
  private[gpio] val props = Props[GpioManager]

  private[gpio] def actor(implicit actorRefFactory: ActorRefFactory): ActorRef = actorRefFactory.actorOf(props)
}
