package ev3dev4s.sensors.examples

import ev3dev4s.sensors.Ev3Battery

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Ev3BatteryExample {

  def main(args: Array[String]): Unit = {
    for _ <- 1 to 20 do {
      println(s"${Ev3Battery.readMicrovolts()} ${Ev3Battery.readMicoramps()}")
      Thread.sleep(100)
    }
  }
}
