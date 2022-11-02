package ev3dev4s.sensors

import ev3dev4s.Log
import ev3dev4s.sysfs.{ChannelRereader, GadgetPortScanner, Port}

import java.io.File

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object SensorPortScanner extends GadgetPortScanner(new File("/sys/class/lego-sensor"),SensorPort.values){
  
  def scanSensors:Map[SensorPort,Sensor[_]] = {
    scanGadgetDirs.map{portAndDir =>
      val driverName = ChannelRereader.readString(portAndDir._2.resolve("driver_name"))
      Log.log(s"$portAndDir $driverName")
      val sensor = driverName match {
        case Ev3Gyroscope.driverName => Ev3Gyroscope(portAndDir._1, Option(portAndDir._2))
        case Ev3ColorSensor.driverName => Ev3ColorSensor(portAndDir._1, Option(portAndDir._2))
        case Ev3TouchSensor.driverName => Ev3TouchSensor(portAndDir._1, Option(portAndDir._2))
        case unknown => throw new IllegalArgumentException(s"Unknown driver $driverName in $portAndDir._2")
      }
      portAndDir._1 -> sensor
    }
  }
}

sealed case class SensorPort(name:Char) extends Port

object SensorPort {
  val One: SensorPort = SensorPort ('1')
  val Two: SensorPort = SensorPort ('2')
  val Three: SensorPort = SensorPort ('3')
  val Four: SensorPort = SensorPort ('4')

  val values: Array[SensorPort] = Array(One,Two,Three,Four)
}