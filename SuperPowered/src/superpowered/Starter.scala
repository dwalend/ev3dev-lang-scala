package superpowered

import ev3dev4s.lego.{Display, Sound}
import ev3dev4s.measure.Conversions.IntConversions
import ev3dev4s.sensors.Ev3KeyPad

object Starter extends Runnable {
  override def run(): Unit = {
    Display.write("Simple Start",0)

    val key: (Ev3KeyPad.Key, Ev3KeyPad.State) = Ev3KeyPad.blockUntilAnyKey()
    Display.write("Key pushed",1)
    Display.setLedsTo(Display.LedColor.Orange)

    Sound.playBeep(440,200.ms)

    Display.clearLcd()
    Display.setLedsTo(Display.LedColor.Green)
  }
}
