package superpowered

import ev3dev4s.measure.Conversions.IntConversions

import java.lang.Runnable
import scala.Unit

object DinoRun extends Runnable{

  def run(): Unit = {

    Robot.movestraight(1840.mm, 400.degreesPerSecond)

  }
}
//1340
