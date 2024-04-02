package ev3dev4s.lego

import ev3dev4s.measured.dimension.Uno
import ev3dev4s.sensors.{Ev3ColorSensor, SensorPort, SensorPortScanner}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object ColorSensor {

  private var sensors: Map[SensorPort, Ev3ColorSensor] = _
  private def scanSensors(): Unit = {
    sensors = SensorPortScanner.scanSensors.collect {
      case (port: SensorPort, sensor: Ev3ColorSensor) => port -> sensor
    }
  }
  scanSensors()

  def readReflected(port: SensorPort): Uno = handleUnplugged(
    sensors(port).reflectMode().readReflect(),
    scanSensors
  )

  def readColor(port: SensorPort): Ev3ColorSensor.Color = handleUnplugged(sensors(port).colorMode().readColor(),scanSensors)

  def readAmbient(port: SensorPort): Uno = handleUnplugged(sensors(port).ambientMode().readAmbient(),scanSensors)
}