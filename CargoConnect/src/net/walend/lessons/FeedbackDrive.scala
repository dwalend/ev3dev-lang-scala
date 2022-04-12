package net.walend.lessons

import net.walend.cargoconnect.Robot
import ev3dev4s.measure.Degrees
import ev3dev4s.measure.DegreesPerSecond
import ev3dev4s.measure.Conversions.*

import ev3dev4s.Log

import ev3dev4s.actuators.{Ev3Led,Motor}
import scala.annotation.tailrec
import ev3dev4s.measure.MilliMeters


object FeedbackLoop:
  @tailrec
  final def feedback[S <: SensorResults](sense:() => S)(complete:S => Boolean)(response:S => Unit):Unit =
    val sensorResults = sense()
    if(complete(sensorResults)) ()
    else
      response(sensorResults)
      feedback(sense)(complete)(response)



trait SensorResults


final case class GyroSensorResults(heading:Degrees,leftMotorTachometer:Degrees) extends SensorResults

object GyroSensorResults:
  def sense():GyroSensorResults = GyroSensorResults(
    Robot.gyroscope.headingMode().readHeading(),
    Robot.leftDriveMotor.readPosition()
  )

  def completeForward(goalTachometer: Degrees)(sensorResults: GyroSensorResults): Boolean =
    sensorResults.leftMotorTachometer > goalTachometer




case class GyroDriveStraightForwardFeedback(
                                              goalHeading:Degrees,
                                              goalDistance:Degrees,
                                              goalSpeed:DegreesPerSecond,
                                              tachometer:Motor = Robot.leftDriveMotor
                                            ) extends Move:

  private def sense():GyroSensorResults =
    GyroSensorResults(Robot.gyroscope.headingMode().readHeading(),tachometer.readPosition())

  private def complete(goalTachometer:Degrees)(sensorResults: GyroSensorResults) =
    sensorResults.leftMotorTachometer > goalDistance

  private def response(sensorResults: GyroSensorResults): Unit =
    val steerAdjust = ((goalHeading - sensorResults.heading).value * 6).degreesPerSecond //todo should also be proportional to goal speed
    Robot.drive(goalSpeed + steerAdjust, goalSpeed - steerAdjust)

  def move():Unit =
    val goalTachometer = Robot.leftDriveMotor.readPosition() + goalDistance

    Ev3Led.writeBothGreen()
    import FeedbackLoop.feedback
    //noinspection EmptyParenMethodAccessedAsParameterless
    feedback(sense)(complete(goalTachometer))(response)
