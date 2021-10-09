package ev3dev4s.sensors.examples

import ev3dev4s.Ev3System
import ev3dev4s.sensors.Ev3TouchSensor

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Ev3TouchSensorExample {

  def main(args: Array[String]): Unit = {
    val touchSensor = Ev3System.portsToSensors.values.collectFirst { case s: Ev3TouchSensor => s }.get
    for _ <- 1 to 20 do {
      println(touchSensor.readTouch())
      Thread.sleep(100)
    }
  }
}
