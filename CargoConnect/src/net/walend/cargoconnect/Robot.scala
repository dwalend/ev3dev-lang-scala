package net.walend.cargoconnect

import ev3dev4s.{Ev3System, Log}
import ev3dev4s.os.Time
import ev3dev4s.sensors.Ev3Gyroscope
import ev3dev4s.sensors.Ev3ColorSensor
import ev3dev4s.sensors.SensorPort
import ev3dev4s.actuators.{Ev3LargeMotor, Ev3MediumMotor, Motor, MotorCommand, MotorPort}
import ev3dev4s.measure.{Degrees, DegreesPerSecond, DutyCycle, MilliMeters}
import ev3dev4s.actuators.MotorStopCommand
import ev3dev4s.measure.Conversions.*
import net.walend.lessons.{BlackSide, GyroDrive, GyroSetHeading, Move}
import ev3dev4s.actuators.Sound
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.sensors.Ev3KeyPad
import ev3dev4s.sensors.Ev3KeyPad.{Key, State}
import net.walend.cargoconnect.Robot.gyroscope

import scala.annotation.tailrec

object Robot: 
  lazy val gyroscope:Ev3Gyroscope = Ev3System.portsToSensors.get(SensorPort.Two).collectFirst{case g:Ev3Gyroscope => g}
    .get
  lazy val gyroHeading: gyroscope.HeadingMode = gyroscope.headingMode()

  lazy val leftColorSensor:Ev3ColorSensor = Ev3System.portsToSensors.get(SensorPort.One).collect{case cs:Ev3ColorSensor => cs}.get
  lazy val rightColorSensor:Ev3ColorSensor = Ev3System.portsToSensors.get(SensorPort.Four).collect{case cs:Ev3ColorSensor => cs}.get

  lazy val forkMotor:Ev3MediumMotor = Ev3System.portsToMotors.get(MotorPort.C).collect{case m:Ev3MediumMotor => m}.get

  lazy val leftDriveMotor:Ev3LargeMotor = Ev3System.portsToMotors.get(MotorPort.A).collect{case m:Ev3LargeMotor => m}.get
  lazy val rightDriveMotor:Ev3LargeMotor = Ev3System.portsToMotors.get(MotorPort.B).collect{case m:Ev3LargeMotor => m}.get

  @tailrec
  def check():Unit =
    try
      gyroHeading.readHeading()
      leftColorSensor.reflectMode().readReflect()
      rightColorSensor.reflectMode().readReflect()
      forkMotor.readPosition()
      leftDriveMotor.readPosition()
      rightDriveMotor.readPosition()
    catch
      case x =>
        Lcd.set(0,"Gadget Check")
        Lcd.set(1,x.getMessage())
        Sound.playTone(440,200.milliseconds)
        Sound.playTone(220,200.milliseconds)
        Ev3KeyPad.blockUntilAnyKey()
        check()

  def drive(leftSpeed:DegreesPerSecond,rightSpeed:DegreesPerSecond): Unit =
    leftDriveMotor.writeSpeed(leftSpeed)
    rightDriveMotor.writeSpeed(rightSpeed)
    leftDriveMotor.writeCommand(MotorCommand.RUN)
    rightDriveMotor.writeCommand(MotorCommand.RUN)

  def writeDirectDriveMode(): Unit =
    leftDriveMotor.writeCommand(MotorCommand.RUN_DIRECT)
    rightDriveMotor.writeCommand(MotorCommand.RUN_DIRECT)

  def directDrive(leftSpeed:DegreesPerSecond,rightSpeed:DegreesPerSecond): Unit =
    def speedToDutyCycle(speed:DegreesPerSecond,motor:Motor): DutyCycle =
      (100f *speed.value/motor.observedMaxSpeed.value).dutyCyclePercent

    leftDriveMotor.writeDutyCycle(speedToDutyCycle(leftSpeed,leftDriveMotor))
    rightDriveMotor.writeDutyCycle(speedToDutyCycle(rightSpeed,rightDriveMotor))

  sealed case class StopMove(stopCommand:MotorStopCommand) extends Move:
    def move(): Unit =
      leftDriveMotor.writeStopAction(stopCommand)
      rightDriveMotor.writeStopAction(stopCommand)
      leftDriveMotor.writeCommand(MotorCommand.STOP)
      rightDriveMotor.writeCommand(MotorCommand.STOP)

  val Coast: StopMove = StopMove(MotorStopCommand.COAST)
  val Brake: StopMove = StopMove(MotorStopCommand.BRAKE)
  val Hold: StopMove = StopMove(MotorStopCommand.HOLD)

  val wheelDiameter = 94.millimeters //todo try the other size of tires 92 mm vs 94.2 mm
  val wheelCircumference = (wheelDiameter.value * Math.PI.toFloat).mm
  
  def distanceToWheelRotation(distance:MilliMeters):Degrees = (distance.value * 360 / wheelCircumference.value).degrees
  def wheelRotationToDistance(degrees:Degrees):MilliMeters = (wheelCircumference * degrees / 360).mm

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
      while
        val key = Ev3KeyPad.blockUntilAnyKey()
        key match
        case (_,State.Released) => false
        case _ => true
      do ()

  object WarmUp extends Move:
    def move():Unit =
      GyroSetHeading(0.degrees)

      val start = Time.now()
      warmUpTask()
      val afterFirst = Time.now()
      Log.log(s"after first ${afterFirst - start}")

      for (i <- 1 to 100) {
        warmUpTask()
      }

      val afterWarmUp = Time.now()
      warmUpTask()
      val afterLast = Time.now()
      Log.log(s"after warm-up ${afterLast - afterWarmUp}")

      Coast.move()
      Sound.beep()

    def warmUpTask():Unit =
//      LineDriveDistanceForward(0.degrees,Robot.rightColorSensor,BlackSide.Right,Robot.fineSpeed,0.mm).move()
      //does not work        GyroArcForwardRight(0.degrees,0.mm,Robot.fineSpeed).move()
//      GyroDriveDistanceForward(0.degrees,Robot.fineSpeed,0.mm).move()
      GyroDrive.driveForwardDistance(0.degrees,Robot.fineSpeed,0.mm)
      GyroDrive.driveBackwardDistance(0.degrees,-Robot.fineSpeed,-0.mm)
      //todo add arc warm up
      //todo really drive 


