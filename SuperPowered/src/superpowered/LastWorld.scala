package superpowered

import ev3dev4s.lego.{Display, Sound}
import ev3dev4s.measure.Conversions.IntConversions

import java.lang.Runnable
import scala.{StringContext, Unit}

/**
 * A HelloWorld for the Ev3 that demonstrates five ways to communicate with the technicians.
 */
object LastWorld extends Runnable {


  override def run(): Unit = {
    Display.clearLcd()
    Sound.playBeep(220.Hz, 200.ms)

    Robot.movestraight(740.mm,200.degreesPerSecond)
  }
}
