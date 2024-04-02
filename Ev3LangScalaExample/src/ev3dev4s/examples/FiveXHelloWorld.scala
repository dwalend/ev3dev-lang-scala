package ev3dev4s.examples

import ev3dev4s.{Ev3System, Log}
import ev3dev4s.lego.{Display, Sound}
import ev3dev4s.measured.dimension.Dimensions.{hertz, second, `*`, given}
import ev3dev4s.measured.dimension.{Frequency,milli}

//noinspection ScalaUnusedSymbol
object FiveXHelloWorld extends Runnable {
  override def run(): Unit = {
    Display.clearLcd()
    Display.setLedsTo(Display.LedColor.Orange)
    Log.log("Hello World")
    Display.write("Hello World",0)
    Sound.speak("Hello World")
    Sound.playBeep(200 * hertz, 100 * milli(second))
    Display.setLedsTo(Display.LedColor.Green)
    Display.write("Push Button",3)
    Ev3System.keyPad.blockUntilAnyKey()
  }
}
