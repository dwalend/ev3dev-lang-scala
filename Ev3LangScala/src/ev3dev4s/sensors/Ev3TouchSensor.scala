package ev3dev4s.sensors

import ev3dev4s.sysfs.ChannelRereader

import java.io.File
import java.nio.file.Path

/**
 * @author David Walend
 * @since v0.0.0
 */
case class Ev3TouchSensor(port:SensorPort,sensorDir:Path) extends Sensor:

  private val touchReader = ChannelRereader(sensorDir.resolve("value0"))

  def readTouch(): Boolean = touchReader.readString().toInt == 1

  override def close(): Unit =
    touchReader.close()

object Ev3TouchSensor {
  val driverName = "lego-ev3-touch"
}