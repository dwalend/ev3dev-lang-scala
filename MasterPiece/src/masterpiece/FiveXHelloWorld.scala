package masterpiece

import ev3dev4s.lego.{Display, Sound}
import ev3dev4s.scala2measure.Conversions.IntConversions
import ev3dev4s.sensors.Ev3KeyPad

import scala.StringContext
import java.lang.Runnable
import scala.Unit

/**
 * A HelloWorld for the Ev3 that demonstrates five ways to communicate with the technicians.
 */
object FiveXHelloWorld extends Runnable {
  override def run(): Unit = {
    Display.clearLcd()
    Display.write(s"Hello World!", 0)

    Display.setLedsTo(Display.LedColor.Off)
    Display.setLedsTo(Display.LedColor.Red,Display.LedColor.Orange)

    Sound.playBeep(220.Hz, 200.ms)
    Display.setLedsTo(Display.LedColor.Orange,Display.LedColor.Red)

    Sound.speak("Hello World")
    Display.setLedsTo(Display.LedColor.Green,Display.LedColor.Green)

    Display.write("Push Button", 3)
    while (Ev3KeyPad.blockUntilAnyKey()._2 != Ev3KeyPad.State.Released) {
      //don't do anything until the key is released
    }
  }
}
