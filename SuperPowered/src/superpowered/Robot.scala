package superpowered

import ev3dev4s.Log
import ev3dev4s.actuators.MotorPort
import ev3dev4s.lego.{Gyroscope,  Movement}
import ev3dev4s.scala2measure.Conversions.{FloatConversions, IntConversions}
import ev3dev4s.scala2measure.{Degrees, DegreesPerSecond, MilliMeters}
import ev3dev4s.sensors.SensorPort

import scala.annotation.tailrec
import scala.{StringContext, Unit}



object Robot {
  Movement.setMovementMotorsTo(MotorPort.A,MotorPort.C)

  val cir: MilliMeters = 275.mm
  def dtd(distance :MilliMeters): Degrees = {
    (360 * distance.v / cir.v).degrees
  }

  def movestraight(distance :MilliMeters, speed:DegreesPerSecond): Unit ={
    Movement.move(
      motorDegrees = dtd(distance),
      speed = speed
    )
  }

  @tailrec
  def rightRotation(goalHeading:Degrees):Unit={
    val heading: Degrees = Gyroscope.readHeading(SensorPort.One)
    val toGo: Degrees = goalHeading - heading
    val speed: DegreesPerSecond = (200 * (toGo.v/90)).degreesPerSecond

    Log.log(s"heading is $heading speed is $speed")
    if(goalHeading>heading) {
      Movement.startMoving(speed,-speed)
      rightRotation(goalHeading)
    } else {
      Movement.stop()
    }
  }

  @tailrec
  def leftRotation(goalHeading: Degrees): Unit = {
    val heading = Gyroscope.readHeading(SensorPort.One)
    val toGo = -goalHeading + heading
    val speed = (200 * (toGo.v / 90)).degreesPerSecond

    Log.log(s"heading is $heading speed is $speed")
    if (goalHeading < heading) {
      Movement.startMoving(-speed, speed)
      //Time.pause(10.milliseconds)
      leftRotation(goalHeading)
    } else {
      Movement.stop()
    }
  }
}
// :D :[]