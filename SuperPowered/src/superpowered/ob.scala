package superpowered

import ev3dev4s.Log
import ev3dev4s.actuators.MotorPort
import ev3dev4s.lego.{Display, Gyroscope, Movement, Sound}
import ev3dev4s.measure.Conversions.IntConversions
import ev3dev4s.measure.Degrees
import ev3dev4s.sensors.{Ev3KeyPad, SensorPort}

import java.lang.Runnable
import scala.{None, Option, StringContext, Unit}

/**
 * A HelloWorld for the Ev3 that demonstrates five ways to communicate with the technicians.
 */
object lastWorld extends Runnable {


  override def run(): Unit = {
    Display.clearLcd()
    Sound.playBeep(220.Hz, 200.ms)

    Robot.movestraight(740.mm,200.degreesPerSecond)
    //    GuidedMission.run

    Display.write("Push Button", 2)

    while (Ev3KeyPad.blockUntilAnyKey()._2 != Ev3KeyPad.State.Released) {
      //don't do anything
    }

  }
}
