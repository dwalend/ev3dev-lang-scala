package ev3dev4s.sensors.gyroscope.examples

import ev3dev4s.sensors.{Ev3Gyroscope, Ev3KeyPad, SensorPortScanner}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object GyroCalibrateExample extends Runnable {
  def main(args: Array[String]): Unit =
    run()

  override def run(): Unit = {
    val gyro: Ev3Gyroscope = SensorPortScanner.scanSensors.values.collectFirst {
      case x: Ev3Gyroscope => x
    }.get

    gyro.despin()

    Ev3KeyPad.blockUntilAnyKey()
  }
}
