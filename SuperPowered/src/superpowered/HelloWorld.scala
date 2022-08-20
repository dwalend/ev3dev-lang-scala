package superpowered

import ev3dev4s.lego.{Display, Sound}
import ev3dev4s.measure.Conversions.IntConversions
import ev3dev4s.sensors.Ev3KeyPad

import java.lang.Runnable
import scala.Unit
import scala.StringContext

object HelloWorld extends Runnable {
  override def run(): Unit = {
    Display.write("Hello World!",0)

    while(Ev3KeyPad.blockUntilAnyKey()._2 != Ev3KeyPad.State.Released) {}
    Display.setLedsTo(Display.LedColor.Green)

    Sound.playBeep(440.Hz,200.ms)

    Display.clearLcd()
    Display.setLedsTo(Display.LedColor.Off)
  }
}
