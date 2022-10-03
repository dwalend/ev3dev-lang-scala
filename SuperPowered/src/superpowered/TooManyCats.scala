package superpowered

import ev3dev4s.Log
import ev3dev4s.lego.{Display, Sound}
import ev3dev4s.measure.Conversions.IntConversions
import ev3dev4s.sensors.Ev3KeyPad

import java.lang.Runnable
import scala.Unit
import scala.StringContext
import scala.collection.immutable.Seq

/**
 * Says cat too many times.
 */
object TooManyCats extends Runnable {
  override def run(): Unit = {
    Sound.playBeep(220.Hz,200.ms)
    Display.write("Hello World!",0)
    Display.setLedsTo(Display.LedColor.Green)
    Sound.speak("Hello World")
    Display.write("Push Button",1)

    val tooManyCats = Seq.fill(9)("cat").mkString(" ")

    Sound.speak(s"I'm a $tooManyCats")

    while(Ev3KeyPad.blockUntilAnyKey()._2 != Ev3KeyPad.State.Released) {
      //don't do anything
    }
    Log.log("Button pushed")
    Sound.speak("Thank you")

    Display.clearLcd()
    Display.setLedsTo(Display.LedColor.Off)
  }
}