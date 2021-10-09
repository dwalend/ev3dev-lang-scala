package ev3dev4s.actuators.examples

import ev3dev4s.Ev3System
import ev3dev4s.actuators.Ev3Led

/**
 *
 * @author David Walend
 */
object Ev3LedExample {
  def main(args: Array[String]): Unit = {
    Ev3System.leftLed.writeOff()
    Ev3System.rightLed.writeOff()
    Thread.sleep(500)

    Ev3System.leftLed.writeGreen()
    Ev3System.rightLed.writeRed()
    Thread.sleep(500)

    Ev3System.leftLed.writeYellow()
    Ev3System.rightLed.writeYellow()
    Thread.sleep(500)

    Ev3System.leftLed.writeRed()
    Ev3System.rightLed.writeGreen()
    Thread.sleep(500)

    for _ <- 1 to 3 do {
      for i <- Ev3Led.darkest to Ev3Led.brightest do {
        Ev3System.leftLed.writeBrightness(i, Ev3Led.brightest - i)
        Ev3System.rightLed.writeBrightness(Ev3Led.brightest - i, i)
        Thread.sleep(10)
      }
    }
    Ev3System.leftLed.writeYellow()
    Ev3System.rightLed.writeYellow()
  }
}
