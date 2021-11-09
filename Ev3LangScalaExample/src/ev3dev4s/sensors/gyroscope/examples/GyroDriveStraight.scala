package ev3dev4s.sensors.gyroscope.examples

import ev3dev4s.actuators.{Motor, MotorCommand, MotorPort, MotorStopCommand, MotorState}
import ev3dev4s.sensors.{Ev3Gyroscope, Ev3KeyPad}
import ev3dev4s.{Ev3System, Log}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object GyroDriveStraight extends Runnable:
  def main(args: Array[String]): Unit =
    run()

  override def run(): Unit =
//    Robot.gyroscope.despin()
    Robot.headingMode.zero()
    Robot.hold()

//    driveGyroFeedbackDistance(0,500,400)
//    driveGyroFeedbackDistance(0,100,100)

//    driveArcGyroFeedback(0,500,400)

    driveArcAbsoluteDistanceGyroSpeedFeedback(0,500,100,800)
//    driveArcAbsoluteDistanceGyroSpeedFeedback(0,100,100)
    Robot.hold()
    Log.log(s"holding motors - waiting for button")

    while(Robot.keypad.blockUntilAnyKey()._2 != Ev3KeyPad.State.Released) {}

  def driveGyroFeedbackDistance(
                             goalHeading: Int,
                             goalSpeed: Int,
                             distanceMm: Int
                           ): Unit =
    val startTac = Robot.leftMotor.readPosition()
    val goalTac = startTac + (360 * distanceMm) / Robot.driveWheelCircumference

    def notThereYet():Boolean =
      val tac = Robot.leftMotor.readPosition()
      tac < goalTac

    driveGyroFeedback(goalHeading,goalSpeed,notThereYet)


  /**
   * Drive straight toward goalHeading at speed, using feedback from the gyroscope.
   *
   * Note that this will overshoot slightly.
   *
   * @param goalHeading
   * @param goalSpeed
   * @param keepGoing
   */
  def driveGyroFeedback(
             goalHeading: Int,
             goalSpeed: Int,
             keepGoing: () => Boolean
           ): Unit =

    Robot.leftMotor.runDutyCycle(0)
    Robot.rightMotor.runDutyCycle(0)

    while (keepGoing())
      val heading: Int = Robot.headingMode.readHeading()
      val steerAdjust: Int =
        if(heading == goalHeading) 0
        else {
          //about 1% per degree off seems good - but it should really care about wheel base width
          val proportionalSteerAdjust = (goalHeading - heading) * goalSpeed / 100
          // return adjustments of at minimum 1
          if (Math.abs(proportionalSteerAdjust) > 1) proportionalSteerAdjust
          else if (proportionalSteerAdjust > 0) 1
          else -1
        }
//      Robot.drive(goalSpeed + steerAdjust, goalSpeed - steerAdjust)

      def dutyCyclesFromAdjust():(Int,Int) =
        val leftIdeal = (goalSpeed + steerAdjust)/10
        val rightIdeal = (goalSpeed - steerAdjust)/10
        val (leftSteering, rightSteering) =
          if(leftIdeal != rightIdeal) (leftIdeal,rightIdeal)
          else if(steerAdjust > 0) (leftIdeal+1,rightIdeal)
          else if(steerAdjust < 0) (leftIdeal,rightIdeal+1)
          else (leftIdeal,rightIdeal)
        if(leftSteering >= 10 && rightSteering >= 10) (leftSteering,rightSteering)
        else (leftSteering+10,rightSteering+10)

      val (leftDutyCycle,rightDutyCycle) = dutyCyclesFromAdjust()

      Robot.leftMotor.writeDutyCycle(leftDutyCycle)
      Robot.rightMotor.writeDutyCycle(rightDutyCycle)

  /**
   *
   * @param goalHeading
   * @param goalSpeed
   * @param fineSpeed
   * @param centerArcLengthMm
   */
  def driveArcOpenLoop(
                        goalHeading: Int,
                        goalSpeed: Int,
                        fineSpeed: Int,
                        centerArcLengthMm: Int
                      ):Unit =
    val (insideGoalTac,insideMotor) = driveArcWriteGoalPositions(goalHeading,centerArcLengthMm)
    driveArcHeadingAdjust(goalHeading,selectSpeed(goalSpeed,fineSpeed,insideGoalTac,insideMotor)._1)

  val fiftyMmDegrees: Int = ((360.toFloat * 50f) / Robot.driveWheelCircumference).round
  /**
   *
   *
   * @param goalHeading
   * @param goalSpeed
   * @param fineSpeed
   * @param centerArcLengthMm
   */
  def driveArcAbsoluteDistanceGyroSpeedFeedback(
                                                 goalHeading: Int,
                                                 goalSpeed: Int,
                                                 fineSpeed: Int,
                                                 centerArcLengthMm: Int
                                               ):Unit =
    val (insideGoalTac,insideMotor) = driveArcWriteGoalPositions(goalHeading,centerArcLengthMm)
    driveArcHeadingAdjust(goalHeading,selectSpeed(goalSpeed,fineSpeed,insideGoalTac,insideMotor)._1)

    while {
        Robot.leftMotor.readState().contains(MotorState.RUNNING) ||
        Robot.rightMotor.readState().contains(MotorState.RUNNING)
    } do
      val (speed,update) = selectSpeed(goalSpeed,fineSpeed,insideGoalTac,insideMotor)
      if(update) driveArcHeadingAdjust(goalHeading,speed)
      //if the distance left is < 50 mm then just let the motors run the last command open-loop
      //but poll them to see when they stop running

  /**
   *
   *
   * @param goalHeading
   * @param centerArcLengthMm
   * @return the inside motor for the turn. If both motors are to travel the same length then it returns the right motor.
   */
  private def driveArcWriteGoalPositions(
                                          goalHeading: Int,
                                          centerArcLengthMm: Int
                                        ): (Int,Motor) =
    val leftStartTac: Int = Robot.leftMotor.readPosition()
    val rightStartTac: Int = Robot.rightMotor.readPosition()

    val goalTacChange: Float = (360.toFloat * centerArcLengthMm) / Robot.driveWheelCircumference
    val heading: Int = Robot.headingMode.readHeading()
    val headingDelta: Int = goalHeading - heading
    val correction: Float = (Math.PI.toFloat * Robot.robotWheelbase * headingDelta)/360
    //left wheel faster for positive
    val leftDistance: Float = centerArcLengthMm.toFloat + correction
    val rightDistance: Float = centerArcLengthMm.toFloat - correction
    val leftGoalDegrees:Int = ((360 * leftDistance) / Robot.driveWheelCircumference).round
    val rightGoalDegrees:Int = ((360 * rightDistance) / Robot.driveWheelCircumference).round
    val leftGoalTac: Int = leftStartTac + leftGoalDegrees
    val rightGoalTac: Int = rightStartTac + rightGoalDegrees

    Robot.leftMotor.writeGoalPosition(leftGoalTac)
    Robot.rightMotor.writeGoalPosition(rightGoalTac)

    if(leftGoalDegrees < rightGoalDegrees) (leftGoalTac,Robot.leftMotor)
    else (rightGoalTac,Robot.rightMotor)


  /**
   * Adjust the speed based on the current gyro heading to drive in an arc.
   * This method assumes the goal absolute postions have already been written
   *
   * @param goalHeading
   * @param goalSpeed
   */
  private def driveArcHeadingAdjust(
                                   goalHeading: Int,
                                   goalSpeed: Int,
                                 ):Unit =
    val heading: Int = Robot.headingMode.readHeading()
    val steerAdjust: Int =
      if(heading == goalHeading) 0
      else
        //about 1% per degree off seems good - but it should really care about wheel base width
        val proportionalSteerAdjust = (goalHeading - heading) * goalSpeed / 100
        // return adjustments of at minimum 1
        if (Math.abs(proportionalSteerAdjust) > 1) proportionalSteerAdjust
        else if (proportionalSteerAdjust > 0) 1
        else -1
    Robot.leftMotor.writeSpeed(goalSpeed + steerAdjust)
    Robot.rightMotor.writeSpeed(goalSpeed - steerAdjust)
    Robot.leftMotor.writeCommand(MotorCommand.RUN_TO_ABSOLUTE_POSITION)
    Robot.rightMotor.writeCommand(MotorCommand.RUN_TO_ABSOLUTE_POSITION)

  /**
   *
   * @param goalSpeed
   * @param fineSpeed
   * @param goalTac
   * @param motor
   * @return speed for motors in an ideal world
   */
  def selectSpeed(
                   goalSpeed: Int,
                   fineSpeed: Int,
                   goalTac:Int,
                   motor:Motor
                 ):(Int,Boolean) =
    val currentTac = motor.readPosition()
    val minusOneIfBackward = if(goalTac < currentTac) -1
    else 1

    val (absoluteSpeed,update) = if(Math.abs(goalTac - currentTac) > fiftyMmDegrees *2) (goalSpeed,true)
    else if (Math.abs(goalTac - currentTac) > fiftyMmDegrees)
    //todo ramp down maybe
      (fineSpeed,true)
    else (fineSpeed,false)
    (absoluteSpeed * minusOneIfBackward,update)

object Robot:

  val driveWheelDiameter = 88 //millimeters
  val driveWheelCircumference = (driveWheelDiameter * Math.PI).toInt

  val robotWheelbase = 18 * 8 //millimeters

  val keypad = Ev3System.keyPad

  val gyroscope:Ev3Gyroscope = Ev3System.portsToSensors.values.collectFirst{case gyro:Ev3Gyroscope => gyro}.get
  val headingMode: gyroscope.HeadingMode = gyroscope.headingMode()

  val leftMotor: Motor = Ev3System.portsToMotors(MotorPort.A)
  val rightMotor: Motor = Ev3System.portsToMotors(MotorPort.B)

  leftMotor.writeStopAction(MotorStopCommand.BRAKE)
  rightMotor.writeStopAction(MotorStopCommand.BRAKE)

  def drive(leftSpeed: Int, rightSpeed: Int): Unit =
    Log.log(s"drive $leftSpeed $rightSpeed")
    leftMotor.writeSpeed(leftSpeed)
    rightMotor.writeSpeed(rightSpeed)
    leftMotor.writeCommand(MotorCommand.RUN)
    rightMotor.writeCommand(MotorCommand.RUN)

  def brake():Unit =
    leftMotor.writeStopAction(MotorStopCommand.BRAKE)
    rightMotor.writeStopAction(MotorStopCommand.BRAKE)
    leftMotor.writeCommand(MotorCommand.STOP)
    rightMotor.writeCommand(MotorCommand.STOP)

  def hold():Unit =
    leftMotor.writeStopAction(MotorStopCommand.HOLD)
    rightMotor.writeStopAction(MotorStopCommand.HOLD)
    leftMotor.writeCommand(MotorCommand.STOP)
    rightMotor.writeCommand(MotorCommand.STOP)




