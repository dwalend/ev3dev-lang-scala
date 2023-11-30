package masterpiece

import ev3dev4s.lego.Gyroscope
import ev3dev4s.scala2measure.Conversions.IntConversions
import ev3dev4s.sensors.SensorPort

import java.lang.Runnable
import scala.Unit

object StuffToMuseum extends Runnable {
  //start with back of robot on very back wall - zero there.
  override def run(): Unit = {
    setGyro()

    startToMuseum()
  }

  // start to museum
  // WOPPER RULES
  private def setGyro(): Unit = {
    //Set the gyroscope
    Gyroscope.reset(SensorPort.One)
    Gyroscope.setHeading(SensorPort.One, 180.degrees)
  }

  //Avril is the best in the entire world and she is much better than Aanya
  private def startToMuseum(): Unit = {
    Robot.moveStraightBackward(635.millimeters)
  Robot.leftRotation(135.degrees)
    Robot.moveStraightBackward(100.millimeters)
    Robot.leftRotation(90.degrees)
Robot.moveStraightBackward(550.millimeters)
    Robot.rightRotation(135.degrees)
    Robot.moveStraightBackward(50.millimeters)
    Robot.rightRotation(180.degrees)


  }
}