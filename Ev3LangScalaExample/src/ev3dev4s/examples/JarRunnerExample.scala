package ev3dev4s.examples

import ev3dev4s.Ev3System
import ev3dev4s.actuators.Ev3Led

/**
 * Run via
 *
 * brickrun -r -- java -cp Ev3LangScala.jar ev3dev4s.JarRunner Ev3LangScalaExample.jar ev3dev4s.examples.Simplest
 *
 * @author David Walend
 * @since v0.0.0
 */
object JarRunnerExample extends Runnable {
  override def run(): Unit = {
    Ev3System.leftLed.writeOff()
    Ev3System.rightLed.writeOff()

    for(_ <- 1 to 10){
      for(b <- Ev3Led.darkest to Ev3Led.brightest) {
        Ev3System.leftLed.writeBrightness(b,b)
        Ev3System.rightLed.writeBrightness(b,Ev3Led.brightest - b)
        Thread.sleep(10)
      }
    }
    
  }
}
