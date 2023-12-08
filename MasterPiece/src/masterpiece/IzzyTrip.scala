package masterpiece

import ev3dev4s.lego.Gyroscope
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
    Robot.moveStraightBackward(200.mm)
    Robot.rightRotation(0.degrees)
    Robot.movestraight(500.mm)
    Robot.rightRotation(45.degrees)
    Robot.movestraight(50.mm)
    Robot.moveStraightBackward(50.mm)
    Robot.leftRotation(0.degrees)
    Robot.moveStraightBackward(700.mm)
  }
}
// Mission Complete