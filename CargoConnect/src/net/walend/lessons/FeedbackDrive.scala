package net.walend.lessons

import net.walend.cargoconnect.Robot
import ev3dev4s.measure.Degrees
import ev3dev4s.measure.DegreesPerSecond
import ev3dev4s.measure.Conversions.*
import ev3dev4s.Log
import ev3dev4s.os.Time
import ev3dev4s.actuators.{Ev3Led, Motor}
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.measure.MilliMeters
import ev3dev4s.sysfs.UnpluggedException
import scala.annotation.tailrec

object FeedbackLoop:
  @tailrec
  final def feedback[S <: SensorResults](sense:() => S)(complete:S => Boolean)(response:S => Unit):Unit =
    val sensorResults = sense()
    if(complete(sensorResults)) ()
    else
      response(sensorResults)
      Time.pause()
      feedback(sense)(complete)(response)

final case class FeedbackMove(
                         name:String,
                         sense: () => GyroSensorResults,
                         complete: GyroSensorResults => GyroSensorResults => Boolean,
                         drive: GyroSensorResults => Unit,
                         start: () => Unit,
                         end: () => Unit
                       ) extends Move:
  def move():Unit =
    val initialSense = sense()
    start()

    import FeedbackLoop.feedback
    //noinspection EmptyParenMethodAccessedAsParameterless
    feedback(sense)(complete(initialSense))(drive)
    end()

trait SensorResults

trait GyroHeadingResult extends SensorResults:
  def heading:Degrees

trait TachometerResult extends SensorResults:
  def tachometerResult:Degrees

final case class GyroSensorResults(heading:Degrees, tachometerResult:Degrees)
  extends GyroHeadingResult with TachometerResult

object GyroSensorResults:
  def sense():GyroSensorResults = GyroSensorResults(
    Robot.gyroscope.headingMode().readHeading(),
    Robot.leftDriveMotor.readPosition()
  )

object GyroDriveFeedback:
  private def senseGyroAndDistance(tachometer:Motor)():GyroSensorResults =
    GyroSensorResults(Robot.gyroscope.headingMode().readHeading(),tachometer.readPosition())

  private def start(goalSpeed:DegreesPerSecond)():Unit =
    Ev3Led.writeBothGreen()
    Robot.directDrive(goalSpeed,goalSpeed)
    Robot.writeDirectDriveMode()

  private def end():Unit =
    Robot.directDrive(0.degreesPerSecond,0.degreesPerSecond)
    Ev3Led.writeBothOff()

  private def forwardUntilDistance(distance:MilliMeters)(initialSensorResults:TachometerResult)(sensorResults: TachometerResult) =
    val goalTachometer = initialSensorResults.tachometerResult + Robot.distanceToWheelRotation(distance)
    sensorResults.tachometerResult > goalTachometer

  private def driveAdjust(goalHeading:Degrees,goalSpeed:DegreesPerSecond)(sensorResults: GyroHeadingResult): Unit =
    val steerAdjust = ((goalHeading - sensorResults.heading).value * goalSpeed.abs.value / 30).degreesPerSecond
    Robot.directDrive(goalSpeed + steerAdjust, goalSpeed - steerAdjust)

  def driveForwardDistance(goalHeading:Degrees,goalSpeed:DegreesPerSecond,distance:MilliMeters,tachometer:Motor = Robot.leftDriveMotor):Move =
    FeedbackMove(
      name = s"GyroF $distance",
      sense = senseGyroAndDistance(tachometer),
      complete = forwardUntilDistance(distance),
      drive = driveAdjust(goalHeading,goalSpeed),
      start = start(goalSpeed),
      end = end
    )

  private def backwardUntilDistance(distance:MilliMeters)(initialSensorResults:TachometerResult)(sensorResults: TachometerResult) =
    val goalTachometer = initialSensorResults.tachometerResult + Robot.distanceToWheelRotation(distance)
    sensorResults.tachometerResult < goalTachometer

  def driveBackwardDistance(goalHeading:Degrees,goalSpeed:DegreesPerSecond,distance:MilliMeters,tachometer:Motor = Robot.leftDriveMotor):Move =
    FeedbackMove(
      name = s"GyroB $distance",
      sense = senseGyroAndDistance(tachometer),
      complete = backwardUntilDistance(distance),
      drive = driveAdjust(goalHeading,goalSpeed),
      start = start(goalSpeed),
      end = end
    )

object FeedbackDriveTest extends Runnable:
  val actions: Array[TtyMenuAction] = Array(
    MovesMenuAction("WarmUp",Robot.WarmUp),
    MovesMenuAction("0-Gyro",GyroSetHeading(0.degrees)),
    MovesMenuAction("Forward",Seq(GyroDriveFeedback.driveForwardDistance(0.degrees,200.degreesPerSecond,500.mm),Robot.Hold)),
    MovesMenuAction("Backward",Seq(GyroDriveFeedback.driveBackwardDistance(0.degrees,-200.degreesPerSecond,-500.mm),Robot.Hold)),
    MovesMenuAction("Stop",Robot.Coast),
    MovesMenuAction("Despin",Seq(DespinGyro))
  )

  //todo add color sensors
  def setSensorRows():Unit =
    Lcd.set(0,s"${lcdView.elapsedTime}s",Lcd.RIGHT)
    val heading:String = UnpluggedException.safeString(() => s"${Robot.gyroscope.headingMode().readHeading().value}d")
    Lcd.set(0,heading,Lcd.LEFT)

    val forkDegrees = UnpluggedException.safeString(() => s"Fork ${Robot.forkMotor.readPosition().value}d")
    Lcd.set(1,forkDegrees,Lcd.LEFT)

  val lcdView:Controller = Controller(actions,setSensorRows)

  override def run():Unit = lcdView.run()

  def main(args: Array[String]): Unit = run()

