package ev3dev4s.sensors

import ev3dev4s.sysfs.ChannelRereader

import java.io.File
import java.nio.file.Path
import scala.collection.immutable.ArraySeq

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object SensorPortScanner:

  def scanSensorsDir:Map[SensorPort,Sensor] = {
    val sensorsDir: File = new File("/sys/class/lego-sensor")
    ArraySeq.unsafeWrapArray(sensorsDir.listFiles()).map{ (sensorDir: File) =>
      //read the address to learn which port
      val addressPath = Path.of(sensorDir.getAbsolutePath,"address")
      val port = SensorPort.namesToPorts(ChannelRereader.readString(addressPath).last)

      //read the driver to figure out large vs medium
      val driverPath = Path.of(sensorDir.getAbsolutePath,"driver_name")
      val driverName = ChannelRereader.readString(driverPath)

      driverName match
        case Ev3Gyroscope.driverName => Ev3Gyroscope(port,Path.of(sensorDir.getAbsolutePath))
        case Ev3ColorSensor.driverName => Ev3ColorSensor(port,Path.of(sensorDir.getAbsolutePath))
        case Ev3TouchSensor.driverName => Ev3TouchSensor(port,Path.of(sensorDir.getAbsolutePath))
        case _ => throw new IllegalArgumentException(s"Unknown driver $driverName")
    }
  }.map{sensor => sensor.port -> sensor}.toMap

//todo use a Scala3 enum
object SensorPort:
  val One: SensorPort = SensorPort('1')
  val Two: SensorPort = SensorPort('2')
  val Three: SensorPort = SensorPort('3')
  val Four: SensorPort = SensorPort('4')

  val namesToPorts: Map[Char, SensorPort] = Set(One,Two,Three,Four).map{ port => port.name -> port}.toMap

sealed case class SensorPort(name:Char)