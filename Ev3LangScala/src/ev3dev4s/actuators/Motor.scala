package ev3dev4s.actuators

import ev3dev4s.sysfs.{ChannelRereader, ChannelRewriter}

import java.nio.file.Path

import java.io.IOException
import java.nio.file.AccessDeniedException

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
sealed abstract class Motor(val port:MotorPort,@volatile private var motorDevice: Option[MotorFS]) extends AutoCloseable:

  def writeCommand(command: MotorCommand):Unit = checkPlug(_.writeCommand(command))

  def writeStopAction(command:MotorStopCommand):Unit = checkPlug(_.writeStopAction(command))

  def writeDutyCycle(percent:Int):Unit = checkPlug(_.writeDutyCycle(percent))
  
  def writeSpeed(degreesPerSecond:Int):Unit = checkPlug(_.writeSpeed(degreesPerSecond))

  def writePosition(degrees:Int):Unit = checkPlug(_.writePosition(degrees))

  def resetPosition():Unit = writePosition(0)

  def writeGoalPosition(degrees:Int):Unit = checkPlug(_.writeGoalPosition(degrees))

  /**
   * @return position in degrees
   */
  def readPosition():Int = checkPlug(_.readPosition())

  def readState(): Array[MotorState] = checkPlug(_.readState())

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

  def runDutyCycle(percent:Int):Unit =
    writeDutyCycle(percent)
    writeCommand(MotorCommand.RUN_DIRECT)

  def runSpeed(degreesPerSecond:Int):Unit =
    writeSpeed(degreesPerSecond)
    writeCommand(MotorCommand.RUN)

  def checkPlug[A](action:MotorFS => A):A =
    def handleException(t:Throwable):Nothing =
      motorDevice.foreach(_.close())
      motorDevice = None //set to None so that next time this will try again
      throw UnpluggedMotorException(port,t)

    try
      motorDevice.orElse { //see if the motor is plugged back in
        motorDevice = MotorPortScanner.findMotorDevice(port)
        motorDevice
      }.fold[A]{ //if still not plugged in
        throw UnpluggedMotorException(port,MotorNotFoundException(port))
      }{ //otherwise do the action
        action(_)
      }
    catch //todo generalize, move to sysfs, and immitate for sensors - tomorrow!
      case iox:IOException if iox.getMessage() == "No such device" => handleException(iox)
      case adx:AccessDeniedException => handleException(adx)

  override def close(): Unit =
    motorDevice.foreach(_.close())
    motorDevice = None

case class UnpluggedMotorException(port: MotorPort,cause:Throwable) extends Exception(s"Motor in $port unplugged",cause)

case class MotorNotFoundException(port:MotorPort) extends Exception(s"Scan for $port found no motor")

//todo so far no need for different classes for different motors
sealed case class Ev3LargeMotor(override val port:MotorPort, md: Option[MotorFS]) extends Motor(port,md)

object Ev3LargeMotor:
  val driverName = "lego-ev3-l-motor"

sealed case class Ev3MediumMotor(override val port:MotorPort, md: Option[MotorFS]) extends Motor(port,md)

object Ev3MediumMotor:
  val driverName = "lego-ev3-m-motor"

//todo sane camelcase names
enum MotorCommand(val command:String):
  /**
   * run-forever: Causes the motor to run until another command is sent
   */
  case RUN extends MotorCommand("run-forever")

  /**
   * run-to-abs-pos: Runs the motor to an absolute position specified by``position_sp`` and then stops the motor using the command specified in stop_action.
   */
  case RUN_TO_ABSOLUTE_POSITION extends MotorCommand("run-to-abs-pos")

  /** todo
run-to-rel-pos: Runs the motor to a position relative to the current position value. The new position will be current position + position_sp. When the new position is reached, the motor will stop using the command specified by stop_action.
run-timed: Run the motor for the amount of time specified in time_sp and then stops the motor using the command specified by stop_action.
   */
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
