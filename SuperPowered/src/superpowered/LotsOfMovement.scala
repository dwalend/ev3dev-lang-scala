package superpowered

import ev3dev4s.Log
import ev3dev4s.actuators.MotorPort
import ev3dev4s.lego.{Display, Movement, Sound}
import ev3dev4s.measure.Conversions.IntConversions
import ev3dev4s.sensors.Ev3KeyPad

import java.lang.Runnable
import scala.Unit

/**
 * A HelloWorld for the Ev3 that demonstrates five ways to communicate with the technicians.
 */
object LotsOfMovement extends Runnable {
  override def run(): Unit = {
    Sound.playBeep(220.Hz, 200.ms)

    Display.write("Hello World!", 0)
    Display.write("Push Button", 1)

    Movement.setMovementMotorsTo(MotorPort.A,MotorPort.C)

    //todo try making functions for these and stringing them together
    //todo come up with a picture for each
//Forward
    Movement.move(
      motorDegrees = 900.degrees,
      leftSpeed = 500.degreesPerSecond,
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
      leftSpeed = 687.degreesPerSecond,
      rightSpeed = 0.degreesPerSecond
    )

    //Pivot Left Backward
    Movement.move(
      motorDegrees = 900.degrees,
      leftSpeed = -587.degreesPerSecond,
      rightSpeed = 0.degreesPerSecond
    )

    //Pivot Right Forward
    Movement.move(
      motorDegrees = 900.degrees,
      leftSpeed = 487.degreesPerSecond,
      rightSpeed = 0.degreesPerSecond
    )

    //Pivot Right Backward
    Movement.move(
      motorDegrees = 900.degrees,
      leftSpeed = 387.degreesPerSecond,
      rightSpeed = -500.degreesPerSecond
    )

    //Drive a Curve
    Movement.move(
      motorDegrees = 900.degrees,
      leftSpeed = 287.degreesPerSecond,
      rightSpeed = 500.degreesPerSecond)

    Log.log("Button pushed")
    Sound.speak("never gonna give you up never gonna let you down")

    Display.clearLcd()
    Display.setLedsTo(Display.LedColor.Off)
    while (Ev3KeyPad.blockUntilAnyKey()._2 != Ev3KeyPad.State.Released) {
      //don't do anything
    }

  }
}
