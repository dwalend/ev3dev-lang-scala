package superpowered

import ev3dev4s.lego.Gyroscope
import ev3dev4s.measure.Conversions.IntConversions
import ev3dev4s.sensors.SensorPort

import java.lang.Runnable
import scala.Unit

object FeedLavaBath extends Runnable{
  override def run(): Unit = {
    Gyroscope.reset(SensorPort.One)
    Robot.movestraight(500.mm, 300.degreesPerSecond)
    Robot.leftRotation(-30.degrees)
    Robot.movestraight(350.mm, 300.degreesPerSecond)
    Robot.movestraight(-350.mm, -300.degreesPerSecond)
    Robot.rightRotation(0.degrees)
    Robot.movestraight(-450.mm, -300.degreesPerSecond)
  }
}
//-30
//450
//350
