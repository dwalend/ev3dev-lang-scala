package ev3dev4s.sensors

import ev3dev4s.sysfs.ChannelRereader

import java.io.File
import java.nio.file.Path

/**
 * @author David Walend
 * @since v0.0.0
 */
object Ev3Battery extends AutoCloseable {

  private val batteryDir = new File("/sys/class/power_supply/lego-ev3-battery")
  private lazy val voltageRereader: ChannelRereader = ChannelRereader(Path.of(batteryDir.getAbsolutePath,"voltage_now"))
  private lazy val currentRereader: ChannelRereader = ChannelRereader(Path.of(batteryDir.getAbsolutePath,"current_now"))

  def readMicrovolts(): Int = voltageRereader.readString().toInt
  def readMillivolts(): Int = readMicrovolts() / 1000

  def readMicoramps(): Int = currentRereader.readString().toInt

  override def close(): Unit = {
    currentRereader.close()
    voltageRereader.close()
  }
}


