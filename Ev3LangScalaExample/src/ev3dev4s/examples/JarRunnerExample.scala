package ev3dev4s.examples

import ev3dev4s.Ev3System
import ev3dev4s.actuators.Ev3Led

import ev3dev4s.measure.Conversions.ledIntensity

/**
 * Run via
 *
 * brickrun -r -- java -cp Ev3LangScala.jar ev3dev4s.JarRunner Ev3LangScalaExample.jar ev3dev4s.examples.Simplest
 *
 * @author David Walend
 * @since v0.0.0
 */
object JarRunnerExample extends Runnable:
  override def run(): Unit =
    Ev3System.leftLed.writeOff()
    Ev3System.rightLed.writeOff()

    for _ <- 1 to 10 do
      for b <- Ev3Led.darkest.round to Ev3Led.brightest.round do
        val brightness = b.ledIntensity
        Ev3System.leftLed.writeBrightness(brightness,brightness)
        Ev3System.rightLed.writeBrightness(brightness,Ev3Led.brightest - brightness)
        Thread.sleep(10)