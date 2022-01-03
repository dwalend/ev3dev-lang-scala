package ev3dev4s.actuators

import ev3dev4s.sysfs.{ChannelRereader, ChannelRewriter, Gadget}

import ev3dev4s.Log

import java.nio.file.Path

import java.io.IOException
import java.nio.file.AccessDeniedException

import ev3dev4s.measure.DutyCycle
import ev3dev4s.measure.DegreesPerSecond
import ev3dev4s.measure.Conversions.*
import ev3dev4s.measure.Degrees
import ev3dev4s.measure.MilliSeconds

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
sealed abstract class Motor(port: MotorPort,motorFS:Option[MotorFS]) extends Gadget(port,motorFS):

  def writeCommand(command: MotorCommand):Unit = checkPort(_.writeCommand(command))

  def writeStopAction(command:MotorStopCommand):Unit = checkPort(_.writeStopAction(command))

  def writeDutyCycle(dutyCycle:DutyCycle):Unit = checkPort(_.writeDutyCycle(dutyCycle))

  def maxSpeed:DegreesPerSecond

  def writeSpeed(speed:DegreesPerSecond):Unit =
    val safeSpeed = if(speed.abs < maxSpeed ) speed
                    else
                      Log.log(s"requested speed $speed is greater than $maxSpeed - using $maxSpeed")
                      (speed.sign * maxSpeed).degreesPerSecond
    checkPort(_.writeSpeed(safeSpeed))

  def writePosition(degrees:Degrees):Unit = checkPort(_.writePosition(degrees))

  def resetPosition():Unit = writePosition(0.degrees)

  def writeGoalPosition(degrees:Degrees):Unit = checkPort(_.writeGoalPosition(degrees))

  def writeDuration(milliseconds:MilliSeconds):Unit = checkPort(_.writeDuration(milliseconds))

  /**
   * @return position in degrees
   */
  def readPosition():Degrees = checkPort(_.readPosition())

  def readState(): Array[MotorState] = checkPort(_.readState())

  def readIsStalled():Boolean =
    readState().contains(MotorState.STALLED)

  def coast(): Unit =
    writeStopAction(MotorStopCommand.COAST)
    writeCommand(MotorCommand.STOP)

  def brake(): Unit =
    writeStopAction(MotorStopCommand.BRAKE)
    writeCommand(MotorCommand.STOP)

  def hold(): Unit =
    writeStopAction(MotorStopCommand.HOLD)
    writeCommand(MotorCommand.STOP)

  def runDutyCycle(dutyCycle:DutyCycle):Unit =
    writeDutyCycle(dutyCycle)
    writeCommand(MotorCommand.RUN_DIRECT)

  def run(speed:DegreesPerSecond):Unit =
    writeSpeed(speed)
    writeCommand(MotorCommand.RUN)
    
  def runToAbsolutePosition(speed:DegreesPerSecond,degrees:Degrees):Unit =
    writeSpeed(speed)
    writeGoalPosition(degrees)
    writeCommand(MotorCommand.RUN_TO_ABSOLUTE_POSITION)
    
  def runToRelativePosition(speed:DegreesPerSecond,degrees:Degrees):Unit =
    writeSpeed(speed)
    writeGoalPosition(degrees)
    writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION)

  def runForDuration(speed:DegreesPerSecond,milliseconds:MilliSeconds):Unit =
    writeSpeed(speed)
    writeDuration(milliseconds)
    writeCommand(MotorCommand.RUN_TIME)

sealed case class Ev3LargeMotor(override val port:MotorPort, md: Option[MotorFS]) extends Motor(port,md):
  override def findGadgetFS(): Option[MotorFS] =
    MotorPortScanner.findGadgetDir(port,Ev3LargeMotor.driverName)
      .map(MotorFS(_))

  override val maxSpeed: DegreesPerSecond = 1050.degreesPerSecond

object Ev3LargeMotor:
  val driverName = "lego-ev3-l-motor"

sealed case class Ev3MediumMotor(override val port:MotorPort, md: Option[MotorFS]) extends Motor(port,md):
  override def findGadgetFS(): Option[MotorFS] =
    MotorPortScanner.findGadgetDir(port,Ev3MediumMotor.driverName)
      .map(MotorFS(_))

  override val maxSpeed:DegreesPerSecond = 1560.degreesPerSecond

object Ev3MediumMotor:
  val driverName = "lego-ev3-m-motor"

enum MotorCommand(val command:String):
  /**
   * run-forever: Causes the motor to run until another command is sent
   */
  case RUN extends MotorCommand("run-forever")

  /**
   * run-to-abs-pos: Runs the motor to an absolute position specified by``position_sp`` and then stops the motor using the command specified in stop_action.
   */
  case RUN_TO_ABSOLUTE_POSITION extends MotorCommand("run-to-abs-pos")

  /** 
run-to-rel-pos: Runs the motor to a position relative to the current position value. The new position will be current position + position_sp. When the new position is reached, the motor will stop using the command specified by stop_action. */
  case RUN_TO_RELATIVE_POSITION extends MotorCommand("run-to-rel-pos")  
  
  /**
run-timed: Run the motor for the amount of time specified in time_sp and then stops the motor using the command specified by stop_action.
   */
  case RUN_TIME extends MotorCommand("run-timed")

  /**
   * run-direct: Runs the motor using the duty cycle specified by duty_cycle_sp. Unlike other run commands, changing duty_cycle_sp while running will take effect immediately.
   */
  case RUN_DIRECT extends MotorCommand("run-direct")
  /**
   * stop: Stop any of the run commands before they are complete using the command specified by stop_action.
   */
  case STOP extends MotorCommand("stop")

  /**
  reset: Resets all of the motor parameter attributes to their default values. This will also have the effect of stopping the motor.
   */

/**
 * Determines the motors behavior when command is set to stop. Possible values are:
 */
enum MotorStopCommand(val command:String):
  /**
   * Removes power from the motor. The motor will freely coast to a stop.
   */
  case COAST extends MotorStopCommand("coast")

  /**
   * Removes power from the motor and creates a passive electrical load. This is usually done by shorting the motor terminals together. This load will absorb the energy from the rotation of the motors and cause the motor to stop more quickly than coasting.
   */
  case BRAKE extends MotorStopCommand("brake")

  /**
   * Causes the motor to actively try to hold the current position. If an external force tries to turn the motor, the motor will “push back” to maintain its position.
   */
  case HOLD extends MotorStopCommand("hold")

enum MotorState(val name:String):
  /**
  running: Power is being sent to the motor.
  */
  case RUNNING extends MotorState("running")
  /**
  ramping: The motor is ramping up or down and has not yet reached a constant output level.
  */
  case RAMPING extends MotorState("ramping")
  /**
  holding: The motor is not turning, but rather attempting to hold a fixed position.
  */
  case HOLDING extends MotorState("holding")
  /**
  overloaded: The motor is turning as fast as possible, but cannot reach its speed_sp.
  */
  case OVERLOADED extends MotorState("overloaded")
  /**
    stalled: The motor is trying to run but is not turning at all.
  */
  case STALLED extends MotorState("stalled")
