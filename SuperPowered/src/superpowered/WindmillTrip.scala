package superpowered

import ev3dev4s.lego.Gyroscope
import ev3dev4s.measure.Conversions.IntConversions
import ev3dev4s.os.Time
import ev3dev4s.sensors.SensorPort

import java.lang.Runnable
import scala.Unit

object WindmillTrip extends Runnable{
  //start with back of robot on very back wall - zero there.
  override def run(): Unit = {
    startToTv()
    tvToWindmill()
    eatWindmill()
    foodForDinoAtHome()
  }

  def startToTv() = {
    Gyroscope.reset(SensorPort.One)
    Robot.movestraight(440.mm, 245.degreesPerSecond)
    Robot.movestraight(-50.mm, speed = -200.degreesPerSecond)
  }

  def tvToWindmill() = {
    Robot.leftRotation(-50.degrees)
    Robot.movestraight(330.mm+135.mm, speed = 200.degreesPerSecond)
    Robot.rightRotation(48.degrees)
  }

  def eatWindmill() = {
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

    Robot.movestraight(-80.mm, speed = -400.degreesPerSecond)
  }


  def foodForDinoAtHome() = {
    Robot.leftRotation(-90.degrees)
    Robot.movestraight(-40.mm, speed = -200.degreesPerSecond)
    Robot.leftRotation(-200.degrees)
    Robot.movestraight(800.mm, speed = 700.degreesPerSecond)
  }

  def startToWindmill(): Unit = {
    //start with back of robot on very back wall - zero there.
    Gyroscope.reset(SensorPort.One)

    //forward 73 cm = 730 mm
    Robot.movestraight(730.mm, 200.degreesPerSecond)

    //rotate right 45
    Robot.rightRotation(45.degrees)

    //each push 4 cm = 40 mm
    Robot.movestraight(40.mm, 400.degreesPerSecond)
    Robot.movestraight(-40.mm, -400.degreesPerSecond)

    Robot.movestraight(40.mm, 400.degreesPerSecond)
    Robot.movestraight(-40.mm, -400.degreesPerSecond)

    Robot.movestraight(40.mm, 400.degreesPerSecond)
    Robot.movestraight(-40.mm, -400.degreesPerSecond)

    Robot.movestraight(40.mm, 400.degreesPerSecond)
    Robot.movestraight(-40.mm, -400.degreesPerSecond)

  }

}
