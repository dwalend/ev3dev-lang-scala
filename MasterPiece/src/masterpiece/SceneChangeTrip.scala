package masterpiece

import ev3dev4s.lego.Gyroscope
import ev3dev4s.os.Time
import ev3dev4s.scala2measure.Conversions.IntConversions
import ev3dev4s.sensors.SensorPort

import java.lang.Runnable
import scala.Unit

object SceneChangeTrip extends Runnable{
  //start with back of robot on very back wall - zero there.
  override def run(): Unit = {
    Gyroscope.reset(SensorPort.One)
    Gyroscope.setHeading(SensorPort.One, 45.degrees)
    Robot.movestraight(320.mm, 245.degreesPerSecond)

    Robot.leftRotation(0.degrees)
    Robot.movestraight(320.mm, 245.degreesPerSecond)



    Robot.leftRotation(-45.degrees)
    Robot.movestraight(50.mm, 245.degreesPerSecond)
    Robot.movestraight(-40.mm, -245.degreesPerSecond)
    Robot.movestraight(50.mm, 245.degreesPerSecond)
    Robot.movestraight(-40.mm, -245.degreesPerSecond)


  }

  def startToTv(): Unit = {
    Gyroscope.reset(SensorPort.One)
    Gyroscope.setHeading(SensorPort.One,0.degrees)
    Robot.movestraight(440.mm, 245.degreesPerSecond)
    Robot.movestraight(-30.mm, speed = -200.degreesPerSecond)
  }

  def tvToWindmill(): Unit = {
    Robot.leftRotation(-50.degrees)
    Robot.movestraight(330.mm+135.mm, speed = 200.degreesPerSecond)
    Robot.rightRotation(48.degrees)
  }

  def eatWindmill(): Unit = {
    Robot.movestraight(300.mm, speed = 400.degreesPerSecond)
    Time.pause(2000.milliseconds)

    Robot.movestraight(-50.mm, speed = -400.degreesPerSecond)
    Robot.movestraight(100.mm, speed = 400.degreesPerSecond)
    Time.pause(2000.milliseconds)

    Robot.movestraight(-50.mm, speed = -400.degreesPerSecond)
    Robot.movestraight(100.mm, speed = 400.degreesPerSecond)
    Time.pause(2000.milliseconds)

    Robot.movestraight(-50.mm, speed = -400.degreesPerSecond)
    Robot.movestraight(100.mm, speed = 400.degreesPerSecond)
    Time.pause(2000.milliseconds)

    Robot.movestraight(-50.mm, speed = -400.degreesPerSecond)
    Robot.movestraight(100.mm, speed = 400.degreesPerSecond)
    Time.pause(2000.milliseconds)

    Robot.movestraight(-210.mm, speed = -400.degreesPerSecond)
  }

  def foodForDinoAtHome(): Unit = {
    Robot.leftRotation(-30.degrees)
    Robot.movestraight(-690.mm, -200.degreesPerSecond)
  }
}
