package ev3dev4s.sensors

import ev3dev4s.sysfs.ChannelRereader

import java.io.File
import java.nio.file.Path

/**
 * @author David Walend
 * @since v0.0.0
 */
case class Ev3TouchSensor(override val port:SensorPort,initialSensorDir:Option[Path])
  extends Sensor(port,initialSensorDir.map(Ev3TouchSensor.Ev3TouchSensorFS(_))):

  override def findGadgetFS(): Option[Ev3TouchSensor.Ev3TouchSensorFS] =
    SensorPortScanner.findGadgetDir(port,Ev3TouchSensor.driverName)
      .map(Ev3TouchSensor.Ev3TouchSensorFS(_))

  def readTouch(): Boolean = checkPort(_.readValue0Int() == 1)

object Ev3TouchSensor:
  val driverName = "lego-ev3-touch"

  //special class because the touch sensor doesn't have modes
  case class Ev3TouchSensorFS(sensorDir:Path) extends SensorFS:
    private val value0Reader = ChannelRereader(sensorDir.resolve("value0"))

    def readValue0Int(): Int = value0Reader.readAsciiInt()

    override def close(): Unit =
      value0Reader.close()