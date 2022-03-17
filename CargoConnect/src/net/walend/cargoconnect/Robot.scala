package net.walend.cargoconnect

import ev3dev4s.Ev3System
import ev3dev4s.sensors.Ev3Gyroscope
import ev3dev4s.sensors.Ev3ColorSensor
import ev3dev4s.sensors.SensorPort
import ev3dev4s.actuators.{Ev3MediumMotor,MotorPort,MotorCommand}
import ev3dev4s.actuators.Ev3LargeMotor
import ev3dev4s.measure.DegreesPerSecond
import ev3dev4s.actuators.MotorStopCommand
import ev3dev4s.measure.Conversions.*

import net.walend.lessons.Move
import ev3dev4s.actuators.Sound
import ev3dev4s.sensors.Ev3KeyPad

object Robot: 
  val gyroscope:Ev3Gyroscope = Ev3System.portsToSensors.values.collectFirst{case g:Ev3Gyroscope => g}.get
  val gyroHeading = gyroscope.headingMode()

  val leftColorSensor:Ev3ColorSensor = Ev3System.portsToSensors.get(SensorPort.One).collect{case cs:Ev3ColorSensor => cs}.get
  val rightColorSensor:Ev3ColorSensor = Ev3System.portsToSensors.get(SensorPort.Four).collect{case cs:Ev3ColorSensor => cs}.get

  val forkMotor:Ev3MediumMotor = Ev3System.portsToMotors.get(MotorPort.C).collect{case m:Ev3MediumMotor => m}.get

  val leftDriveMotor:Ev3LargeMotor = Ev3System.portsToMotors.get(MotorPort.A).collect{case m:Ev3LargeMotor => m}.get
  val rightDriveMotor:Ev3LargeMotor = Ev3System.portsToMotors.get(MotorPort.B).collect{case m:Ev3LargeMotor => m}.get

  def drive(leftSpeed:DegreesPerSecond,rightSpeed:DegreesPerSecond) = 
    leftDriveMotor.writeSpeed(leftSpeed)
    rightDriveMotor.writeSpeed(rightSpeed)
    leftDriveMotor.writeCommand(MotorCommand.RUN)
    rightDriveMotor.writeCommand(MotorCommand.RUN)

  sealed case class StopMove(stopCommand:MotorStopCommand) extends Move:
    def move() = 
      leftDriveMotor.writeStopAction(stopCommand)
      rightDriveMotor.writeStopAction(stopCommand)
      leftDriveMotor.writeCommand(MotorCommand.STOP)
      rightDriveMotor.writeCommand(MotorCommand.STOP)

  val Coast = StopMove(MotorStopCommand.COAST)
  val Brake = StopMove(MotorStopCommand.BRAKE)
  val Hold = StopMove(MotorStopCommand.HOLD)   

  val wheelDiameter = 94.millimeters //todo try the other size of tires 92 mm vs 94.2 mm
  val wheelCircumference = (wheelDiameter.value * Math.PI.toFloat).mm
  val wheelToWheel = 23.studs //todo is that right?

  val driveAxelToExtendedFork = 33.studs

  val cruiseSpeed = 500.degreesPerSecond  //todo figure out a good cruise speed
  val fineSpeed = 200.degreesPerSecond
  val noSlipSpeed = 5.degreesPerSecond 

  object Beep extends Move:
    def move():Unit = 
      Sound.beep()

  object StopAndWaitForButton extends Move:
    def move():Unit = 
      Hold.move()
      Ev3KeyPad.blockUntilAnyKey()