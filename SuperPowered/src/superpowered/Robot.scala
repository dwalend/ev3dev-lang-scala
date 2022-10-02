package superpowered

import ev3dev4s.actuators.MotorPort
import ev3dev4s.lego.Movement
import ev3dev4s.measure.Conversions.{FloatConversions, IntConversions}
import ev3dev4s.measure.{Degrees, DegreesPerSecond, MilliMeters}

import scala.Unit

object Robot {
  Movement.setMovementMotorsTo(MotorPort.A,MotorPort.C)

  val cir: MilliMeters = 275.mm
  def dtd(distance :MilliMeters): Degrees = {
    (360 * distance.v / cir.v).degrees
  }

  def movestraight(distance :MilliMeters, speed:DegreesPerSecond): Unit ={
    Movement.move(
      motorDegrees = dtd(distance),
      leftSpeed = speed,
      rightSpeed = speed
    )
  }
}
// :D :[]