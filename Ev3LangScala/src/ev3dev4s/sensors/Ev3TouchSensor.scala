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

  def readTouch(): Boolean = checkPort(_.readTouch())

object Ev3TouchSensor:
  val driverName = "lego-ev3-touch"

  case class Ev3TouchSensorFS(sensorDir:Path) extends SensorFS:
    //todo could be the common thing is to read value0, and the readTouch logic can just use that value
    private val touchReader = ChannelRereader(sensorDir.resolve("value0"))

    def readTouch(): Boolean = touchReader.readString().toInt == 1

    override def close(): Unit =
      touchReader.close()

