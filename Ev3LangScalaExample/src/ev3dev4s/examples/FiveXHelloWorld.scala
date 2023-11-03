package ev3dev4s.examples

import ev3dev4s.{Ev3System, Log}
import ev3dev4s.lego.{Display, Sound}
import ev3dev4s.scala2measure.Conversions.IntConversions

//noinspection ScalaUnusedSymbol
object FiveXHelloWorld extends Runnable {
  override def run(): Unit = {

    Log.log(s"\nFiveX Hello World started....\n\n")

    Log.log("Clearing the display")
    Display.clearLcd()

    Log.log("Setting the display")
    Display.write("Here We Go!", 3)

    Log.log("Setting LEDs to Orange")
    Display.setLedsTo(Display.LedColor.Orange)

    Log.log("Logging: Hello World")

    Log.log("Display shows: Hello World")
    Display.write("Hello World", 0)

    Log.log("Speaking aloud: Hello World")
    Sound.speak("Hello World")

    Log.log("Beeping aloud")
    Sound.playBeep(400.Hz, 150.ms)

    Log.log("Setting LEDs to Green")
    Display.setLedsTo(Display.LedColor.Green)

    Log.log("Demanding that you push a button")
    Display.write("Push Button", 3)
    Ev3System.keyPad.blockUntilAnyKey()
    Display.write("Thank you! ", 3)

    Log.log("Setting LEDs to Red")
    Display.setLedsTo(Display.LedColor.Red)

    Log.log("Speaking aloud: You have been greeted")
    Sound.speak("You have been greeted")
    Display.write("Cool Innit?", 3)
  }
}
