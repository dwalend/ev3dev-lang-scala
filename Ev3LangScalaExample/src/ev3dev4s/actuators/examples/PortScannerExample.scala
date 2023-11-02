package ev3dev4s.actuators.examples

import ev3dev4s.Ev3System
import ev3dev4s.sensors.Sensor
import ev3dev4s.sysfs.{ChannelRereader, GadgetPortScanner, Port}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object PortScannerExample {

  def main(args: Array[String]): Unit = {
    println(Ev3System.describeSensors())
    println(Ev3System.describeMotors())
  }
}
