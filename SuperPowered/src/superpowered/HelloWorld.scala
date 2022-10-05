package superpowered

import ev3dev4s.Log
import ev3dev4s.actuators.MotorPort
import ev3dev4s.lego.{Display, Movement, Sound}
import ev3dev4s.measure.Conversions.IntConversions
import ev3dev4s.sensors.Ev3KeyPad

import java.lang.Runnable
import scala.{None, Option, StringContext, Unit}

/**
 * A HelloWorld for the Ev3 that demonstrates five ways to communicate with the technicians.
 */
object HelloWorld extends Runnable {
  override def run(): Unit = {
    Sound.playBeep(220.Hz, 200.ms)
    Display.write("Hello World!", 0)

    Robot.movestraight(320.mm,500.degreesPerSecond)
    //    GuidedMission.run()
/*
    Movement.setMovementMotorsTo(MotorPort.A,MotorPort.C)

    //todo try making functions for these and stringing them together
    //todo come up with a picture for each
//Forward
    Movement.move(
      motorDegrees = 900.degrees,
      leftSpeed = ,
      rightSpeed = 500.degreesPerSecond
    )
//Backward
    Movement.move(
      motorDegrees = 900.degrees,
      leftSpeed = -500.degreesPerSecond,
      rightSpeed = -500.degreesPerSecond
    )

    //Rotate Left
    Movement.move(
      motorDegrees = 900.degrees,
      leftSpeed = -500.degreesPerSecond,
      rightSpeed = 500.degreesPerSecond
    )

    //Rotate Right
    Movement.move(
      motorDegrees = 900.degrees,
      leftSpeed = 500.degreesPerSecond,
      rightSpeed = -500.degreesPerSecond
    )

    //Pivot Left Forward
    Movement.move(
      motorDegrees = 900.degrees,
      leftSpeed = 500.degreesPerSecond,
      rightSpeed = 0.degreesPerSecond
    )

    //Pivot Left Backward
    Movement.move(
      motorDegrees = 900.degrees,
      leftSpeed = -500.degreesPerSecond,
      rightSpeed = 0.degreesPerSecond
    )

    //Pivot Right Forward
    Movement.move(
      motorDegrees = 900.degrees,
      leftSpeed = 500.degreesPerSecond,
      rightSpeed = 0.degreesPerSecond
    )

    //Pivot Right Backward
    Movement.move(
      motorDegrees = 900.degrees,
      leftSpeed = 0.degreesPerSecond,
      rightSpeed = -500.degreesPerSecond
    )

    //Drive a Curve
    Movement.move(
      motorDegrees = 900.degrees,
      leftSpeed = 200.degreesPerSecond,
      rightSpeed = 500.degreesPerSecond)

    Log.log("Button pushed")
    Sound.speak("cat no cat no cat")
*/
    Display.clearLcd()
    Display.setLedsTo(Display.LedColor.Off)

    Display.write("Push Button", 1)

    while (Ev3KeyPad.blockUntilAnyKey()._2 != Ev3KeyPad.State.Released) {
      //don't do anything
    }

  }
}
