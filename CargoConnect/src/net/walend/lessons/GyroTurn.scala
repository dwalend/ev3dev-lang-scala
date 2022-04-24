package net.walend.lessons

import net.walend.cargoconnect.Robot
import ev3dev4s.measure.Degrees
import ev3dev4s.measure.DegreesPerSecond
import ev3dev4s.measure.Conversions.*

import ev3dev4s.Log

import ev3dev4s.actuators.Ev3Led
import scala.annotation.tailrec
import ev3dev4s.measure.MilliMeters

object GyroTurn:

  case class GyroReading(heading:Degrees) extends SensorReading

  object GyroReading:
    def sense():GyroReading =
      GyroReading(Robot.gyroscope.headingMode().readHeading())

  def rightForwardPivotFeedback(goalHeading:Degrees,goalSpeed:DegreesPerSecond):Move =
    FeedbackMove(
      name = s"R F Pivot $goalHeading",
      sense = GyroReading.sense,
      complete = rightEnough(goalHeading),
      drive = leftWheelDrive(goalHeading,goalSpeed),
      start = startLeftWheel(goalHeading,goalSpeed),
      end = end
    )

  def rightEnough(goalHeading:Degrees)(notUsed: GyroReading)(gyroReading: GyroReading):Boolean =
    goalHeading < gyroReading.heading

  def speed(goalHeading:Degrees,goalSpeed:DegreesPerSecond,gyroReading: GyroReading):DegreesPerSecond =
    
    val fullSpeedThreshold = 45.degrees
    val minSpeedThreshold = 5.degrees
    val minSpeed = (goalSpeed.sign * 100.degreesPerSecond).degreesPerSecond //todo this is too slow for rotations - or need to use the motor's internal control to go this slow.

    val absHeadingDiff = (goalHeading - gyroReading.heading).abs

    if(absHeadingDiff > fullSpeedThreshold) goalSpeed
    else if(absHeadingDiff > minSpeedThreshold)
      val threshholdDiff = fullSpeedThreshold - minSpeedThreshold
      ((goalSpeed.value * (absHeadingDiff - minSpeedThreshold).value/threshholdDiff.value) +
        (minSpeed.value * (fullSpeedThreshold - absHeadingDiff).value/threshholdDiff.value)).degreesPerSecond
    else minSpeed


  def leftWheelDrive(goalHeading:Degrees,goalSpeed:DegreesPerSecond)(gyroReading: GyroReading):Unit =
    val s = speed(goalHeading,goalSpeed,gyroReading)
    Robot.drive(s,0.degreesPerSecond)

  def startLeftWheel(goalHeading:Degrees,goalSpeed:DegreesPerSecond)(gyroReading: GyroReading):Unit =
    leftWheelDrive(goalHeading,goalSpeed)(gyroReading)
    Ev3Led.Left.writeGreen()
    Ev3Led.Right.writeOff()

  def end():Unit =
    Ev3Led.writeBothOff()

  def leftForwardPivotFeedback(goalHeading:Degrees,goalSpeed:DegreesPerSecond):Move =
    FeedbackMove(
      name = s"L F Pivot $goalHeading",
      sense = GyroReading.sense,
      complete = leftEnough(goalHeading),
      drive = rightWheelDrive(goalHeading,goalSpeed),
      start = startRightWheel(goalHeading,goalSpeed),
      end = end
    )

  def leftEnough(goalHeading:Degrees)(notUsed: GyroReading)(gyroReading: GyroReading):Boolean =
    goalHeading > gyroReading.heading

  def rightWheelDrive(goalHeading:Degrees,goalSpeed:DegreesPerSecond)(gyroReading: GyroReading):Unit =
    val s = speed(goalHeading,goalSpeed,gyroReading)
    Robot.drive(0.degreesPerSecond,s)

  def startRightWheel(goalHeading:Degrees,goalSpeed:DegreesPerSecond)(gyroReading: GyroReading):Unit =
    rightWheelDrive(goalHeading,goalSpeed)(gyroReading)
    Ev3Led.Right.writeGreen()
    Ev3Led.Left.writeOff()


  def rightBackwardPivotFeedback(goalHeading:Degrees,goalSpeed:DegreesPerSecond):Move =
    FeedbackMove(
      name = s"R B Pivot $goalHeading",
      sense = GyroReading.sense,
      complete = rightEnough(goalHeading),
      drive = rightWheelDrive(goalHeading,goalSpeed),
      start = startRightWheel(goalHeading,goalSpeed),
      end = end
    )

  def leftBackwardPivotFeedback(goalHeading:Degrees,goalSpeed:DegreesPerSecond):Move =
    FeedbackMove(
      name = s"L B Pivot $goalHeading",
      sense = GyroReading.sense,
      complete = leftEnough(goalHeading),
      drive = leftWheelDrive(goalHeading,goalSpeed),
      start = startLeftWheel(goalHeading,goalSpeed),
      end = end
    )

  def rightRotateFeedback(goalHeading:Degrees,goalSpeed:DegreesPerSecond):Move =
    FeedbackMove(
      name = s"R Rotate $goalHeading",
      sense = GyroReading.sense,
      complete = rightEnough(goalHeading),
      drive = rightRotateDrive(goalHeading,goalSpeed),
      start = startRightRotate(goalHeading,goalSpeed),
      end = end
    )

  def rightRotateDrive(goalHeading:Degrees,goalSpeed:DegreesPerSecond)(gyroReading: GyroReading):Unit =
    val s = (speed(goalHeading,goalSpeed,gyroReading).value/2).degreesPerSecond
    Robot.drive(s,-s)

  def startRightRotate(goalHeading:Degrees,goalSpeed:DegreesPerSecond)(gyroReading: GyroReading):Unit =
    rightRotateDrive(goalHeading,goalSpeed)(gyroReading)
    Ev3Led.writeBothGreen()

  def leftRotateFeedback(goalHeading:Degrees,goalSpeed:DegreesPerSecond):Move =
    FeedbackMove(
      name = s"R Rotate $goalHeading",
      sense = GyroReading.sense,
      complete = leftEnough(goalHeading),
      drive = leftRotateDrive(goalHeading,goalSpeed),
      start = startLeftRotate(goalHeading,goalSpeed),
      end = end
    )

  def leftRotateDrive(goalHeading:Degrees,goalSpeed:DegreesPerSecond)(gyroReading: GyroReading):Unit =
    val s = (speed(goalHeading,goalSpeed,gyroReading).value/2).degreesPerSecond
    Robot.drive(-s,s)

  def startLeftRotate(goalHeading:Degrees,goalSpeed:DegreesPerSecond)(gyroReading: GyroReading):Unit =
    leftRotateDrive(goalHeading,goalSpeed)(gyroReading)
    Ev3Led.writeBothGreen()

  /****/

