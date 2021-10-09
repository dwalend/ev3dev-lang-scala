package ev3dev4s.actuators.examples

import ev3dev4s.Ev3System


/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object PortScannerExample:
  def main(args: Array[String]): Unit =
    val sensors = Ev3System.portsToSensors
    println(sensors.mkString("\n"))

    val motors = Ev3System.portsToMotors
    println(motors.mkString("\n"))
