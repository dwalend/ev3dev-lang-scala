package ev3dev4s.actuators

import ev3dev4s.sysfs.{ChannelRereader, ChannelRewriter}

import java.nio.file.Path

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
sealed abstract class Motor(port:MotorPort,motorDir:Path) extends AutoCloseable {

  private val commandWriter = ChannelRewriter(motorDir.resolve("command"))
  private val stopActionWriter = ChannelRewriter(motorDir.resolve("stop_action"))

  private val dutyCycleSpWriter = ChannelRewriter(motorDir.resolve("duty_cycle_sp"))
  private val speedSpWriter = ChannelRewriter(motorDir.resolve("speed_sp"))
  private val positionWriter = ChannelRewriter(motorDir.resolve("position"))

  private val positionReader = ChannelRereader(motorDir.resolve("position"))
  private val stateReader = ChannelRereader(motorDir.resolve("state"),bufferLength = 52)


  //todo maybe writeCommand should be on the write side of a ReadWriteLock - and all others can be on the Read side?
  def writeCommand(command: MotorCommand):Unit = {
    commandWriter.writeString(command.command)
  }

  def writeStopAction(command:MotorStopCommand):Unit = {
    stopActionWriter.writeString(command.command)
  }

  def writeDutyCycle(percent:Int):Unit = {
    dutyCycleSpWriter.writeAsciiInt(percent)
  }

  def writeSpeed(degreesPerSecond:Int):Unit = {
    speedSpWriter.writeAsciiInt(degreesPerSecond)
  }

  def writePosition(degrees:Int):Unit = {
    positionWriter.writeAsciiInt(degrees)
  }

  def resetPosition():Unit = {
    writePosition(0)
  }

  /**
   * @return position in degrees todo double-check that
   */
  def readPosition():Int = {
    positionReader.readAsciiInt()
  }

  def readState():Seq[MotorState] = {
    stateReader.readString().split(' ').filterNot(_ == "").map{MotorState.stateNamesToStates(_)}
  }

  def readIsStalled():Boolean = {
    readState().contains(MotorState.STALLED)
  }

  def coast(): Unit = {
    writeStopAction(MotorStopCommand.COAST)
    writeCommand(MotorCommand.STOP)
  }

  def brake(): Unit = {
    writeStopAction(MotorStopCommand.BRAKE)
    writeCommand(MotorCommand.STOP)
  }

  def hold(): Unit = {
    writeStopAction(MotorStopCommand.HOLD)
    writeCommand(MotorCommand.STOP)
  }

  def runDutyCycle(percent:Int):Unit = {
    writeDutyCycle(percent)
    writeCommand(MotorCommand.RUN_DIRECT)
  }

  def runSpeed(degreesPerSecond:Int):Unit = {
    writeSpeed(degreesPerSecond)
    writeCommand(MotorCommand.RUN)
  }

  override def close(): Unit = {
    stateReader.close()
    positionReader.close()

    positionWriter.close()
    stopActionWriter.close()
    dutyCycleSpWriter.close()
    commandWriter.close()

  }
}

sealed case class Ev3LargeMotor(port:MotorPort,motorDir:Path) extends Motor(port,motorDir)

object Ev3LargeMotor {
  val driverName = "lego-ev3-l-motor"
}

sealed case class Ev3MediumMotor(port:MotorPort,motorDir:Path) extends Motor(port,motorDir)

object Ev3MediumMotor {
  val driverName = "lego-ev3-m-motor"
}

//todo use a Scala3 enum
object MotorCommand {
  /**
   * run-forever: Causes the motor to run until another command is sent
   */
  val RUN: MotorCommand = MotorCommand("run-forever")
  /**
   * run-to-abs-pos: Runs the motor to an absolute position specified by``position_sp`` and then stops the motor using the command specified in stop_action.
run-to-rel-pos: Runs the motor to a position relative to the current position value. The new position will be current position + position_sp. When the new position is reached, the motor will stop using the command specified by stop_action.
run-timed: Run the motor for the amount of time specified in time_sp and then stops the motor using the command specified by stop_action.
   */
  /**
   * run-direct: Runs the motor using the duty cycle specified by duty_cycle_sp. Unlike other run commands, changing duty_cycle_sp while running will take effect immediately.
   */
  val RUN_DIRECT: MotorCommand = MotorCommand("run-direct")
  /**
   * stop: Stop any of the run commands before they are complete using the command specified by stop_action.
   */
  val STOP: MotorCommand = MotorCommand("stop")

  /**
  reset: Resets all of the motor parameter attributes to their default values. This will also have the effect of stopping the motor.
   */
}

sealed case class MotorCommand(command:String)

/**
 * Determines the motors behavior when command is set to stop. Possible values are:
 */
//todo use a Scala3 enum
object MotorStopCommand {
  /**
   * Removes power from the motor. The motor will freely coast to a stop.
   */
  val COAST: MotorStopCommand = MotorStopCommand("coast")

  /**
   * Removes power from the motor and creates a passive electrical load. This is usually done by shorting the motor terminals together. This load will absorb the energy from the rotation of the motors and cause the motor to stop more quickly than coasting.
   */
  val BRAKE: MotorStopCommand = MotorStopCommand("brake")

  /**
   * Causes the motor to actively try to hold the current position. If an external force tries to turn the motor, the motor will “push back” to maintain its position.
   */
  val HOLD: MotorStopCommand = MotorStopCommand("hold")
}

sealed case class MotorStopCommand(command:String)

//todo use a Scala3 enum
object MotorState {
  /**
  running: Power is being sent to the motor.
  */
  val RUNNING: MotorState = MotorState("running")
  /**
  ramping: The motor is ramping up or down and has not yet reached a constant output level.
  */
  val RAMPING: MotorState = MotorState("ramping")
  /**
  holding: The motor is not turning, but rather attempting to hold a fixed position.
  */
  val HOLDING: MotorState = MotorState("holding")
  /**
  overloaded: The motor is turning as fast as possible, but cannot reach its speed_sp.
  */
  val OVERLOADED: MotorState = MotorState("overloaded")
  /**
    stalled: The motor is trying to run but is not turning at all.
  */
  val STALLED: MotorState = MotorState("stalled")

  val stateNamesToStates: Map[String, MotorState] = Seq(RUNNING,RAMPING,HOLDING,OVERLOADED,STALLED).map{ s => s.name -> s}.toMap
}

sealed case class MotorState(name:String)
