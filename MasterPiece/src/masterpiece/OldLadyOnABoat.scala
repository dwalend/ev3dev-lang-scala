package masterpiece

import ev3dev4s.actuators.{MotorPort, MotorStopCommand}
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
  Robot.moveStraightBackward(100.mm)
Robot.leftRotation(30.degrees)
  //Robot.movestraight(20.mm)
  Motors.runForDuration(MotorPort.D,(3*1000).milliseconds,100.degreesPerSecond)
//Robot.leftRotation(0.degrees)
 // Robot.movestraight(100.mm)
//Motors.runForDuration(MotorPort.D,(3*1000).milliseconds,-100.degreesPerSecond)
 //Robot.movestraight(70.mm)
 // Robot.leftRotation(-135.degrees)
Robot.rightRotation(90.degrees)
  Robot.moveStraightBackward(400.mm)
 // Robot.leftRotation(43.degrees)
 // Robot.movestraight(420.mm)
 // Motors.runForDuration(MotorPort.D,(3*1000).milliseconds,-100.degreesPerSecond)
 // Robot.leftRotation(-45.degrees)
 // Motors.runForDuration(MotorPort.D,(3*1000).milliseconds,100.degreesPerSecond)
//Robot.leftRotation(-130.degrees)
 // Robot.movestraight(280.mm)
// Listen to Ella for Starting135
  // Not doing so will result in immediate explosion
  // Even, especially for adults

}
  private def fromStartToBoat(): Unit = {
    Robot.movestraight(530.mm)
    Motors.setStopCommand(MotorPort.D, MotorStopCommand.BRAKE)
    Motors.runForDuration(MotorPort.D,(3000).milliseconds,100.degreesPerSecond)

    Motors.runForDuration(MotorPort.D,(3*1000).milliseconds,-100.degreesPerSecond)
    Motors.runForDuration(MotorPort.D,(2000).milliseconds,-100.degreesPerSecond)
    //Motors.runForDuration(MotorPort.D,(1*1000).milliseconds,-100.degreesPerSecond)

  }



  private def setGyro (): Unit = {
  //Set the gyroscope
  Gyroscope.reset (SensorPort.One)
  Gyroscope.setHeading (SensorPort.One, 90.degrees)
  }
}














































































































