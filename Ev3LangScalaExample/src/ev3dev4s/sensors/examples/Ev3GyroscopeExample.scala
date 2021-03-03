package ev3dev4s.sensors.examples

import ev3dev4s.sensors.{Ev3Gyroscope, SensorPortScanner}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Ev3GyroscopeExample {

  def main(args: Array[String]): Unit = {
    val gyroscope = SensorPortScanner.scanSensorsDir.values.collectFirst { case s: Ev3Gyroscope => s }.get

    val headingMode1 = gyroscope.headingMode()
    for (i <- 1 to 10) {
      println(headingMode1.readHeading())
      Thread.sleep(100)
    }

    gyroscope.calibrateMode()

    val headingMode2 = gyroscope.headingMode()
    for (i <- 1 to 10) {
      println(headingMode2.readHeading())
      Thread.sleep(100)
    }
  }
}
