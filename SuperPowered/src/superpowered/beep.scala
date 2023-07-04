package superpowered

import ev3dev4s.lego.{Display, Sound}
import ev3dev4s.scala2measure.Conversions.IntConversions
import ev3dev4s.sensors.Ev3KeyPad

import java.lang.Runnable
import scala.Unit

object beep extends Runnable {
  override def run(): Unit = {
    Sound.playBeep(220.Hz,200.ms)
    Display.write("Hello World!",0)
    Display.setLedsTo(Display.LedColor.Green)
    Sound.speak("Hello World")
    Display.write("Push Button",1)

    while(Ev3KeyPad.blockUntilAnyKey()._2 != Ev3KeyPad.State.Released) {
      //don't do anything
    }

    Sound.speak( "am I a cat cat cat cat cat cat cat ")

    Display.clearLcd()
    Display.setLedsTo(Display.LedColor.Off)
  }
}
