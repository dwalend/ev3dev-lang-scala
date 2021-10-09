package ev3dev4s.sensors.examples

import ev3dev4s.Ev3System
import ev3dev4s.sensors.Ev3ColorSensor

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Ev3ColorSensorExample:

  def main(args: Array[String]): Unit =
    val colorSensor = Ev3System.portsToSensors.values.collectFirst { case s: Ev3ColorSensor => s }.get

    val headingMode1 = colorSensor.reflectMode()
    for _ <- 1 to 20 do
      println(headingMode1.readReflect())
      Thread.sleep(100)
