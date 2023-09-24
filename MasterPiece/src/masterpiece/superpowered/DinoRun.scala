package masterpiece.superpowered

import ev3dev4s.scala2measure.Conversions.IntConversions
import masterpiece.Robot

import java.lang.Runnable
import scala.Unit

object DinoRun extends Runnable{

  def run(): Unit = {

    Robot.movestraight(1840.mm, 400.degreesPerSecond)

  }
}