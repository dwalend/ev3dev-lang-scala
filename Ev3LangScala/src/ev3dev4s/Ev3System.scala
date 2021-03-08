package ev3dev4s

import ev3dev4s.actuators.{Ev3Led, Motor, MotorPort, MotorPortScanner}
import ev3dev4s.sensors.{Ev3KeyPad, Sensor, SensorPort, SensorPortScanner}

/**
 * @author David Walend
 * @since v0.0.0
 */
object Ev3System {

  lazy val leftLed: Ev3Led = Ev3Led.LEFT
  lazy val rightLed: Ev3Led = Ev3Led.RIGHT

  lazy val keyPad: Ev3KeyPad.type = Ev3KeyPad

  lazy val portsToMotors: Map[MotorPort, Motor] = MotorPortScanner.scanMotorsDir
  lazy val portsToSensors: Map[SensorPort, Sensor] = SensorPortScanner.scanSensorsDir

  //todo LCD
  //todo sound
  //todo battery

}
