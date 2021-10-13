package ev3dev4s.sensors

import ev3dev4s.sysfs.{ChannelRereader, GadgetPortScanner, Port}

import java.io.File
import java.nio.file.Path
import scala.collection.immutable.ArraySeq

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object SensorPortScanner extends GadgetPortScanner(new File("/sys/class/lego-sensor"),SensorPort.values):
  
  def scanSensors:Map[SensorPort,Sensor[_]] =
    scanGadgetDirs.map{(port,sensorDir) =>
      val driverName = ChannelRereader.readString(sensorDir.resolve("driver_name"))
      val sensor = driverName match
//        case Ev3Gyroscope.driverName => Ev3Gyroscope(port,Path.of(sensorDir.getAbsolutePath))
//        case Ev3ColorSensor.driverName => Ev3ColorSensor(port,Path.of(sensorDir.getAbsolutePath))
        case Ev3TouchSensor.driverName => Ev3TouchSensor(port,Option(sensorDir))
        case unknown => throw new IllegalArgumentException(s"Unknown driver $driverName")
      port -> sensor
    }

enum SensorPort(val name:Char) extends Port:
  case One extends SensorPort('1')
  case Two extends SensorPort('2')
  case Three extends SensorPort('3')
  case Four extends SensorPort('4')