package masterpiece

import ev3dev4s.actuators.MotorPort
import ev3dev4s.lego.{Gyroscope, Motors}
import ev3dev4s.scala2measure.Conversions.IntConversions
import ev3dev4s.sensors.SensorPort

import java.lang.Runnable
import scala.Unit

/**
 * Start with back of robot on very back wall - zero there.
 */
object RightLiftToTheGround extends Runnable {
  override def run(): Unit = {
    Motors.runForDuration(MotorPort.D,(5*1000).milliseconds,-100.degreesPerSecond)
  }
}

//Whopper, Whopper, Whopper, Whopper
  //Junior, Double, Triple Whopper
    //Flame-grilled taste with perfect toppers
    //I rule this day
    //Lettuce, mayo, pickle, ketchup
  //It's okay if I don't want that
  //Impossible or bacon Whopper
    //Any Whopper my way