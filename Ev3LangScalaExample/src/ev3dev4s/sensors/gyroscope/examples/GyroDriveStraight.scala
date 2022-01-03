package ev3dev4s.sensors.gyroscope.examples

import ev3dev4s.actuators.{Motor, MotorCommand, MotorPort, MotorStopCommand, MotorState}
import ev3dev4s.sensors.{Ev3Gyroscope, Ev3KeyPad}
import ev3dev4s.{Ev3System, Log}
import ev3dev4s.measure.Degrees
import ev3dev4s.measure.Conversions.*
import ev3dev4s.measure.DegreesPerSecond
import ev3dev4s.measure.DutyCycle

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

//    driveArcAbsoluteDistanceGyroSpeedFeedback(goalHeading = 0.degree,cruiseSpeed = 500,fineSpeed = 100,centerArcLengthMm = 800)
//    driveArcAbsoluteDistanceGyroSpeedFeedback(0,100,100)
    Robot.hold()
    Log.log(s"holding motors - waiting for button")

    while(Robot.keypad.blockUntilAnyKey()._2 != Ev3KeyPad.State.Released) {}

  /**
   * Gyro straight with the duty cycle
   *
   * @param goalHeading that the gyroscope should read during this traverse
   * @param dutyCycle degrees per second to turn the motors
   * @param distanceMm distance to travel
   */
  def driveGyroFeedbackDistance(
                                 goalHeading: Degrees,
                                 dutyCycle: DutyCycle,
                                 distanceMm: Int
                           ): Unit =
    val startTac: Degrees = Robot.leftMotor.readPosition()
    val goalTac = (startTac.value + (360 * distanceMm) / Robot.driveWheelCircumference).degrees

    def notThereYet():Boolean =
      val tac = Robot.leftMotor.readPosition()
      tac < goalTac

    driveGyroFeedback(goalHeading,dutyCycle,notThereYet)


  /**
   * Drive straight toward goalHeading at speed, using feedback from the gyroscope.
   *
   * Note that this will overshoot slightly.
   *
   * @param goalHeading that the gyroscope should read during this traverse
   * @param dutyCycle degrees per second to turn the motors
   * @param keepGoing false when time to stop
   */
  def driveGyroFeedback(
                         goalHeading: Degrees,
                         dutyCycle: DutyCycle,
                         keepGoing: () => Boolean
                        ): Unit =

    Robot.leftMotor.runDutyCycle(0.dutyCyclePercent)
    Robot.rightMotor.runDutyCycle(0.dutyCyclePercent)

    while (keepGoing())
      val heading: Degrees = Robot.headingMode.readHeading()
      val steerAdjust: DutyCycle = 
        if(heading == goalHeading) 0.dutyCyclePercent
        else 
          //about 1% per degree off seems good - but it should really care about wheel base width
          val proportionalSteerAdjust = ((goalHeading - heading) * dutyCycle / 100).dutyCyclePercent
          if (proportionalSteerAdjust.abs > 1.dutyCyclePercent) proportionalSteerAdjust
          else if (proportionalSteerAdjust == 0.dutyCyclePercent) 0.dutyCyclePercent
          else if (proportionalSteerAdjust > 0.dutyCyclePercent) 1.dutyCyclePercent
          else -1.dutyCyclePercent
        

      def dutyCyclesFromAdjust():(DutyCycle,DutyCycle) =
        val leftIdeal = ((dutyCycle + steerAdjust)/10.unitless).dutyCyclePercent
        val rightIdeal = ((dutyCycle - steerAdjust)/10.unitless).dutyCyclePercent
        (leftIdeal,rightIdeal)
        //todo for fractions 
        //val (leftSteering, rightSteering) = (leftIdeal,rightIdeal)
          //todo for fractions 
          //if(leftIdeal != rightIdeal) (leftIdeal,rightIdeal)
          //else if(steerAdjust.value > 0) (leftIdeal+1,rightIdeal)
          //else if(steerAdjust.value < 0) (leftIdeal,rightIdeal+1)
          //else (leftIdeal,rightIdeal)
        //todo speed up very slow movement by 10. Not sure this is usefulif(leftSteering >= 10 && rightSteering >= 10) (leftSteering,rightSteering)
        //todo speed up very slow movement by 10. Not sure this is useful else (leftSteering+10,rightSteering+10) 

      val (leftDutyCycle,rightDutyCycle) = dutyCyclesFromAdjust()

      Robot.leftMotor.writeDutyCycle(leftDutyCycle)
      Robot.rightMotor.writeDutyCycle(rightDutyCycle)
      Thread.`yield`()
/*

  val fiftyMmDegrees: Int = ((360.toFloat * 50f) / Robot.driveWheelCircumference).round
  /**
   * Drive in an arc - read the heading at the beginning, then travel open-loop
   *
   * @param goalHeading that the gyroscope should read during this traverse
   * @param cruiseSpeed degrees/second for moving the robot quickly
   * @param fineSpeed degrees/second to let the robot hit the mark exactly
   * @param centerArcLengthMm distance to travel
   */
  def driveArcOpenLoop(
                        goalHeading: Degrees,
                        cruiseSpeed: Int,
                        fineSpeed: Int,
                        centerArcLengthMm: Int,
                        fineControlMm:Int = fiftyMmDegrees,
                        openLoopMm:Int = fiftyMmDegrees
                      ):Unit =
    val (insideGoalTac,insideMotor) = driveArcWriteGoalPositions(goalHeading,centerArcLengthMm)
    driveArcHeadingAdjust(goalHeading,selectSpeed(cruiseSpeed,fineSpeed,insideGoalTac,insideMotor,fineControlMm,openLoopMm)._1)
    while {
      Robot.leftMotor.readState().contains(MotorState.RUNNING) ||
      Robot.rightMotor.readState().contains(MotorState.RUNNING)
    } do Thread.`yield`()

  /**
   * Drive in an arc with feedback from the gyroscope.
   *
   * To go in a straight line give it the robot's current heading
   *
   * @param goalHeading that the gyroscope should read during this traverse
   * @param cruiseSpeed degrees/second for moving the robot quickly
   * @param fineSpeed degrees/second to let the robot hit the mark exactly
   * @param centerArcLengthMm distance to travel
   * @param fineControlMm distance from open loop to slow down to avoid overshoot
   * @param openLoopMm distance from the end to switch to open-loop
   */
  def driveArcAbsoluteDistanceGyroSpeedFeedback(
                                                  goalHeading: Degrees,
                                                  cruiseSpeed: Int,
                                                  fineSpeed: Int,
                                                  centerArcLengthMm: Int,
                                                  fineControlMm:Int = fiftyMmDegrees,
                                                  openLoopMm:Int = fiftyMmDegrees
                                                ):Unit =
    val (insideGoalTac,insideMotor) = driveArcWriteGoalPositions(goalHeading,centerArcLengthMm)
    driveArcHeadingAdjust(goalHeading,selectSpeed(cruiseSpeed,fineSpeed,insideGoalTac,insideMotor,fineControlMm,openLoopMm)._1)

    while {
        Robot.leftMotor.readState().contains(MotorState.RUNNING) ||
        Robot.rightMotor.readState().contains(MotorState.RUNNING)
    } do
      val (speed,update) = selectSpeed(cruiseSpeed,fineSpeed,insideGoalTac,insideMotor,fineControlMm,openLoopMm)
      if(update) driveArcHeadingAdjust(goalHeading,speed)
      //if the distance left is < 50 mm then just let the motors run the last command open-loop
      //but poll them to see when they stop running

  /**
   *
   *
   * @param goalHeading that the gyroscope should read during this traverse
   * @param centerArcLengthMm distance to travel
   * @return the inside motor for the turn. If both motors are to travel the same length then it returns the right motor.
   */
  private def driveArcWriteGoalPositions(
                                          goalHeading: Degrees,
                                          centerArcLengthMm: Int
                                        ): (Int,Motor) =
    val leftStartTac: Int = Robot.leftMotor.readPosition()
    val rightStartTac: Int = Robot.rightMotor.readPosition()

    val goalTacChange: Float = (360.toFloat * centerArcLengthMm) / Robot.driveWheelCircumference
    val heading: Degrees = Robot.headingMode.readHeading()
    val headingDelta: Degrees = goalHeading - heading
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
   * @param goalHeading that the gyroscope should read during this traverse
   * @param averageSpeed degrees/second for moving the robot quickly
   */
  private def driveArcHeadingAdjust(
                                     goalHeading: Degrees,
                                     averageSpeed: Int,
                                 ):Unit =
    val heading: Degrees = Robot.headingMode.readHeading()
    val steerAdjust: Int =
      if(heading == goalHeading) 0
      else
        //todo about 1% per degree off seems good - but it should really care about wheel base width
        //val proportionalSteerAdjust = (goalHeading - heading) * averageSpeed / 100
        val headingDelta = goalHeading - heading
        val proportionalSteerAdjust:Float =
          (averageSpeed * Math.PI.toFloat * Robot.robotWheelbase * headingDelta)/
            (360*Robot.driveWheelDiameter) // d/s
        // return adjustments of at minimum 1
        if (Math.abs(proportionalSteerAdjust) > 1) proportionalSteerAdjust.round
        else if (proportionalSteerAdjust > 0) 1
        else -1
    Robot.leftMotor.writeSpeed(averageSpeed + steerAdjust)
    Robot.rightMotor.writeSpeed(averageSpeed - steerAdjust)
    Robot.leftMotor.writeCommand(MotorCommand.RUN_TO_ABSOLUTE_POSITION)
    Robot.rightMotor.writeCommand(MotorCommand.RUN_TO_ABSOLUTE_POSITION)
    Thread.`yield`()

  /**
   *
   * @param cruiseSpeed degrees/second for moving the robot quickly
   * @param fineSpeed degrees/second to let the robot hit the mark exactly
   * @param goalTac path length set for motor
   * @param motor to read progress to the goalTac
   * @return speed for motors in an ideal world
   */
  private def selectSpeed(
                           goalSpeed: Int,
                           fineSpeed: Int,
                           goalTac:Int,
                           motor:Motor,
                           fineControlMm:Int,
                           openLoopMm:Int
                         ):(Int,Boolean) =
    val currentTac = motor.readPosition()
    val minusOneIfBackward = if(goalTac < currentTac) -1
    else 1

    val (absoluteSpeed,update) = if(Math.abs(goalTac - currentTac) > fineControlMm + openLoopMm)
      (goalSpeed,true)
    else if (Math.abs(goalTac - currentTac) > openLoopMm)
    //todo ramp down maybe
      (fineSpeed,true)
    else (fineSpeed,false)
    (absoluteSpeed * minusOneIfBackward,update)
*/
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

  def drive(leftSpeed: DegreesPerSecond, rightSpeed: DegreesPerSecond): Unit =
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




