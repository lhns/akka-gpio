package org.lolhens.akka.gpio

import akka.actor.{Actor, ActorRef, ActorRefFactory, Props, Terminated}
import akka.routing.{BroadcastRoutingLogic, Router}
import com.pi4j.io.gpio._
import com.pi4j.io.gpio.event.{GpioPinDigitalStateChangeEvent, GpioPinListenerDigital}
import org.lolhens.akka.gpio.Gpio.{GetState, Register, SetState, StateChanged}

import scala.util.Try

/**
  * Created by pierr on 07.04.2017.
  */
class DigitalGpioConnection(gpioController: GpioController,
                            pins: Map[Int, Pin],
                            pull: Option[Boolean],
                            inverted: Boolean) extends Actor {
  var eventRouter = Router(BroadcastRoutingLogic())

  private val (input, output) = (PinMode.DIGITAL_INPUT, PinMode.DIGITAL_OUTPUT)
  private val pullResistance = pull.map(_ ^ inverted) match {
    case None => PinPullResistance.OFF
    case Some(true) => PinPullResistance.PULL_UP
    case Some(false) => PinPullResistance.PULL_DOWN
  }

  case class ProvisionedPin(gpioPin: GpioPinDigitalOutput, state: Option[Boolean]) {
    def signal: Boolean = gpioPin.isHigh ^ inverted

    def setState(newState: Option[Boolean]): ProvisionedPin =
      if (newState != state) {
        newState match {
          case Some(signal) =>
            if (state.isEmpty)
              gpioPin.setMode(output)
            gpioPin.setState(signal ^ inverted)

          case None =>
            Try(gpioPin.setPullResistance(pullResistance))
            gpioPin.setMode(input)
        }

        copy(state = newState)
      } else this
  }

  object ProvisionedPin {
    def apply(pin: Int, state: Option[Boolean]): ProvisionedPin = {
      val gpioPin: GpioPinDigitalOutput = state match {
        case Some(high) =>
          val gpioPin = gpioController.provisionDigitalMultipurposePin(pins(pin), output)
          gpioPin.setState(high)
          gpioPin

        case None =>
          val gpioPin = gpioController.provisionDigitalMultipurposePin(pins(pin), input)
          Try(gpioPin.setPullResistance(pullResistance))
          gpioPin
      }

      gpioPin.addListener(new GpioPinListenerDigital {
        override def handleGpioPinDigitalStateChangeEvent(event: GpioPinDigitalStateChangeEvent): Unit = {
          val signal = event.getState.isHigh ^ inverted
          self ! StateChanged(pin, signal)
        }
      })

      ProvisionedPin(gpioPin, state)
    }
  }

  var provisionedPins: Map[Int, ProvisionedPin] = Map.empty

  var lastPinState: Map[Int, Boolean] = Map.empty

  override def receive: Receive = {
    case Register(ref) =>
      context watch ref
      eventRouter = eventRouter.addRoutee(ref)

    case Terminated(ref) =>
      eventRouter = eventRouter.removeRoutee(ref)

    case stateChanged@StateChanged(pin, state) =>
      val lastState = lastPinState.getOrElse(pin, false)
      if (state != lastState) {
        lastPinState += (pin -> state)
        eventRouter.route(stateChanged, self)
      }

    case SetState(states) =>
      states
        .filter(e => pins.contains(e._1))
        .foreach {
          case (pin, state) =>
            val provisionedPin = provisionedPins.get(pin) match {
              case Some(provisionedPin) => provisionedPin.setState(state)
              case None => ProvisionedPin(pin, state)
            }

            provisionedPins += (pin -> provisionedPin)

            self ! StateChanged(pin, state.getOrElse(provisionedPin.signal))
        }

    case GetState(pins@_*) =>
      sender() ! pins.map(pin => pin -> lastPinState.getOrElse(pin, false)).toMap
  }
}

object DigitalGpioConnection {
  private[gpio] def props(gpioController: GpioController,
                          pins: Map[Int, Pin],
                          pull: Option[Boolean],
                          inverted: Boolean) = Props(new DigitalGpioConnection(gpioController, pins, pull, inverted))

  private[gpio] def actor(gpioController: GpioController,
                          pins: Map[Int, Pin],
                          pull: Option[Boolean],
                          inverted: Boolean)
                         (implicit actorRefFactory: ActorRefFactory): ActorRef =
    actorRefFactory.actorOf(props(gpioController, pins, pull, inverted))
}
