package masterpiece

import ev3dev4s.Log
import ev3dev4s.actuators.{Ev3LargeMotor, MotorCommand, MotorPort}
import ev3dev4s.lego.{Gyroscope, Movement}
import ev3dev4s.scala2measure.Conversions.{FloatConversions, IntConversions}
import ev3dev4s.scala2measure.{Degrees, DegreesPerSecond, MilliMeters}
import ev3dev4s.sensors.{Ev3Gyroscope, SensorPort}

import java.lang.{Math, Runnable}
import scala.Predef.???
import scala.annotation.tailrec
import scala.{Boolean, None, StringContext, Unit}



object Robot {
  private val gyroscopePort = SensorPort.One
  private val gyroscope = Ev3Gyroscope(gyroscopePort,None)

  private val leftDrivePort = MotorPort.A
  private val rightDrivePort = MotorPort.C

  private val leftDriveMotor = Ev3LargeMotor(leftDrivePort,None)
  private val rightDriveMotor = Ev3LargeMotor(rightDrivePort,None)

  Movement.setMovementMotorsTo(leftDrivePort,rightDrivePort)

  private val wheelCircumference: MilliMeters = 180.mm
  private val robotWheelbase: MilliMeters = 116.mm

  private val cruiseSpeed: DegreesPerSecond = 400.degreesPerSecond
  private val fineSpeed: DegreesPerSecond = 40.degreesPerSecond

  private def rollingDistanceToDegrees(distance :MilliMeters): Degrees = {
    (360 * distance.v / wheelCircumference.v).degrees
  }

  def movestraight(distance :MilliMeters, speed:DegreesPerSecond = cruiseSpeed): Unit ={
    Movement.move(
      motorDegrees = rollingDistanceToDegrees(distance),
      speed = speed
    )
  }

  def moveStraightBackward(distance :MilliMeters, speed:DegreesPerSecond = cruiseSpeed): Unit = {
    Movement.move(
      motorDegrees = rollingDistanceToDegrees(-distance),
      speed = -speed
    )
  }

  @tailrec
  def rightRotation(goalHeading:Degrees):Unit={
    val heading: Degrees = Gyroscope.readHeading(SensorPort.One)
    val toGo: Degrees = goalHeading - heading
    val speed: DegreesPerSecond = (Robot.cruiseSpeed.v * (toGo.v/90)).degreesPerSecond

    Log.log(s"heading is $heading, speed is $speed")
    if(goalHeading > heading) {
      Movement.startMoving(speed,-speed)
      rightRotation(goalHeading)
    } else {
      Movement.stop()
    }
  }

  @tailrec
  def leftRotation(goalHeading: Degrees): Unit = {
    val heading = Gyroscope.readHeading(SensorPort.One)
    val toGo = -goalHeading + heading
    val speed = (Robot.cruiseSpeed.v * (toGo.v / 90)).degreesPerSecond

    Log.log(s"heading is $heading, speed is $speed")
    if (goalHeading < heading) {
      Movement.startMoving(-speed, speed)
      //Time.pause(10.milliseconds)
      leftRotation(goalHeading)
    } else {
      Movement.stop()
    }
  }

  private var expectedHeading:Degrees = 0.degrees

  def setHeading(heading:Degrees):Unit = {
    gyroscope.headingMode().setHeading(heading)
    expectedHeading = heading
  }

  def straightForward(distance :MilliMeters, speed:DegreesPerSecond = cruiseSpeed,goalHeading:Degrees = expectedHeading):Unit  = {
    leftDriveMotor.writeSpeed((speed.v/2.0f).degreesPerSecond)
    leftDriveMotor.writeCommand(MotorCommand.RUN)

    rightDriveMotor.writeSpeed(speed)
    rightDriveMotor.writeGoalPosition(rollingDistanceToDegrees(distance))
    rightDriveMotor.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION)

    val rightIsRunning: () => Boolean = rightDriveMotor.readIsRunning

    @tailrec
    def recur():Unit = {
      if(rightIsRunning()) {
        val heading = gyroscope.headingMode().readHeading()
        val delta: Degrees = heading - goalHeading
        val leftSpeed: DegreesPerSecond = speed - (delta.v * (speed.v * 0.05f)).degreesPerSecond
        leftDriveMotor.writeSpeed(leftSpeed)
        leftDriveMotor.writeCommand(MotorCommand.RUN)

        Log.log(s"heading is $heading, leftSpeed is $leftSpeed")
        recur()
      } else {
        leftDriveMotor.writeCommand(MotorCommand.STOP)
      }
    }
    recur()
  }

  def rotateRight(goalHeading:Degrees,speed:DegreesPerSecond = cruiseSpeed):Unit = {
    val heading: Degrees = gyroscope.headingMode().readHeading()
    val toGo: Degrees = goalHeading - heading
    val feedbackSpeed: DegreesPerSecond = {
      val rawSpeed = (speed.v * (toGo.v / 90)).degreesPerSecond
      if (rawSpeed > speed) speed
      else if (rawSpeed < Robot.fineSpeed) Robot.fineSpeed
      else rawSpeed
    }

    Log.log(s"heading is $heading, speed is $feedbackSpeed")
    if (goalHeading > heading) {
      Movement.startMoving(feedbackSpeed, -feedbackSpeed)
      rotateRight(goalHeading,speed)
    } else {
      Movement.stop()
    }
  }

  def pivotRightForwardTo(goalHeading: Degrees, speed: DegreesPerSecond = cruiseSpeed): Unit = {
    rightDriveMotor.writeCommand(MotorCommand.STOP)

    @tailrec
    def recur():Unit = {
      val heading: Degrees = gyroscope.headingMode().readHeading()
      val toGo: Degrees = goalHeading - heading
      val feedbackSpeed: DegreesPerSecond = {
        val rawSpeed = (speed.v * (toGo.v / 90)).degreesPerSecond
        if (rawSpeed > speed) speed
        else if (rawSpeed < Robot.fineSpeed) Robot.fineSpeed
        else rawSpeed
      }

      Log.log(s"heading is $heading, speed is $feedbackSpeed")
      if (goalHeading > heading) {
        leftDriveMotor.writeSpeed(feedbackSpeed)
        leftDriveMotor.writeCommand(MotorCommand.RUN)
        recur()
      } else {
        leftDriveMotor.writeCommand(MotorCommand.STOP)
      }
    }
    recur()
    expectedHeading = goalHeading
  }

  def curveRightForwardTo(goalHeading:Degrees, outerRadius:MilliMeters, speed:DegreesPerSecond = cruiseSpeed) = {
    val starterHeading = gyroscope.headingMode().readHeading()
    val deltaHeading = goalHeading - starterHeading
    val deltaLeftDistance: MilliMeters = ((deltaHeading.v/360) * 2 * Math.PI.toFloat * outerRadius.v).mm
    val deltaLeftOdometer:Degrees = rollingDistanceToDegrees(deltaLeftDistance)
    val optimalRightSpeed: DegreesPerSecond = (((outerRadius - robotWheelbase)/outerRadius) * speed.v).degreesPerSecond

    val starterLeftOdometer = leftDriveMotor.readPosition()
    def expectedLeftMotorOdometer(heading:Degrees):Degrees = {
      starterLeftOdometer + (((heading - starterHeading).v / deltaHeading.v ) * deltaLeftOdometer.v).degrees
    }
    def leftIsRunning(): Boolean = leftDriveMotor.readIsRunning()

    leftDriveMotor.writeSpeed(speed)
    leftDriveMotor.writeGoalPosition(deltaLeftOdometer)
    leftDriveMotor.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION)

    @tailrec
    def recur():Unit = {
      if(leftIsRunning()) {
        val heading = gyroscope.headingMode().readHeading()
        val leftOdometer = leftDriveMotor.readPosition()
        val rightSpeed: DegreesPerSecond = optimalRightSpeed -
          (optimalRightSpeed * 0.005f  * (leftOdometer - expectedLeftMotorOdometer(heading)) ).degreesPerSecond

        rightDriveMotor.writeSpeed(rightSpeed)
        rightDriveMotor.writeCommand(MotorCommand.RUN)
        Log.log(s"heading is $heading, expected - leftOdometer is ${expectedLeftMotorOdometer(heading) - leftOdometer}, speed is $rightSpeed")

        recur()
      }
      else {
        rightDriveMotor.writeCommand(MotorCommand.STOP)
      }
    }

    recur()
    expectedHeading = goalHeading
  }
}

object Straight extends Runnable {
  override def run(): Unit = {
    Robot.setHeading(0.degrees)
    Robot.straightForward(500.mm,650.degreesPerSecond)
  }
}

object RotateRight extends Runnable {
  override def run(): Unit = {
    Robot.setHeading(0.degrees)
    Robot.rotateRight(90.degrees,650.degreesPerSecond)
  }
}

object PivotRight extends Runnable {
  override def run(): Unit = {
    Robot.setHeading(0.degrees)
    Robot.pivotRightForwardTo(90.degrees,650.degreesPerSecond)
  }
}

object CurveRight extends Runnable {
  override def run(): Unit = {
    Robot.setHeading(0.degrees)
    Robot.curveRightForwardTo(90.degrees,300.mm)
  }
}