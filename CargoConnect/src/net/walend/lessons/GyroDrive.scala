package net.walend.lessons

import net.walend.cargoconnect.Robot
import ev3dev4s.measure.Degrees
import ev3dev4s.measure.DegreesPerSecond
import ev3dev4s.measure.Conversions._
import ev3dev4s.Log
import ev3dev4s.actuators.{Ev3Led, Motor}
import ev3dev4s.measure.MilliMeters

object GyroDrive {

  private[lessons] def start(goalSpeed: DegreesPerSecond)(notUsed: SensorReading): Unit = {
    Ev3Led.writeBothGreen()
    Robot.directDrive(goalSpeed, goalSpeed)
    Robot.writeDirectDriveMode()
  }

  private[lessons] def end(): Unit = Ev3Led.writeBothOff()

  private[lessons] def forwardUntilDistance(distance: MilliMeters)(initialSensorResults: TachometerAngle)(sensorResults: TachometerAngle) = {
    val goalTachometer = initialSensorResults.tachometerAngle + Robot.distanceToWheelRotation(distance)
    sensorResults.tachometerAngle > goalTachometer
  }

  private[lessons] def driveAdjust(goalHeading: Degrees, goalSpeed: DegreesPerSecond)(initial: GyroHeading)(sensorResults: GyroHeading): Unit = {
    val steerAdjust = ((goalHeading - sensorResults.heading).v * goalSpeed.abs.v / 30).degreesPerSecond
    Log.log(s"steerAdjust $steerAdjust goalSpeed $goalSpeed sensorResults $sensorResults")
    Robot.directDrive(goalSpeed + steerAdjust, goalSpeed - steerAdjust)
  }

  //todo check inputs
  def driveForwardDistance(goalHeading: Degrees, goalSpeed: DegreesPerSecond, distance: MilliMeters, tachometer: Motor = Robot.leftDriveMotor): Move =
    FeedbackMove(
      name = s"GyroF $distance",
      sense = GyroAndTachometer.sense(tachometer),
      complete = forwardUntilDistance(distance),
      drive = driveAdjust(goalHeading, goalSpeed),
      start = start(goalSpeed),
      end = end
    )

  //todo check inputs
  private def backwardUntilDistance(distance: MilliMeters)(initialSensorResults: TachometerAngle)(sensorResults: TachometerAngle) = {
    val goalTachometer = initialSensorResults.tachometerAngle + Robot.distanceToWheelRotation(distance)
    sensorResults.tachometerAngle < goalTachometer
  }

  def driveBackwardDistance(goalHeading: Degrees, goalSpeed: DegreesPerSecond, distance: MilliMeters, tachometer: Motor = Robot.leftDriveMotor): Move =
    FeedbackMove(
      name = s"GyroB $distance",
      sense = GyroAndTachometer.sense(tachometer),
      complete = backwardUntilDistance(distance),
      drive = driveAdjust(goalHeading, goalSpeed),
      start = start(goalSpeed),
      end = end
    )

  object WarmUp extends Move {
    /**
     * Call driveForward and driveBackward 's feedback loop 1100 times each
     */
    override def move(): Unit = {
      GyroSetHeading(0.degrees).move()
      for (_ <- 0 until 6) {
        driveForwardDistance(0.degrees, 200.degreesPerSecond, 1000.mm).move()
        driveBackwardDistance(0.degrees, -200.degreesPerSecond, -1000.mm).move()
      }
      Robot.Coast.move()
    }
  }
}

trait GyroHeading extends SensorReading {
  def heading: Degrees
}

trait TachometerAngle extends SensorReading {
  def tachometerAngle: Degrees
}

final case class GyroAndTachometer(heading:Degrees, tachometerAngle:Degrees)
  extends GyroHeading with TachometerAngle

object GyroAndTachometer {

  def sense(tachometer: Motor)(): GyroAndTachometer =
    GyroAndTachometer(
      Robot.gyroscope.headingMode().readHeading(),
      tachometer.readPosition()
    )
}

case class GyroSetHeading(heading:Degrees) extends Move {
  def move(): Unit = Robot.gyroscope.headingMode().setHeading(heading)
}

object GyroUnwind extends Move {
  def move(): Unit = Robot.gyroscope.headingMode().unwind()
}

object DespinGyro extends Move {
  def move(): Unit =
    Robot.gyroscope.despin()
}
