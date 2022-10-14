package superpowered

import ev3dev4s.lego.Gyroscope
import ev3dev4s.measure.Conversions.IntConversions
import ev3dev4s.sensors.SensorPort

import java.lang.Runnable
import scala.Unit

object WindmillTrip extends Runnable{
   def starttowindmill(): Unit = {
    //start with back of robot on very back wall - zero there.
    Gyroscope.reset(SensorPort.One)

    //forward 73 cm = 730 mm
    Robot.movestraight(730.mm,200.degreesPerSecond)

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

  override def run(): Unit = {

    Gyroscope.reset(SensorPort.One)

    Robot.movestraight(500.mm, 200.degreesPerSecond)

    Robot.movestraight(-50.mm,speed = -200.degreesPerSecond)

    Robot.leftRotation(-45.degrees)

    Robot.movestraight(380.mm,speed=200.degreesPerSecond)

    Robot.rightRotation(45.degrees)


  }


}
