package superpowered

import ev3dev4s.Log
import ev3dev4s.actuators.Sound
import ev3dev4s.lego.Display
import ev3dev4s.measure.Conversions.IntConversions
import ev3dev4s.sensors.Ev3KeyPad

import java.lang.Runnable
import scala.Unit

/**
 * A HelloWorld for the Ev3 that demonstrates five ways to communicate with the technicians.
 * 88mm wheels
 * 52.5
 * 27.6 cm per rotation
 * 4.375 cm
 */
object HelloWorldFancySpeak extends Runnable {
  override def run(): Unit = {

    Sound.playTone(220.Hz, 200.ms)
    Display.write("Hello World!", 0)
    Display.setLedsTo(Display.LedColor.Green)
    Sound.speak("Hello World", "mb-en1+f1")
    Display.write("Push Button", 1)

    while(Ev3KeyPad.blockUntilAnyKey()._2 != Ev3KeyPad.State.Released) {
      //don't do anything
    }
    Log.log("Button pushed")
    Sound.speak("Thank you")

    Display.setLedsTo(Display.LedColor.Off)
  }
}
