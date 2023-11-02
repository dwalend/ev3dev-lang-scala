package ev3dev4s.actuators

import ev3dev4s.sysfs.Gadget

import ev3dev4s.Log

import ev3dev4s.scala2measure.DutyCycle
import ev3dev4s.scala2measure.DegreesPerSecond
import ev3dev4s.scala2measure.Conversions._
import ev3dev4s.scala2measure.Degrees
import ev3dev4s.scala2measure.MilliSeconds

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
sealed abstract class Motor(port: MotorPort, motorFS: Option[MotorFS]) extends Gadget(port, motorFS) {

  def writeCommand(command: MotorCommand): Unit = checkPort(_.writeCommand(command))

  def writeStopAction(command: MotorStopCommand): Unit = checkPort(_.writeStopAction(command))

  def writeDutyCycle(dutyCycle: DutyCycle): Unit = checkPort(_.writeDutyCycle(dutyCycle))

  def maxSpeed: DegreesPerSecond

  def observedMaxSpeed: DegreesPerSecond

  def writeSpeed(speed: DegreesPerSecond): Unit = {
    val safeSpeed = if (speed.abs < maxSpeed) speed
    else {
      Log.log(s"requested speed $speed is greater than $maxSpeed - using $maxSpeed")
      (speed.sign * maxSpeed).degreesPerSecond
    }
    checkPort(_.writeSpeed(safeSpeed))
  }

  def writeRampUpTime(fromZeroToMax: MilliSeconds): Unit = {
    checkPort(_.writeRampUpSpeed(fromZeroToMax))
  }

  def writeRampDownTime(fromMaxToZero: MilliSeconds): Unit = {
    checkPort(_.writeRampDownSpeed(fromMaxToZero))
  }

  def writePosition(degrees: Degrees): Unit = checkPort(_.writePosition(degrees))

  def resetPosition(): Unit = writePosition(0.degrees)

  def writeGoalPosition(degrees: Degrees): Unit = checkPort(_.writeGoalPosition(degrees))

  def writeDuration(milliseconds: MilliSeconds): Unit = checkPort(_.writeDuration(milliseconds))

  /**
   * @return position in degrees
   */
  def readPosition(): Degrees = checkPort(_.readPosition())

  def readState(): Array[MotorState] = checkPort(_.readState())

  def readIsStalled(): Boolean =
    readState().contains(MotorState.STALLED)

  def readIsRunning(): Boolean =
    readState().contains(MotorState.RUNNING)

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

  def runDutyCycle(dutyCycle: DutyCycle): Unit = {
    writeDutyCycle(dutyCycle)
    writeCommand(MotorCommand.RUN_DIRECT)
  }

  def run(speed: DegreesPerSecond): Unit = {
    writeSpeed(speed)
    writeCommand(MotorCommand.RUN)
  }

  def runToAbsolutePosition(speed: DegreesPerSecond, degrees: Degrees): Unit = {
    writeSpeed(speed)
    writeGoalPosition(degrees)
    writeCommand(MotorCommand.RUN_TO_ABSOLUTE_POSITION)
  }

  def runToRelativePosition(speed: DegreesPerSecond, degrees: Degrees): Unit = {
    writeSpeed(speed)
    writeGoalPosition(degrees)
    writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION)
  }

  def runForDuration(speed: DegreesPerSecond, milliseconds: MilliSeconds): Unit = {
    writeSpeed(speed)
    writeDuration(milliseconds)
    writeCommand(MotorCommand.RUN_TIME)
  }
}

sealed case class Ev3LargeMotor(override val port: MotorPort, md: Option[MotorFS]) extends Motor(port, md) {
  override def findGadgetFS(): Option[MotorFS] =
    MotorPortScanner.findGadgetDir(port, Ev3LargeMotor.driverName)
      .map(MotorFS)

  override val maxSpeed: DegreesPerSecond = 1050.degreesPerSecond
  override val observedMaxSpeed: DegreesPerSecond = 700.degreesPerSecond
}

object Ev3LargeMotor {
  val driverName = "lego-ev3-l-motor"
}

sealed case class Ev3MediumMotor(override val port: MotorPort, md: Option[MotorFS]) extends Motor(port, md) {
  override def findGadgetFS(): Option[MotorFS] =
    MotorPortScanner.findGadgetDir(port, Ev3MediumMotor.driverName)
      .map(MotorFS)

  override val maxSpeed: DegreesPerSecond = 1560.degreesPerSecond
  override val observedMaxSpeed: DegreesPerSecond = (maxSpeed.v * 0.7f).degreesPerSecond
}

object Ev3MediumMotor {
  val driverName = "lego-ev3-m-motor"
}

sealed case class MotorCommand(command: String)

object MotorCommand {
  /**
   * run-forever: Causes the motor to run until another command is sent
   */
  val RUN: MotorCommand = MotorCommand("run-forever")

  /**
   * run-to-abs-pos: Runs the motor to an absolute position specified by``position_sp`` and then stops the motor using the command specified in stop_action.
   */
  val RUN_TO_ABSOLUTE_POSITION: MotorCommand = MotorCommand("run-to-abs-pos")

  /**
   * run-to-rel-pos: Runs the motor to a position relative to the current position v. The new position will be current position + position_sp. When the new position is reached, the motor will stop using the command specified by stop_action. */
  val RUN_TO_RELATIVE_POSITION: MotorCommand = MotorCommand("run-to-rel-pos")

  /**
   * run-timed: Run the motor for the amount of time specified in time_sp and then stops the motor using the command specified by stop_action.
   */
  val RUN_TIME: MotorCommand = MotorCommand("run-timed")

  /**
   * run-direct: Runs the motor using the duty cycle specified by duty_cycle_sp. Unlike other run commands, changing duty_cycle_sp while running will take effect immediately.
   */
  val RUN_DIRECT: MotorCommand = MotorCommand("run-direct")
  /**
   * stop: Stop any of the run commands before they are complete using the command specified by stop_action.
   */
  val STOP: MotorCommand = MotorCommand("stop")

  /**
   * reset: Resets all of the motor parameter attributes to their default values. This will also have the effect of stopping the motor.
   */
}

/**
 * Determines the motors behavior when command is set to stop. Possible values are:
 */
sealed case class MotorStopCommand(command: String)

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

sealed case class MotorState(name: String)

object MotorState {
  /**
   * running: Power is being sent to the motor.
   */
  val RUNNING: MotorState = MotorState("running")
  /**
   * ramping: The motor is ramping up or down and has not yet reached a constant output level.
   */
  val RAMPING: MotorState = MotorState("ramping")
  /**
   * holding: The motor is not turning, but rather attempting to hold a fixed position.
   */
  val HOLDING: MotorState = MotorState("holding")
  /**
   * overloaded: The motor is turning as fast as possible, but cannot reach its speed_sp.
   */
  val OVERLOADED: MotorState = MotorState("overloaded")
  /**
   * stalled: The motor is trying to run but is not turning at all.
   */
  val STALLED: MotorState = MotorState("stalled")

  val values: Array[MotorState] = Array(RUNNING, RAMPING, HOLDING, OVERLOADED, STALLED)
}