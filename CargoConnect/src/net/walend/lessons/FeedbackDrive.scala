package net.walend.lessons

import net.walend.cargoconnect.Robot
import ev3dev4s.measure.Degrees
import ev3dev4s.measure.DegreesPerSecond
import ev3dev4s.measure.Conversions._
import ev3dev4s.Log
import ev3dev4s.os.Time
import ev3dev4s.actuators.{Ev3Led, Motor}
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.measure.MilliMeters
import ev3dev4s.sysfs.UnpluggedException
import scala.annotation.tailrec

object FeedbackLoop {
  @tailrec
  final def feedback[S <: SensorReading](sense: () => S)(complete: S => Boolean)(response: S => Unit): Unit = {
    val sensorResults = sense()
    if (complete(sensorResults)) ()
    else {
      response(sensorResults)
      Time.pause()
      feedback(sense)(complete)(response)
    }
  }
}

final case class FeedbackMove[S <: SensorReading] (
                                                   name:String,
                                                   sense: () => S,
                                                   complete: S => S => Boolean,
                                                   drive: S => S => Unit,
                                                   start: S => Unit,
                                                   end: () => Unit
                                                  ) extends Move {
  def move(): Unit = {
    val initialSense = sense()
    start(initialSense)

    import FeedbackLoop.feedback
    //noinspection EmptyParenMethodAccessedAsParameterless
    feedback(sense)(complete(initialSense))(drive(initialSense))
    end()
  }
}

trait SensorReading

object FeedbackDriveTest extends Runnable {
  val actions: Array[TtyMenuAction] = Array(

    MovesMenuAction("0 Gyro", GyroSetHeading(0.degrees)),
    MovesMenuAction("WarmUp", GyroDrive.WarmUp),
    MovesMenuAction("ColorCalibrate", Seq(CalibrateReflect)),

    MovesMenuAction("Gyro F 0", Seq(GyroDrive.driveForwardDistance(0.degrees, 200.degreesPerSecond, 500.mm), Robot.Hold)),
    MovesMenuAction("Gyro B 0", Seq(GyroDrive.driveBackwardDistance(0.degrees, -200.degreesPerSecond, -500.mm), Robot.Hold)),

    MovesMenuAction("90 Gyro", GyroSetHeading(90.degrees)),
    MovesMenuAction("Gyro F 90", Seq(GyroDrive.driveForwardDistance(90.degrees, 200.degreesPerSecond, 500.mm), Robot.Hold)),
    MovesMenuAction("Turn RF 90", Seq(GyroTurn.rightForwardPivot(90.degrees, 200.degreesPerSecond), Robot.Hold)),
    MovesMenuAction("Turn LF -90", Seq(GyroTurn.leftForwardPivot(-90.degrees, 200.degreesPerSecond), Robot.Hold)),
    MovesMenuAction("Turn RB 90", Seq(GyroTurn.rightBackwardPivot(90.degrees, -200.degreesPerSecond), Robot.Hold)),
    MovesMenuAction("Turn LB -90", Seq(GyroTurn.leftBackwardPivot(-90.degrees, -200.degreesPerSecond), Robot.Hold)),
    MovesMenuAction("Rotate R 90", Seq(GyroTurn.rightRotate(90.degrees, 200.degreesPerSecond), Robot.Hold)),
    MovesMenuAction("Rotate L -90", Seq(GyroTurn.leftRotate(-90.degrees, 200.degreesPerSecond), Robot.Hold)),

    MovesMenuAction("Line F L .5m", Seq(LineDrive.driveForwardUntilDistance(0.degrees, Robot.rightColorSensor, BlackSide.Right, 200.degreesPerSecond, 500.mm), Robot.Hold)),
    MovesMenuAction("Line F L black", Seq(LineDrive.driveForwardUntilBlack(0.degrees, Robot.rightColorSensor, BlackSide.Right, 200.degreesPerSecond, 500.mm), Robot.Hold)),

    MovesMenuAction("Arc FR", Seq(GyroArc.driveArcForwardRight(90.degrees, 200.degreesPerSecond, 500.mm), Robot.Hold)),
    MovesMenuAction("Arc FL", Seq(GyroArc.driveArcForwardLeft(-90.degrees, 200.degreesPerSecond, 500.mm), Robot.Hold)),
    MovesMenuAction("Arc BR", Seq(GyroArc.driveArcBackwardRight(90.degrees, -200.degreesPerSecond, 500.mm), Robot.Hold)),
    MovesMenuAction("Arc BL", Seq(GyroArc.driveArcBackwardLeft(-90.degrees, -200.degreesPerSecond, 500.mm), Robot.Hold)),

    MovesMenuAction("Stop", Robot.Coast),
    MovesMenuAction("Despin", Seq(DespinGyro))
  )

  //todo add color sensors
  def setSensorRows(): Unit = {
    Lcd.set(0, s"${
      lcdView.elapsedTime
    }s", Lcd.RIGHT)
    val heading: String = UnpluggedException.safeString(() => s"${
      Robot.gyroscope.headingMode().readHeading().value
    }d")
    Lcd.set(0, heading, Lcd.LEFT)
    val leftColor: String = UnpluggedException.safeString(() => s"${
      Robot.leftColorSensor.reflectMode().readReflect().round
    }")

    Lcd.set(1, leftColor, Lcd.LEFT)
    val rightColor: String = UnpluggedException.safeString(() => s"${
      Robot.rightColorSensor.reflectMode().readReflect().round
    }")
    Lcd.set(1, rightColor, Lcd.RIGHT)


    val forkDegrees = UnpluggedException.safeString(() => s"Fork ${
      Robot.forkMotor.readPosition().value
    }d")
    Lcd.set(2, forkDegrees, Lcd.LEFT)
  }


  val lcdView: Controller = Controller(actions, setSensorRows)

  override def run(): Unit = lcdView.run()

  def main(args: Array[String]): Unit = run()
}

