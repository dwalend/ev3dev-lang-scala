package ev3dev4s.examples

import ev3dev4s.Log
import ev3dev4s.lego.{Display, Sound}
import ev3dev4s.scala2measure.Conversions.IntConversions

//noinspection ScalaUnusedSymbol
object FiveXHelloWorld extends Runnable {
  override def run(): Unit = {
    Display.setLedsTo(Display.LedColor.Orange)
    Log.log("Hello World")
    Display.write("Hello World",1)
    Sound.speak("Hello World")
    Sound.playBeep(200.Hz,100.ms)
    Display.setLedsTo(Display.LedColor.Green)
  }
}
