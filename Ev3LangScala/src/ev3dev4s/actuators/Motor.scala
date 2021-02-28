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
  private val dutyCycleSpWriter = ChannelRewriter(motorDir.resolve("duty_cycle_sp"))
  private val stopActionWriter = ChannelRewriter(motorDir.resolve("stop_action"))

  private val positionReader = ChannelRereader(motorDir.resolve("position"))

  override def close(): Unit = this.synchronized {
    positionReader.close()

    stopActionWriter.close()
    dutyCycleSpWriter.close()
    commandWriter.close()

  }
}

sealed case class Ev3LargeMotor(port:MotorPort,motorDir:Path) extends Motor(port,motorDir)

object Ev3LargeMotor {
  val driverName = "lego-ev3-l-motor"
}

//todo use a Scala3 enum
object MotorCommand {
  /**
   * run-forever: Causes the motor to run until another command is sent
   */
  //  val RUN = MotorCommand("run-forever")
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

//todo use a Scala3 enum
object MotorStopCommand {
  //todo fill in the different stop commands here
}

sealed case class MotorStopCommand(command:String)
