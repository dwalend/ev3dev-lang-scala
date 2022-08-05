package net.walend.lessons

import net.walend.cargoconnect.Robot
import ev3dev4s.measure.Degrees
import ev3dev4s.measure.DegreesPerSecond
import ev3dev4s.measure.Conversions._
import ev3dev4s.Log
import ev3dev4s.actuators.{Ev3LargeMotor, Ev3Led, Motor}
import ev3dev4s.measure.MilliMeters
import scala.annotation.tailrec

object GyroArc {

  def arcDrive(goalHeading: Degrees, goalSpeed: DegreesPerSecond, radius: MilliMeters)(initial: GyroAndTachometer)(reading: GyroAndTachometer): Unit = {

    val totalDegrees = goalHeading - initial.heading
    val turnSign = totalDegrees.sign * goalSpeed.sign
    val outerMotor = if (turnSign > 0) Robot.leftDriveMotor
    else Robot.rightDriveMotor

    val totalDistance = ((2 * Math.PI.toFloat * radius.v * totalDegrees.v * turnSign) / 360).mm //outer wheel

    val remainingDegrees = goalHeading - reading.heading
    val remainingDistance = totalDistance - Robot.wheelRotationToDistance(reading.tachometerAngle - initial.tachometerAngle)

    val expectedRemainingDegrees = ((remainingDistance.v / totalDistance.v) * totalDegrees.v).degrees

    val steerAdjust = (turnSign * (remainingDegrees - expectedRemainingDegrees).v * goalSpeed.abs.v / 30).degreesPerSecond

    val outerSpeed = goalSpeed + steerAdjust
    val innerSpeed = (((goalSpeed - steerAdjust).v * (radius - Robot.wheelToWheel).v) / radius.v).degreesPerSecond

    if (outerMotor == Robot.leftDriveMotor) Robot.drive(outerSpeed, innerSpeed)
    else Robot.drive(innerSpeed, outerSpeed)
  }

  private[lessons] def startLeftOuter(goalSpeed: DegreesPerSecond)(notUsed: SensorReading): Unit = {
    Ev3Led.Left.writeGreen()
    Ev3Led.Right.writeYellow()
    Robot.drive(goalSpeed, goalSpeed)
  }

  private[lessons] def startRightOuter(goalSpeed: DegreesPerSecond)(notUsed: SensorReading): Unit = {
    Ev3Led.Left.writeYellow()
    Ev3Led.Right.writeGreen()
    Robot.drive(goalSpeed, goalSpeed)
  }


  private[lessons] def end(): Unit = {
    Robot.drive(0.degreesPerSecond, 0.degreesPerSecond)
    Ev3Led.writeBothOff()
  }


  def driveArcForwardRight(goalHeading: Degrees, goalSpeed: DegreesPerSecond, radius: MilliMeters): Move =
    FeedbackMove(
      name = "Arc FR",
      sense = GyroAndTachometer.sense(Robot.leftDriveMotor),
      complete = GyroTurn.rightEnough(goalHeading),
      drive = arcDrive(goalHeading, goalSpeed, radius),
      start = startLeftOuter(goalSpeed),
      end = end
    )

  def driveArcForwardLeft(goalHeading: Degrees, goalSpeed: DegreesPerSecond, radius: MilliMeters): Move =
    FeedbackMove(
      name = "Arc FL",
      sense = GyroAndTachometer.sense(Robot.rightDriveMotor),
      complete = GyroTurn.leftEnough(goalHeading),
      drive = arcDrive(goalHeading, goalSpeed, radius),
      start = startRightOuter(goalSpeed),
      end = end
    )

  def driveArcBackwardRight(goalHeading: Degrees, goalSpeed: DegreesPerSecond, radius: MilliMeters): Move =
    FeedbackMove(
      name = "Arc BR",
      sense = GyroAndTachometer.sense(Robot.rightDriveMotor),
      complete = GyroTurn.rightEnough(goalHeading),
      drive = arcDrive(goalHeading, goalSpeed, radius),
      start = startRightOuter(goalSpeed),
      end = end
    )

  def driveArcBackwardLeft(goalHeading: Degrees, goalSpeed: DegreesPerSecond, radius: MilliMeters): Move =
    FeedbackMove(
      name = "Arc BL",
      sense = GyroAndTachometer.sense(Robot.leftDriveMotor),
      complete = GyroTurn.leftEnough(goalHeading),
      drive = arcDrive(goalHeading, goalSpeed, radius),
      start = startLeftOuter(goalSpeed),
      end = end
    )

  object WarmUp extends Move {
    /**
     * Call driveForward and driveBackward 's feedback loop 1100 times each
     */
    override def move(): Unit = {
      GyroSetHeading(0.degrees).move()
      for (_ <- 0 until 2) {
        driveArcForwardRight(90.degrees, Robot.fineSpeed, Robot.wheelToWheel + 200.mm).move()
        driveArcForwardLeft(0.degrees, Robot.fineSpeed, Robot.wheelToWheel + 200.mm).move()
        driveArcBackwardRight(90.degrees, -Robot.fineSpeed, Robot.wheelToWheel + 200.mm).move()
        driveArcBackwardLeft(0.degrees, -Robot.fineSpeed, Robot.wheelToWheel + 200.mm).move()
      }

      Robot.Coast.move()
    }
  }
}