package ev3dev4s.sensors

import ev3dev4s.sysfs.{ChannelRereader,Port}

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
  
  private def scanSensorDirs:Map[SensorPort,Path] =
    //noinspection SpellCheckingInspection
    val sensorsDir: File = new File("/sys/class/lego-sensor")
    ArraySeq.unsafeWrapArray(sensorsDir.listFiles()).map { (sensorDir: File) =>
      //read the address to learn which port
      val addressPath = Path.of(sensorDir.getAbsolutePath,"address")
      val port = namesToPorts(ChannelRereader.readString(addressPath).last)
      (port -> sensorDir.toPath)
    }.toMap

  def scanSensors:Map[SensorPort,Sensor[_]] =
    scanSensorDirs.map{(port,sensorDir) =>
      val driverName = ChannelRereader.readString(sensorDir.resolve("driver_name"))
      val sensor = driverName match
//        case Ev3Gyroscope.driverName => Ev3Gyroscope(port,Path.of(sensorDir.getAbsolutePath))
//        case Ev3ColorSensor.driverName => Ev3ColorSensor(port,Path.of(sensorDir.getAbsolutePath))
        case Ev3TouchSensor.driverName => Ev3TouchSensor(port,Option(sensorDir))
        case unknown => throw new IllegalArgumentException(s"Unknown driver $driverName")
      port -> sensor
    }

  private[sensors] def findSensorDir(port: SensorPort,expectedDriverName:String):Option[Path] =
    scanSensorDirs.get(port)
      .map{dir =>
        val foundDriverName = ChannelRereader.readString(dir.resolve("driver_name") )
        if(foundDriverName == expectedDriverName) dir
        else throw WrongGadgetInPortException(port,expectedDriverName,foundDriverName)
      }

  val namesToPorts: Map[Char, SensorPort] = SensorPort.values.map{ port => port.name -> port}.toMap

case class WrongGadgetInPortException(port:SensorPort,expectedDriverName:String,foundDriverName:String)
  extends Exception(s"Expected $expectedDriverName in $port but found $foundDriverName")

enum SensorPort(val name:Char) extends Port:
  case One extends SensorPort('1')
  case Two extends SensorPort('2')
  case Three extends SensorPort('3')
  case Four extends SensorPort('4')