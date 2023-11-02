package ev3dev4s.lego

import ev3dev4s.sensors.{Ev3TouchSensor, SensorPort, SensorPortScanner}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object TouchSensor {

  private var sensors: Map[SensorPort, Ev3TouchSensor] = _

  private def scanSensors(): Unit = {
    sensors = SensorPortScanner.scanSensors.collect {
      case (port: SensorPort, sensor: Ev3TouchSensor) => port -> sensor
    }
  }

  scanSensors()

  def readTouch(port: SensorPort): Boolean = handleUnplugged(sensors(port).readTouch(), scanSensors)
}