package ev3dev4s.actuators

import ev3dev4s.sysfs.{ChannelRereader, ChannelRewriter, GadgetFS}

import java.nio.file.Path
import ev3dev4s.Log
import ev3dev4s.measured.dimension.Dimensions.{degree, second, *, given}
import ev3dev4s.measured.dimension.{Angle, AngularVelocity, Time, Uno, milli, percent}

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

  private val rampUpWriter = ChannelRewriter(motorDir.resolve("ramp_up_sp"))
  private val rampDownWriter = ChannelRewriter(motorDir.resolve("ramp_down_sp"))

  private val positionReader = ChannelRereader(motorDir.resolve("position"))
  private val stateReader = ChannelRereader(motorDir.resolve("state"),bufferLength = 52)

  //todo maybe writeCommand should be on the write side of a ReadWriteLock - and all others can be on the Read side?
  def writeCommand(command: MotorCommand):Unit =
    commandWriter.writeString(command.command)

  def writeStopAction(command:MotorStopCommand):Unit =
    stopActionWriter.writeString(command.command)

  def writeDutyCycle(dutyCycle:Uno):Unit = {
    if(abs(dutyCycle) > 1) Log.log(s"abs duty cycle ${dutyCycle.in(percent)} is greater than ${unitless.in(percent)}")
    dutyCycleSpWriter.writeAsciiInt(round(dutyCycle))
  }

  def writeSpeed(speed:AngularVelocity):Unit =
    speedSpWriter.writeAsciiInt(round(speed))

  def writePosition(degrees:Angle):Unit =
    positionWriter.writeAsciiInt(round(degrees))

  def resetPosition():Unit =
    writePosition(0 * degree)

  def writeGoalPosition(angle:Angle):Unit =
    goalPositionWriter.writeAsciiInt(round(angle))

  def writeDuration(time:Time):Unit =
    timeWriter.writeAsciiInt(time.in(milli(second)).round)

  def writeRampUpSpeed(fromZeroToMax:Time):Unit = {
    rampUpWriter.writeAsciiInt(fromZeroToMax.in(milli(second)).round)
  }

  def writeRampDownSpeed(fromMaxToZero: Time): Unit = {
    rampDownWriter.writeAsciiInt(fromMaxToZero.in(milli(second)).round)
  }


  /**
   * @return position in degrees 
   */
  def readPosition():Angle =
    positionReader.readAsciiInt() * degree

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