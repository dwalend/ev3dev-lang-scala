package net.walend.lego

import ev3dev4s.actuators.{Motor, MotorCommand, MotorPort, MotorStopCommand}
import ev3dev4s.measure.{Degrees, DegreesPerSecond, Percent, Unitless}
import net.walend.lego.Motors
import ev3dev4s.measure.Conversions._

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Movement {

  private var leftMotor: Option[Motor] = None
  private var rightMotor: Option[Motor] = None

  def setMovementMotorsTo(left: MotorPort, right: MotorPort): Unit = {
    leftMotor = Motors.motors.get(left)
    rightMotor = Motors.motors.get(right)
  }


  def move(degrees: Degrees, speed: DegreesPerSecond): Unit = {
    leftMotor.foreach(_.writeGoalPosition(degrees))
    rightMotor.foreach(_.writeGoalPosition(degrees))
    leftMotor.foreach(_.writeSpeed(speed))
    rightMotor.foreach(_.writeSpeed(speed))
    leftMotor.foreach(_.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION))
    rightMotor.foreach(_.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION))

    leftMotor.foreach(Motors.watchForStop)
    rightMotor.foreach(Motors.watchForStop)
  }


  def startMoving(leftSpeed: DegreesPerSecond, rightSpeed: DegreesPerSecond): Unit = {
    leftMotor.foreach(_.writeSpeed(leftSpeed))
    rightMotor.foreach(_.writeSpeed(rightSpeed))
    leftMotor.foreach(_.writeCommand(MotorCommand.RUN))
    rightMotor.foreach(_.writeCommand(MotorCommand.RUN))
  }


  def move(degrees: Degrees, leftSpeed: DegreesPerSecond, rightSpeed: DegreesPerSecond): Unit = {
    val (watched, notWatched) = if (leftSpeed.abs > rightSpeed.abs) (leftMotor, rightMotor)
    else (rightMotor, leftMotor)
    leftMotor.foreach(_.writeGoalPosition(degrees))
    rightMotor.foreach(_.writeGoalPosition(degrees))
    leftMotor.foreach(_.writeSpeed(leftSpeed))
    rightMotor.foreach(_.writeSpeed(rightSpeed))
    leftMotor.foreach(_.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION))
    rightMotor.foreach(_.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION))

    watched.foreach(Motors.watchForStop)
    notWatched.foreach(_.writeCommand(MotorCommand.STOP))
  }

  private def steerMotors(steer: Percent) = if (steer < 0.percent) (leftMotor, rightMotor)
  else (rightMotor, leftMotor)

  private def innerMotorProportion(steer: Percent): Unitless =
    ((100f - 2f * steer.abs.value) / 100f).unitless


  def moveSteer(steer: Percent, speed: DegreesPerSecond, degrees: Degrees): Unit = {
    val (innerMotor, outerMotor) = steerMotors(steer)
    val innerSpeed = (speed.value * innerMotorProportion(steer).value).degreesPerSecond
    val innerDegrees = (degrees.value * innerMotorProportion(steer).value).degrees

    outerMotor.foreach(_.writeGoalPosition(degrees))
    innerMotor.foreach(_.writeGoalPosition(innerDegrees))
    outerMotor.foreach(_.writeSpeed(speed))
    innerMotor.foreach(_.writeSpeed(innerSpeed))
    outerMotor.foreach(_.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION))
    innerMotor.foreach(_.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION))

    leftMotor.foreach(Motors.watchForStop)
    rightMotor.foreach(Motors.watchForStop)
  }

  def startMovingSteer(steer: Percent, speed: DegreesPerSecond): Unit = {
    val (innerMotor, outerMotor) = steerMotors(steer)
    val innerSpeed = (speed.value * innerMotorProportion(steer).value).degreesPerSecond
    outerMotor.foreach(_.writeSpeed(speed))
    innerMotor.foreach(_.writeSpeed(innerSpeed))
    outerMotor.foreach(_.writeCommand(MotorCommand.RUN))
    innerMotor.foreach(_.writeCommand(MotorCommand.RUN))
  }


  def stop(): Unit = {
    leftMotor.foreach(_.writeCommand(MotorCommand.STOP))
    rightMotor.foreach(_.writeCommand(MotorCommand.STOP))
  }


  def atStop(stopCommand: MotorStopCommand): Unit = {
    leftMotor.foreach(_.writeStopAction(stopCommand))
    rightMotor.foreach(_.writeStopAction(stopCommand))
  }
}
  