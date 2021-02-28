package ev3dev4s.actuators.examples

import ev3dev4s.actuators.MotorPortScanner

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object PortScannerExample {
  def main(args: Array[String]): Unit = {
    val motors = MotorPortScanner.scanMotorsDir
    println(motors.mkString("\n"))
  }
}
