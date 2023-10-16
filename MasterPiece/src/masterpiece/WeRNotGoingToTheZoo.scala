package masterpiece

import ev3dev4s.lego.{Gyroscope, Motors}
import ev3dev4s.scala2measure.Conversions.IntConversions
import ev3dev4s.sensors.SensorPort

import java.lang.Runnable
import scala.Unit

object WeRNotGoingToTheZoo extends Runnable {
  //start with back of robot on very back wall - zero there.
  override def run(): Unit = {
    setGyro()
    Robot.moveStraightBackward(600.mm)
    Robot.rightRotation(225.degrees)
    Robot.moveStraightBackward(180.mm)
    Robot.movestraight(180.mm)
    Robot.rightRotation(300.degrees)
    Robot.movestraight(540.mm)

  }

  //WOPPER RULES
  private def setGyro(): Unit = {
    //Set the gyroscope
    Gyroscope.reset(SensorPort.One)
    Gyroscope.setHeading(SensorPort.One, 180.degrees)
  }
}

