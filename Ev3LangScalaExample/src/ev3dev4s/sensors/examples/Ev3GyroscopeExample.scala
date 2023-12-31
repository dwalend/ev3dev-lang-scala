package ev3dev4s.sensors.examples

import ev3dev4s.Ev3System
import ev3dev4s.sensors.Ev3Gyroscope

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Ev3GyroscopeExample {

  def main(args: Array[String]): Unit = {
    val gyroscope: Ev3Gyroscope = Ev3System.portsToSensors.values.collectFirst {
      case s: Ev3Gyroscope => s
    }.get

    val headingMode1 = gyroscope.headingMode()
    for (_ <- 1 to 10) {
      println(headingMode1.readHeading())
      Thread.sleep(100)
    }
  }
}