package net.walend.lessons

import net.walend.cargoconnect.Robot
import ev3dev4s.measure.Degrees
import ev3dev4s.measure.DegreesPerSecond
import ev3dev4s.measure.Conversions.*
import ev3dev4s.Log
import ev3dev4s.actuators.{Ev3LargeMotor, Ev3Led, Motor}
import ev3dev4s.measure.MilliMeters
import scala.annotation.tailrec

object GyroArcFeedback:



  def arcDrive(goalHeading:Degrees,goalSpeed:DegreesPerSecond,radius:MilliMeters)(initial:GyroAndTachometer)(reading:GyroAndTachometer):Unit =

    val totalDegrees = goalHeading - initial.heading
    val turnSign = totalDegrees.sign * goalSpeed.sign
    val outerMotor = if(turnSign > 0) Robot.leftDriveMotor
                     else Robot.rightDriveMotor

    val totalDistance = ((2 * Math.PI.toFloat * radius.value * totalDegrees.value * turnSign)/360).mm //outer wheel

    val remainingDegrees = goalHeading - reading.heading
    val remainingDistance = totalDistance - Robot.wheelRotationToDistance(reading.tachometerAngle - initial.tachometerAngle)

    val expectedRemainingDegrees = ((remainingDistance.value/totalDistance.value)*totalDegrees.value).degrees

    val steerAdjust = (turnSign * (remainingDegrees - expectedRemainingDegrees).value * goalSpeed.abs.value / 30).degreesPerSecond

    val outerSpeed = goalSpeed + steerAdjust
    val innerSpeed = (((goalSpeed - steerAdjust).value * (radius - Robot.wheelToWheel).value)/radius.value).degreesPerSecond

    if (outerMotor == Robot.leftDriveMotor) Robot.directDrive(outerSpeed,innerSpeed)
    else Robot.directDrive(innerSpeed,outerSpeed)

  private[lessons] def startLeftOuter(goalSpeed:DegreesPerSecond)(notUsed:SensorReading):Unit =
    Ev3Led.Left.writeGreen()
    Ev3Led.Right.writeYellow()
    Robot.directDrive(goalSpeed,goalSpeed)
    Robot.writeDirectDriveMode()

  private[lessons] def startRightOuter(goalSpeed:DegreesPerSecond)(notUsed:SensorReading):Unit =
    Ev3Led.Left.writeYellow()
    Ev3Led.Right.writeGreen()
    Robot.directDrive(goalSpeed,goalSpeed)
    Robot.writeDirectDriveMode()


  private[lessons] def end():Unit =
    Robot.directDrive(0.degreesPerSecond,0.degreesPerSecond)
    Ev3Led.writeBothOff()


  def driveArcForwardRight(goalHeading:Degrees,goalSpeed:DegreesPerSecond,radius:MilliMeters):Move =
    FeedbackMove(
      name = "Arc FR",
      sense = GyroAndTachometer.sense(Robot.leftDriveMotor),
      complete = GyroTurn.rightEnough(goalHeading),
      drive = arcDrive(goalHeading,goalSpeed,radius),
      start = startLeftOuter(goalSpeed),
      end = end
    )

  def driveArcForwardLeft(goalHeading:Degrees,goalSpeed:DegreesPerSecond,radius:MilliMeters):Move =
    FeedbackMove(
      name = "Arc FL",
      sense = GyroAndTachometer.sense(Robot.rightDriveMotor),
      complete = GyroTurn.leftEnough(goalHeading),
      drive = arcDrive(goalHeading,goalSpeed,radius),
      start = startRightOuter(goalSpeed),
      end = end
    )

  def driveArcBackwardRight(goalHeading:Degrees,goalSpeed:DegreesPerSecond,radius:MilliMeters):Move =
    FeedbackMove(
      name = "Arc BR",
      sense = GyroAndTachometer.sense(Robot.rightDriveMotor),
      complete = GyroTurn.rightEnough(goalHeading),
      drive = arcDrive(goalHeading,goalSpeed,radius),
      start = startRightOuter(goalSpeed),
      end = end
    )

  def driveArcBackwardLeft(goalHeading:Degrees,goalSpeed:DegreesPerSecond,radius:MilliMeters):Move =
    FeedbackMove(
      name = "Arc BL",
      sense = GyroAndTachometer.sense(Robot.leftDriveMotor),
      complete = GyroTurn.leftEnough(goalHeading),
      drive = arcDrive(goalHeading,goalSpeed,radius),
      start = startLeftOuter(goalSpeed),
      end = end
    )