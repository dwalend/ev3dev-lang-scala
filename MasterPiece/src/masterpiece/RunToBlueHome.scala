package masterpiece

import ev3dev4s.actuators.{MotorPort, MotorStopCommand}
import ev3dev4s.lego.{Gyroscope, Motors}
import ev3dev4s.scala2measure.Conversions.IntConversions
import ev3dev4s.sensors.SensorPort

import java.lang.Runnable
import scala.Unit

object RunToBlueHome extends Runnable {
  //start with back of robot on the south wall, right start area
  override def run(): Unit = {
    setGyro()

    fromRedToBlue()

    //ErrandsBeforeKidnapping
  }


  private def fromRedToBlue(): Unit = {
   //Robot.movestraight(480.mm)
    Robot.movestraight(250.mm)
    Robot.rightRotation(90.degrees)
    Robot.rightRotation(90.degrees)
    Robot.movestraight(1600
      .mm)
    //Robot.movestraight(1400.mm)
    //Robot.movestraight(1920.mm)


  }







  private def setGyro (): Unit = {
  //Set the gyroscope
  Gyroscope.reset (SensorPort.One)
  Gyroscope.setHeading (SensorPort.One, 0.degrees)
  }
}














































































































