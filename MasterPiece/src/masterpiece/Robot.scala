package masterpiece

import ev3dev4s.Log
import ev3dev4s.actuators.{Ev3LargeMotor, Ev3MediumMotor, MotorCommand, MotorPort, MotorStopCommand}
import ev3dev4s.lego.{Gyroscope, Movement}
import ev3dev4s.scala2measure.Conversions.{FloatConversions, IntConversions}
import ev3dev4s.scala2measure.{Degrees, DegreesPerSecond, MilliMeters}
import ev3dev4s.sensors.{Ev3Gyroscope, SensorPort}

import java.lang.{Math, Runnable}
import scala.annotation.{tailrec, unused}
import scala.{Boolean, Float, None, StringContext, Unit}



object Robot {
  private val gyroscopePort = SensorPort.One
  private val gyroscope = Ev3Gyroscope(gyroscopePort,None)

  private val leftDrivePort = MotorPort.B
  private val rightDrivePort = MotorPort.C

  private val leftDriveMotor = Ev3LargeMotor(leftDrivePort,None)
  private val rightDriveMotor = Ev3LargeMotor(rightDrivePort,None)

  Movement.setMovementMotorsTo(leftDrivePort,rightDrivePort)

  private val leftToolPort = MotorPort.A
  private val rightToolPort = MotorPort.D

  private val leftToolMotor = Ev3MediumMotor(leftToolPort,None)
  private val rightToolMotor = Ev3MediumMotor(rightToolPort,None)

  private val wheelCircumference: MilliMeters = 180.mm
  private val robotWheelbase: MilliMeters = 116.mm

  private val cruiseSpeed: DegreesPerSecond = 400.degreesPerSecond
  private val fineSpeed: DegreesPerSecond = 40.degreesPerSecond

  private def rollingDistanceToDegrees(distance :MilliMeters): Degrees = {
    (360 * distance.v / wheelCircumference.v).degrees
  }

  private def degreesTorRollingDistance(degrees: Degrees): MilliMeters = {
    (wheelCircumference.v * degrees.v / 360 ).mm
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

  def leftLiftUp(): Unit = {
    leftToolMotor.writeStopAction(MotorStopCommand.BRAKE)
    leftToolMotor.writeSpeed(300.degreesPerSecond)
    leftToolMotor.writeDuration(1000.ms)
    leftToolMotor.writeCommand(MotorCommand.RUN_TIME)
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

  private def feedbackLoop[Start,Sense](
                       start: () => Start,
                       sense: () => Sense,
                       isDone: Sense => Boolean,
                       control: (Start,Sense) => Unit, //(initial,sense) => ()
                       end: Sense => Unit,
                     ) = {
    val initial:Start = start()

    @tailrec
    def recur():Sense = {
      val sensed = sense()
      if(isDone(sensed)) {
        end(sensed)
        sensed
      } else {
        control(initial,sensed)
        recur()
      }
    }
    recur()
  }

  def straightForward(distance :MilliMeters, speed:DegreesPerSecond = cruiseSpeed, goalHeading:Degrees = expectedHeading):Unit = {
    case class Started(rightStartPosition:Degrees,rightGoalPosition:Degrees)

    def start(): Started = {
      leftDriveMotor.writeStopAction(MotorStopCommand.BRAKE)
      rightDriveMotor.writeStopAction(MotorStopCommand.BRAKE)

      val rightStartPosition: Degrees = rightDriveMotor.readPosition()
      val rightGoalPosition = rightStartPosition + rollingDistanceToDegrees(distance)
      rightDriveMotor.writeGoalPosition(rightGoalPosition)
      rightDriveMotor.writeSpeed(fineSpeed)
      rightDriveMotor.writeCommand(MotorCommand.RUN_TO_ABSOLUTE_POSITION)
      Started(rightStartPosition,rightGoalPosition)
    }

    case class Sensed(heading:Degrees,isRightRunning:Boolean,rightMotorPosition:Degrees)
    def sense(): Sensed = {
      Sensed(
        gyroscope.headingMode().readHeading(),
        rightDriveMotor.readIsRunning(),
        rightDriveMotor.readPosition(),
      )
    }

    def control(started:Started,sensed:Sensed):Unit = {
      def gentleSpeed(leftSpeed:DegreesPerSecond,rightSpeed:DegreesPerSecond):(DegreesPerSecond,DegreesPerSecond) = {
        val gentleDistance = 70.mm
        val gentleDegrees = rollingDistanceToDegrees(gentleDistance)
        val rightPosition = rightDriveMotor.readPosition()
        val rightDelta = rightPosition - started.rightStartPosition
        val rightRemaining = started.rightGoalPosition - rightPosition

        val accelFactor = rightDelta/gentleDegrees
        val decelFactor = rightRemaining/gentleDegrees

        def modifiedSpeed(s:DegreesPerSecond,factor:Float):DegreesPerSecond = {
          (s-Robot.fineSpeed)*factor + Robot.fineSpeed*(1.0f - factor)
        }

        if(accelFactor < 1.0f) (modifiedSpeed(leftSpeed,accelFactor),modifiedSpeed(rightSpeed,accelFactor))
        else if (decelFactor < 1.0f) (modifiedSpeed(leftSpeed,decelFactor),modifiedSpeed(rightSpeed,decelFactor))
        else (leftSpeed,rightSpeed)
      }

      val delta: Degrees = sensed.heading - goalHeading
      val rawLeftSpeed: DegreesPerSecond = speed - (delta.v * (speed.v * 0.05f)).degreesPerSecond
      val (leftSpeed,rightSpeed) = gentleSpeed(rawLeftSpeed,speed)

      leftDriveMotor.writeSpeed(leftSpeed)
      rightDriveMotor.writeSpeed(rightSpeed)
      leftDriveMotor.writeCommand(MotorCommand.RUN)
      rightDriveMotor.writeCommand(MotorCommand.RUN_TO_ABSOLUTE_POSITION)

    }

    def isDone(sensed:Sensed):Boolean = {
      !sensed.isRightRunning
    }

    def end(@unused sensed:Sensed): Unit = {
      leftDriveMotor.writeCommand(MotorCommand.STOP)
    }

    feedbackLoop(
      start,
      sense,
      isDone,
      control,
      end
    )
  }


  @tailrec
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

  def curveRightForwardTo(goalHeading:Degrees, outerRadius:MilliMeters, speed:DegreesPerSecond = cruiseSpeed): Unit = {
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

object LeftUp extends Runnable {
  override def run(): Unit = {
    Robot.leftLiftUp()
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