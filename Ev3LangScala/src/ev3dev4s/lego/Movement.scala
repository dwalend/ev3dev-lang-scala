package ev3dev4s.lego

import ev3dev4s.actuators.{Motor, MotorCommand, MotorPort, MotorStopCommand}
import ev3dev4s.lego.Motors.handleUnpluggedMotor
import ev3dev4s.scala2measure.{Degrees, DegreesPerSecond, MilliSeconds, Percent, Unitless}
import ev3dev4s.scala2measure.Conversions._

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Movement {

  private var leftMotorPort: Option[MotorPort] = None
  private var rightMotorPort: Option[MotorPort] = None

  def setMovementMotorsTo(left: MotorPort, right: MotorPort): Unit = {
    leftMotorPort = Option(left)
    rightMotorPort = Option(right)
  }

  def leftMotor: Option[Motor] = leftMotorPort.flatMap(Motors.motors.get(_))

  def rightMotor: Option[Motor] = rightMotorPort.flatMap(Motors.motors.get(_))
  
  /**
   * Turn the pair of motors the number of degrees at the same speed.
   *
   * @param motorDegrees The absolute distance to turn the motor
   * @param speed        Left motor speed. Negative is backwards.
   */
  def move(motorDegrees: Degrees, speed: DegreesPerSecond): Unit = handleUnpluggedMotor {
    leftMotor.foreach(_.writeGoalPosition(relativeGoalPosition(motorDegrees, speed)))
    rightMotor.foreach(_.writeGoalPosition(relativeGoalPosition(motorDegrees, speed)))
    leftMotor.foreach(_.writeSpeed(speed))
    rightMotor.foreach(_.writeSpeed(speed))
    leftMotor.foreach(_.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION))
    rightMotor.foreach(_.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION))

    leftMotor.foreach(Motors.watchForStop)
    rightMotor.foreach(Motors.watchForStop)
  }

  private def relativeGoalPosition(motorDegrees: Degrees, speed: DegreesPerSecond): Degrees = {
    (motorDegrees.abs * speed.sign).degrees
  }

  def startMoving(leftSpeed: DegreesPerSecond, rightSpeed: DegreesPerSecond): Unit = handleUnpluggedMotor {
    leftMotor.foreach(_.writeSpeed(leftSpeed))
    rightMotor.foreach(_.writeSpeed(rightSpeed))
    leftMotor.foreach(_.writeCommand(MotorCommand.RUN))
    rightMotor.foreach(_.writeCommand(MotorCommand.RUN))
  }

  /**
   * Turn the pair of motors the number of degrees at two different speeds.
   *
   * @param motorDegrees The absolute distance to turn the motor
   * @param leftSpeed    Left motor speed. Negative is backwards.
   * @param rightSpeed   Right motor speed. Negative is backwards.
   */
  def move(motorDegrees: Degrees, leftSpeed: DegreesPerSecond, rightSpeed: DegreesPerSecond): Unit = handleUnpluggedMotor {
    val (watched, notWatched) = if (leftSpeed.abs > rightSpeed.abs) (leftMotor, rightMotor)
    else (rightMotor, leftMotor)
    leftMotor.foreach(_.writeGoalPosition(relativeGoalPosition(motorDegrees, leftSpeed)))
    rightMotor.foreach(_.writeGoalPosition(relativeGoalPosition(motorDegrees, rightSpeed)))
    leftMotor.foreach(_.writeSpeed(leftSpeed))
    rightMotor.foreach(_.writeSpeed(rightSpeed))
    leftMotor.foreach(_.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION))
    rightMotor.foreach(_.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION))

    watched.foreach(Motors.watchForStop)
    notWatched.foreach(_.writeCommand(MotorCommand.STOP))
  }

  /**
   * Turn the pair of motors the number of degrees at two different speeds.
   *
   * @param duration   The time to turn the motor before stopping it.
   * @param leftSpeed  Left motor speed. Negative is backwards.
   * @param rightSpeed Right motor speed. Negative is backwards.
   */
  def moveDuration(duration: MilliSeconds, leftSpeed: DegreesPerSecond, rightSpeed: DegreesPerSecond): Unit = handleUnpluggedMotor {
    val (watched, notWatched) = if (leftSpeed.abs > rightSpeed.abs) (leftMotor, rightMotor)
    else (rightMotor, leftMotor)
    leftMotor.foreach(_.writeDuration(duration))
    rightMotor.foreach(_.writeDuration(duration))
    leftMotor.foreach(_.writeSpeed(leftSpeed))
    rightMotor.foreach(_.writeSpeed(rightSpeed))
    leftMotor.foreach(_.writeCommand(MotorCommand.RUN_TIME))
    rightMotor.foreach(_.writeCommand(MotorCommand.RUN_TIME))

    watched.foreach(Motors.watchForStop)
    notWatched.foreach(_.writeCommand(MotorCommand.STOP))
  }

  private def steerMotors(steer: Percent): (Option[Motor], Option[Motor]) = if (steer < 0.percent) (leftMotor, rightMotor)
  else (rightMotor, leftMotor)

  private def innerMotorProportion(steer: Percent): Unitless =
    ((100f - 2f * steer.abs.v) / 100f).unitless


  def moveSteer(steer: Percent, speed: DegreesPerSecond, degrees: Degrees): Unit = handleUnpluggedMotor {
    val (innerMotor, outerMotor) = steerMotors(steer)
    val innerSpeed: DegreesPerSecond = (speed.v * innerMotorProportion(steer).v).degreesPerSecond
    val innerDegrees = (degrees.v * innerMotorProportion(steer).v).degrees

    outerMotor.foreach(_.writeGoalPosition(degrees))
    innerMotor.foreach(_.writeGoalPosition(innerDegrees))
    outerMotor.foreach(_.writeSpeed(speed))
    innerMotor.foreach(_.writeSpeed(innerSpeed))
    outerMotor.foreach(_.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION))
    innerMotor.foreach(_.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION))

    leftMotor.foreach(Motors.watchForStop)
    rightMotor.foreach(Motors.watchForStop)
  }

  def startMovingSteer(steer: Percent, speed: DegreesPerSecond): Unit = handleUnpluggedMotor {
    val (innerMotor, outerMotor) = steerMotors(steer)
    val innerSpeed: DegreesPerSecond = (speed.v * innerMotorProportion(steer).v).degreesPerSecond
    outerMotor.foreach(_.writeSpeed(speed))
    innerMotor.foreach(_.writeSpeed(innerSpeed))
    outerMotor.foreach(_.writeCommand(MotorCommand.RUN))
    innerMotor.foreach(_.writeCommand(MotorCommand.RUN))
  }


  def stop(): Unit = handleUnpluggedMotor {
    leftMotor.foreach(_.writeCommand(MotorCommand.STOP))
    rightMotor.foreach(_.writeCommand(MotorCommand.STOP))
  }


  def atStop(stopCommand: MotorStopCommand): Unit = handleUnpluggedMotor {
    leftMotor.foreach(_.writeStopAction(stopCommand))
    rightMotor.foreach(_.writeStopAction(stopCommand))
  }
}
  