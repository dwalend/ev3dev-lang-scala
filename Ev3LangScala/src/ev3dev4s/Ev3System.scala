package ev3dev4s

import ev3dev4s.actuators.{Ev3Led, Motor, MotorPort, MotorPortScanner, Sound}
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.sensors.{Ev3Battery, Ev3KeyPad, Sensor, SensorPort, SensorPortScanner}
import ev3dev4s.scala2measure.MilliSeconds

/**
 * @author David Walend
 * @since v0.0.0
 */
object Ev3System {

  lazy val leftLed: Ev3Led = Ev3Led.Left
  lazy val rightLed: Ev3Led = Ev3Led.Right

  lazy val keyPad: Ev3KeyPad.type = Ev3KeyPad
  lazy val lcd: Lcd.type = Lcd
  lazy val sound: Sound.type = Sound
  lazy val battery: Ev3Battery.type = Ev3Battery

  lazy val portsToMotors: Map[MotorPort, Motor] = MotorPortScanner.scanMotors
  lazy val portsToSensors: Map[SensorPort, Sensor[_]] = SensorPortScanner.scanSensors

  def sleep(duration: MilliSeconds): Unit = {
    Thread.sleep(duration.round.toLong)
  }

  def motorA(): Motor = portsToMotors.apply(MotorPort.A)

  def motorB(): Motor = portsToMotors.apply(MotorPort.B)

  def motorC(): Motor = portsToMotors.apply(MotorPort.C)

  def motorD(): Motor = portsToMotors.apply(MotorPort.D)

  def liftMotor(): Motor = motorA()

  def leftWheel(): Motor = motorB()

  def rightWheel(): Motor = motorC()

  def describeSensors(): String = {
    val sensors = Ev3System.portsToSensors
    sensors.mkString("\n")
  }

  def describeMotors(): String = {
    val motors = Ev3System.portsToMotors
    motors.mkString("\n")
  }

}
