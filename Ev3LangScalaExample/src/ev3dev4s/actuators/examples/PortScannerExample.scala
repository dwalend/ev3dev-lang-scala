package ev3dev4s.actuators.examples

import ev3dev4s.actuators.MotorScanner

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object PortScannerExample {
  def main(args: Array[String]): Unit = {
    val motors = MotorScanner.scanMotorsDir
    println("Found: ")
    println(motors.mkString("\n"))
  }
}
