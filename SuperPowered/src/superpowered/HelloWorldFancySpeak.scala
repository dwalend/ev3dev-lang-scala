package superpowered

import ev3dev4s.Log
import ev3dev4s.actuators.{MotorPort, Sound}
import ev3dev4s.lego.Display
import ev3dev4s.measure.Conversions.IntConversions
import ev3dev4s.sensors.{Ev3KeyPad, SensorPort}
import ev3dev4s.lego.Motors
import ev3dev4s.lego.Movement
import ev3dev4s.lego.Gyroscope
import ev3dev4s.lego.ColorSensor

import java.lang.Runnable
import scala.Unit

/**
 * A HelloWorld for the Ev3 that demonstrates five ways to communicate with the technicians.
 * 88mm wheels
 * 52.5
 * 27.6 cm per rotation
 * 4.375 cm
 */
object HelloWorldFancySpeak extends Runnable {
  override def run(): Unit = {
    while(Ev3KeyPad.blockUntilAnyKey()._2 != Ev3KeyPad.State.Released) {
      //don't do anything
    }
    Log.log("Button pushed")
    Movement.setMovementMotorsTo(MotorPort.A, MotorPort.B)
    Gyroscope.reset(SensorPort.One)
    Motors.runForDegrees(MotorPort.C, 200.degrees)
    while (ColorSensor.readReflected(SensorPort.Two)>24.percent)
      Movement.startMoving(40.degreesPerSecond, 40.degreesPerSecond) //Time.pause(some milliseconds)
    Movement.stop()
    Movement.move(28.degrees, 40.degreesPerSecond, 40.degreesPerSecond)
    while (Gyroscope.readHeading(SensorPort.Four)> -90.degrees)
      Movement.startMoving(-100.degreesPerSecond,0.degreesPerSecond)
    Movement.stop()
    Movement.move(684.degrees, 40.degreesPerSecond, 40.degreesPerSecond)
    Motors.runForDegrees(MotorPort.C, -200.degrees)
    Movement.move(-28.degrees, 40.degreesPerSecond, 40.degreesPerSecond)
    Motors.runForDegrees(MotorPort.C, 200.degrees)
    while (Gyroscope.readHeading(SensorPort.Four) < 90.degrees)
      Movement.startMoving(0.degreesPerSecond, -100.degreesPerSecond)
    Movement.stop()


    Display.setLedsTo(Display.LedColor.Off)
  }
}
