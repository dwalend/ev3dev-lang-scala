package superpowered

import ev3dev4s.Log
import ev3dev4s.actuators.{MotorCommand, MotorPort, MotorStopCommand}
import ev3dev4s.lego.{ColorSensor, Display, Gyroscope, Motors, Movement}
import ev3dev4s.measure.Conversions.IntConversions
import ev3dev4s.os.Time
import ev3dev4s.sensors.{Ev3KeyPad, SensorPort}

import java.lang.Runnable
import scala.Unit

/**
 * A HelloWorld for the Ev3 that demonstrates five ways to communicate with the technicians.
 * 88mm wheels
 * 52.5
 * 27.6 cm per rotation
 * 4.375 cm
 */
object GuidedMission extends Runnable {
  override def run(): Unit = {

    while(Ev3KeyPad.blockUntilAnyKey()._2 != Ev3KeyPad.State.Released) {
      //don't do anything
    }
    Movement.setMovementMotorsTo(MotorPort.A, MotorPort.C)
    Gyroscope.reset(SensorPort.One)

    Motors.setSpeed(MotorPort.B, 200.degreesPerSecond)
    Motors.runForDegrees(MotorPort.B, 200.degrees)
    Movement.startMoving(400.degreesPerSecond, 400.degreesPerSecond)
    while (ColorSensor.readReflected(SensorPort.Four)>24.percent) {
       Time.pause(10.milliseconds)
    }
    Movement.stop()
    Movement.move(28.degrees, 400.degreesPerSecond, 400.degreesPerSecond)
    Movement.startMoving(-1000.degreesPerSecond,0.degreesPerSecond)
    while (Gyroscope.readHeading(SensorPort.One) > -90.degrees) {
      Time.pause(10.milliseconds)
    }
    Movement.stop()
    Movement.move(684.degrees, 400.degreesPerSecond, 400.degreesPerSecond)
    Motors.setSpeed(MotorPort.B, -200.degreesPerSecond)
    Motors.runForDegrees(MotorPort.B, -200.degrees)
    Movement.move(-28.degrees, 400.degreesPerSecond, 400.degreesPerSecond)

    Motors.setSpeed(MotorPort.B, 200.degreesPerSecond)
    Motors.runForDegrees(MotorPort.B, 200.degrees)
    Movement.startMoving(0.degreesPerSecond, -1000.degreesPerSecond)
    while (Gyroscope.readHeading(SensorPort.One) < 90.degrees) {
      Time.pause(10.milliseconds)
    }
    Movement.stop()


    Display.setLedsTo(Display.LedColor.Off)
  }
}
