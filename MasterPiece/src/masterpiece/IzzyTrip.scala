package masterpiece

import ev3dev4s.lego.Gyroscope
import ev3dev4s.scala2measure.Conversions.IntConversions
import ev3dev4s.sensors.SensorPort

import java.lang.Runnable
import scala.Unit

object IzzyTrip extends Runnable{
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

  private def fromStartToIzzy(): Unit ={
    //move from start to the scene changer
    Robot.movestraight(80.mm)
    Robot.leftRotation(-45.degrees)
    Robot.movestraight(500.mm)
    Robot.moveStraightBackward(35.mm)

  }

}