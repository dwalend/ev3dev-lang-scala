package superpowered

import ev3dev4s.Log
import ev3dev4s.lego.{Display, Sound}
import ev3dev4s.scala2measure.Conversions.IntConversions
import ev3dev4s.sensors.Ev3KeyPad

import java.lang.Runnable
import scala.Unit

/**
 * A HelloWorld for the Ev3 that demonstrates five ways to communicate with the technicians.
 */
object Orange extends Runnable {
  override def run(): Unit = {
    Sound.playBeep(220.Hz,200.ms)
    Display.write("Hello World!",0)
    Display.setLedsTo(Display.LedColor.Red)
    Sound.speak("Hello")
    Display.write("Push Button",1)

    while(Ev3KeyPad.blockUntilAnyKey()._2 != Ev3KeyPad.State.Released) {
      //don't do anything
    }
    Log.log("Button pushed")
    Sound.speak("hi! ! ! ! ! ! !")
    Display.clearLcd()
    Display.setLedsTo(Display.LedColor.Off)
  }
}
