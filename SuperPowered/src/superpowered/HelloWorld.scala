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

    val key: (Ev3KeyPad.Key, Ev3KeyPad.State) = Ev3KeyPad.blockUntilAnyKey()
    Display.write(s"${key._1.name} ${key._2.name}",1)
    Display.setLedsTo(Display.LedColor.Orange)

    Sound.playBeep(440.Hz,200.ms)

    Display.clearLcd()
    Display.setLedsTo(Display.LedColor.Green)
  }
}
