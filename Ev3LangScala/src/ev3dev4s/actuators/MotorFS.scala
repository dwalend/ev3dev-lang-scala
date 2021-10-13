package ev3dev4s.actuators

import ev3dev4s.sysfs.{ChannelRereader, ChannelRewriter, GadgetFS}

import java.nio.file.Path

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
private[actuators] case class MotorFS(motorDir:Path) extends GadgetFS:

  private val commandWriter = ChannelRewriter(motorDir.resolve("command"))
  private val stopActionWriter = ChannelRewriter(motorDir.resolve("stop_action"))

  private val dutyCycleSpWriter = ChannelRewriter(motorDir.resolve("duty_cycle_sp"))
  private val speedSpWriter = ChannelRewriter(motorDir.resolve("speed_sp"))
  private val positionWriter = ChannelRewriter(motorDir.resolve("position"))

  private val positionReader = ChannelRereader(motorDir.resolve("position"))
  private val stateReader = ChannelRereader(motorDir.resolve("state"),bufferLength = 52)

  private val goalPositionWriter = ChannelRewriter(motorDir.resolve("position_sp"))

  //todo maybe writeCommand should be on the write side of a ReadWriteLock - and all others can be on the Read side?
  def writeCommand(command: MotorCommand):Unit =
    commandWriter.writeString(command.command)

  def writeStopAction(command:MotorStopCommand):Unit =
    stopActionWriter.writeString(command.command)

  def writeDutyCycle(percent:Int):Unit =
    dutyCycleSpWriter.writeAsciiInt(percent)

  def writeSpeed(degreesPerSecond:Int):Unit =
    speedSpWriter.writeAsciiInt(degreesPerSecond)

  def writePosition(degrees:Int):Unit =
    positionWriter.writeAsciiInt(degrees)

  def resetPosition():Unit =
    writePosition(0)

  def writeGoalPosition(degrees:Int):Unit =
    goalPositionWriter.writeAsciiInt(degrees)
  /**
   * @return position in degrees todo double-check that
   */
  def readPosition():Int =
    positionReader.readAsciiInt()

  val stateNamesToStates: Map[String, MotorState] = MotorState.values.map{ s => s.name -> s}.toMap
  def readState(): Array[MotorState] =
    stateReader.readString().split(' ').filterNot(_ == "").map{stateNamesToStates(_)}

  override def close(): Unit =
    //todo likely don't care if any of these throws an exception
    stateReader.close()
    positionReader.close()

    goalPositionWriter.close()
    positionWriter.close()
    stopActionWriter.close()
    dutyCycleSpWriter.close()
    commandWriter.close()