package ev3dev4s.actuators

import ev3dev4s.sysfs.{ChannelRereader, ChannelRewriter, GadgetFS}

import java.nio.file.Path

import ev3dev4s.measure.Degrees
import ev3dev4s.measure.Conversions._
import ev3dev4s.measure.DegreesPerSecond
import ev3dev4s.measure.MilliSeconds
import ev3dev4s.measure.DutyCycle

import ev3dev4s.Log

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
private[actuators] case class MotorFS(motorDir:Path) extends GadgetFS{

  private val commandWriter = ChannelRewriter(motorDir.resolve("command"))
  private val stopActionWriter = ChannelRewriter(motorDir.resolve("stop_action"))

  private val dutyCycleSpWriter = ChannelRewriter(motorDir.resolve("duty_cycle_sp"))
  private val speedSpWriter = ChannelRewriter(motorDir.resolve("speed_sp"))
  private val goalPositionWriter = ChannelRewriter(motorDir.resolve("position_sp"))
  private val timeWriter = ChannelRewriter(motorDir.resolve("time_sp"))

  private val positionWriter = ChannelRewriter(motorDir.resolve("position"))

  private val positionReader = ChannelRereader(motorDir.resolve("position"))
  private val stateReader = ChannelRereader(motorDir.resolve("state"),bufferLength = 52)

  //todo maybe writeCommand should be on the write side of a ReadWriteLock - and all others can be on the Read side?
  def writeCommand(command: MotorCommand):Unit =
    commandWriter.writeString(command.command)

  def writeStopAction(command:MotorStopCommand):Unit =
    stopActionWriter.writeString(command.command)

  def writeDutyCycle(dutyCycle:DutyCycle):Unit = {
    if(dutyCycle.abs > 100.dutyCyclePercent) Log.log(s"abs duty cycle $dutyCycle is greater than ${100.dutyCyclePercent}")
    dutyCycleSpWriter.writeAsciiInt(dutyCycle.round)
  }

  def writeSpeed(speed:DegreesPerSecond):Unit =
    speedSpWriter.writeAsciiInt(speed.round)

  def writePosition(degrees:Degrees):Unit =
    positionWriter.writeAsciiInt(degrees.round)

  def resetPosition():Unit =
    writePosition(0.degrees)

  def writeGoalPosition(degrees:Degrees):Unit =
    goalPositionWriter.writeAsciiInt(degrees.round)

  def writeDuration(milliseconds:MilliSeconds):Unit =
    timeWriter.writeAsciiInt(milliseconds.round)

  /**
   * @return position in degrees 
   */
  def readPosition():Degrees =
    positionReader.readAsciiInt().degrees

  val stateNamesToStates: Map[String, MotorState] = MotorState.values.map{ s => s.name -> s}.toMap
  def readState(): Array[MotorState] =
    stateReader.readString().split(' ').filterNot(_ == "").map{stateNamesToStates(_)}

  override def close(): Unit = {
    stateReader.close()
    positionReader.close()

    goalPositionWriter.close()
    positionWriter.close()
    stopActionWriter.close()
    dutyCycleSpWriter.close()
    commandWriter.close()
    timeWriter.close()
  }
}