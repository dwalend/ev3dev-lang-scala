package ev3dev4s.sensors.examples

import ev3dev4s.Ev3System
import ev3dev4s.os.Time
import ev3dev4s.sensors.Ev3Gyroscope

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Ev3GyroHeadingAndRateExample {

  def main(args: Array[String]): Unit = {
    val gyroscope: Ev3Gyroscope = Ev3System.portsToSensors.values.collectFirst {
      case s: Ev3Gyroscope => s
    }.get

    val headingMode = gyroscope.headingAndRateMode()
    for (_ <- 1 to 100) {
      println(s"${Time.now()} ${headingMode.readHeading()} ${headingMode.readRate()}")
      Thread.sleep(100)
    }
  }
}