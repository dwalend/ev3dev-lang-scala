package ev3dev4s.lego

import ev3dev4s.measure.Percent
import ev3dev4s.sensors.{Ev3TouchSensor, SensorPort, SensorPortScanner}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object TouchSensor {

  val sensors: Map[SensorPort, Ev3TouchSensor] = SensorPortScanner.scanSensors.collect {
    case (port:SensorPort, sensor:Ev3TouchSensor) => port -> sensor
  }

  def readTouch(port:SensorPort):Boolean = sensors(port).readTouch()
}