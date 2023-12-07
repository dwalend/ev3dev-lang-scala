package masterpiece

import ev3dev4s.actuators.MotorPort
import ev3dev4s.lego.{Gyroscope, Motors}
import ev3dev4s.scala2measure.Conversions.IntConversions
import ev3dev4s.sensors.SensorPort

import java.lang.Runnable
import scala.Unit

object IzzyTrip extends Runnable {
  //start with back of robot on the south wall, right start area
  override def run(): Unit = {
    setGyro()

    fromStartToIzzy()
  }

  //WOPPER RULES
  private def setGyro(): Unit = {
    //Set the gyroscope
    Gyroscope.reset(SensorPort.One)
    Gyroscope.setHeading(SensorPort.One, 0.degrees)
  }

  //Motor Height listen to Aanya
  // Memorize line
  private def fromStartToIzzy(): Unit = {
    //move from start to the scene changer
    Robot.movestraight(80.mm)
    Robot.leftRotation(-45.degrees)
    Robot.movestraight(500.mm)
    Motors.runForDuration(MotorPort.B, (1 * 2000).milliseconds, -100.degreesPerSecond)
    Robot.moveStraightBackward(225.mm)
    Robot.rightRotation(0.degrees)
    Robot.movestraight(500.mm)
Robot.moveStraightBackward(600.mm)

  }
}
// Mission Complete