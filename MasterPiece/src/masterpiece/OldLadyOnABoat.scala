package masterpiece

import ev3dev4s.actuators.MotorPort
import ev3dev4s.lego.{Gyroscope, Motors}
import ev3dev4s.scala2measure.Conversions.IntConversions
import ev3dev4s.sensors.SensorPort

import java.lang.Runnable
import scala.Unit

object OldLadyOnABoat extends Runnable {
  //start with back of robot on the south wall, right start area
  override def run(): Unit = {
    setGyro()

    fromStartToBoat()

    ErrandsBeforeKidnapping
  }

def ErrandsBeforeKidnapping: Unit = {
  Robot.moveStraightBackward(35.mm)
Robot.leftRotation(45.degrees)


// Listen to Ella for Starting
  // Not doing so will result in immediate explosion
  // Even, especially for adults

}
  private def fromStartToBoat(): Unit = {
  Robot.movestraight(570.mm)
    Motors.runForDuration(MotorPort.D,(3*1000).milliseconds,-100.degreesPerSecond)
    Motors.runForDuration(MotorPort.D,(3*1000).milliseconds,-100.degreesPerSecond)

  }


  private def setGyro (): Unit = {
  //Set the gyroscope
  Gyroscope.reset (SensorPort.One)
  Gyroscope.setHeading (SensorPort.One, 90.degrees)
  }
}














































































































