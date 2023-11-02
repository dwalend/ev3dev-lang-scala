package ev3dev4s.actuators.examples

import ev3dev4s.{Ev3System => EE}
import ev3dev4s.actuators.Ev3Led

import ev3dev4s.scala2measure.Conversions._

/**
 *
 * @author David Walend
 */
object Ev3LedExample {
  def main(args: Array[String]): Unit = {
    EE.leftLed.writeOff()
    EE.rightLed.writeOff()
    EE.sleep(500.ms)

    EE.leftLed.writeGreen()
    EE.rightLed.writeRed()
    EE.sleep(500.ms)

    EE.leftLed.writeYellow()
    EE.rightLed.writeYellow()
    EE.sleep(500.ms)

    EE.leftLed.writeRed()
    EE.rightLed.writeGreen()
    EE.sleep(500.ms)

    for (_ <- 1 to 3) {
      for (i <- Ev3Led.darkest.round to Ev3Led.brightest.round) {
        val intensity = i.ledIntensity
        EE.leftLed.writeBrightness(red = intensity, green = Ev3Led.brightest - intensity)
        EE.rightLed.writeBrightness(green = intensity, red = Ev3Led.brightest - intensity)
        EE.sleep(10.ms)
      }
    }

    EE.leftLed.writeYellow()
    EE.rightLed.writeYellow()
  }
}
