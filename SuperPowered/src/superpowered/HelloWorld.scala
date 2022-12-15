package superpowered

import ev3dev4s.lego.{Display, Sound}
import ev3dev4s.measure.Conversions.IntConversions

import java.lang.Runnable
import scala.{StringContext, Unit}

object HelloWorld extends Runnable {

  override def run(): Unit = {
    Display.clearLcd()
    Sound.playBeep(220.Hz, 200.ms)
    Menu.run()
  }
}
