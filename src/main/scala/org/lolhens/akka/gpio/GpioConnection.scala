package org.lolhens.akka.gpio

import akka.actor.{Actor, ActorRef, ActorRefFactory, Props, Terminated}
import akka.routing.{BroadcastRoutingLogic, Router}
import com.pi4j.io.gpio._
import com.pi4j.io.gpio.event.{GpioPinDigitalStateChangeEvent, GpioPinListenerDigital}
import org.lolhens.akka.gpio.Gpio.{Register, SetState, StateChanged}

/**
  * Created by pierr on 07.04.2017.
  */
class GpioConnection(gpioController: GpioController,
                     pins: Map[Int, Pin]) extends Actor {
  var eventRouter = Router(BroadcastRoutingLogic())

  private val (input, output) = (PinMode.DIGITAL_INPUT, PinMode.DIGITAL_OUTPUT)
  private val pullDown = PinPullResistance.PULL_DOWN

  case class ProvisionedPin(pin: Int, state: Option[Boolean]) {
    val gpioPin: GpioPinDigitalOutput = state match {
      case Some(high) =>
        val gpioPin = gpioController.provisionDigitalMultipurposePin(pins(pin), output, pullDown)
        gpioPin.setState(high)
        gpioPin

      case None =>
        gpioController.provisionDigitalMultipurposePin(pins(pin), input, pullDown)
    }

    self ! StateChanged(pin, state.getOrElse(gpioPin.isHigh))

    gpioPin.addListener(new GpioPinListenerDigital {
      override def handleGpioPinDigitalStateChangeEvent(event: GpioPinDigitalStateChangeEvent): Unit =
        self ! StateChanged(pin, event.getState.isHigh)
    })

    def setState(newState: Option[Boolean]): ProvisionedPin =
      if (newState != state) {
        newState match {
          case Some(high) =>
            if (state.isEmpty)
              gpioPin.setMode(output)
            gpioPin.setState(high)

          case None =>
            gpioPin.setMode(input)
        }

        self ! StateChanged(pin, newState.getOrElse(gpioPin.isHigh))

        copy(state = newState)
      } else this
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
        eventRouter.route(stateChanged, self)
        lastPinState += (pin -> state)
      }

    case SetState(states) =>
      states
        .filter(e => pins.contains(e._1))
        .foreach {
          case (pin, state) =>
            provisionedPins += (pin -> {
              provisionedPins.get(pin) match {
                case Some(provisionedPin) => provisionedPin.setState(state)
                case None => ProvisionedPin(pin, state)
              }
            })
        }
  }
}

object GpioConnection {
  private[gpio] def props(gpioController: GpioController,
                          pins: Map[Int, Pin]) = Props(new GpioConnection(gpioController, pins))

  private[gpio] def actor(gpioController: GpioController,
                          pins: Map[Int, Pin])
                         (implicit actorRefFactory: ActorRefFactory): ActorRef =
    actorRefFactory.actorOf(props(gpioController, pins))
}
