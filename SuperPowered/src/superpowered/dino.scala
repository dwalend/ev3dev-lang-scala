package superpowered

import ev3dev4s.measure.Conversions.IntConversions

import java.lang.Runnable
import scala.Unit

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object dinosor extends Runnable{
  override def run(): Unit = {
 Robot.movestraight(35.mm, speed=200.degreesPerSecond)


    

  }
}
