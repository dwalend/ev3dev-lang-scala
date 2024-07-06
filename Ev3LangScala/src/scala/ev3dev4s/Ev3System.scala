package ev3dev4s

import ev3dev4s.actuators.{Ev3Led, Motor, MotorPort, MotorPortScanner, Sound}
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.sensors.{Ev3Battery, Ev3KeyPad, Sensor, SensorPort, SensorPortScanner}

/**
 * @author David Walend
 * @since v0.0.0
 */
object Ev3System {

  lazy val leftLed: Ev3Led = Ev3Led.Left
  lazy val rightLed: Ev3Led = Ev3Led.Right

  lazy val keyPad = Ev3KeyPad
  lazy val lcd = Lcd
  lazy val sound = Sound
  lazy val battery = Ev3Battery

  lazy val portsToMotors: Map[MotorPort, Motor] = MotorPortScanner.scanMotors
  lazy val portsToSensors: Map[SensorPort, Sensor[_]] = SensorPortScanner.scanSensors

}