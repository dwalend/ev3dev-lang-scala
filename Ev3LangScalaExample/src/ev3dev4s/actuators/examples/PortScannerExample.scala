package ev3dev4s.actuators.examples

import ev3dev4s.actuators.MotorPortScanner
import ev3dev4s.sensors.SensorPortScanner

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object PortScannerExample {
  def main(args: Array[String]): Unit = {
    val sensors = SensorPortScanner.scanSensorsDir
    println(sensors.mkString("\n"))

    val motors = MotorPortScanner.scanMotorsDir
    println(motors.mkString("\n"))
  }
}
