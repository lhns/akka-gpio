package org.lolhens.akka.gpio

import akka.actor.{ActorRef, ExtendedActorSystem, ExtensionId, ExtensionIdProvider}
import akka.io.IO

/**
  * Created by pierr on 07.04.2017.
  */
object Gpio extends ExtensionId[GpioExt] with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem): GpioExt = new GpioExt(system)

  override def lookup = Gpio

  trait Command

  trait Event

  case class ConnectDigital(gpioHeader: GpioHeader,
                            pull: Option[Boolean] = Some(false),
                            inverted: Boolean = false) extends Command

  /*
  TODO: Not supported yet
   */
  case class ConnectAnalog(gpioHeader: GpioHeader) extends Command

  case class Register(ref: ActorRef) extends Command

  case class CommandFailed(command: Command, reason: Throwable) extends Event

  case class Connected(pins: Set[Int]) extends Event

  case class SetState(pins: Map[Int, Option[Boolean]]) extends Command

  object SetState {
    def apply(pin: Int, state: Option[Boolean]): SetState = SetState(Map(pin -> state))
  }

  case class StateChanged(pin: Int, state: Boolean) extends Event

  /*
  Answers with Map[Int, Boolean]
   */
  case class GetState(pins: Int*) extends Command

}

class GpioExt(system: ExtendedActorSystem) extends IO.Extension {
  override def manager: ActorRef = GpioManager.actor(system)
}
