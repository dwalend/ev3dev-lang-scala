package net.walend.lego

import ev3dev4s.measure.Percent
import ev3dev4s.sensors.{Ev3ColorSensor, SensorPort, SensorPortScanner}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object ColorSensor {

  val sensors: Map[SensorPort, Ev3ColorSensor] = SensorPortScanner.scanSensors.collect {
    case (port:SensorPort, sensor:Ev3ColorSensor) => port -> sensor
  }

  def readReflected(port: SensorPort): Percent = sensors(port).reflectMode().readReflect()

  def readColor(port: SensorPort): Ev3ColorSensor.Color = sensors(port).colorMode().readColor()

  def readAmbient(port: SensorPort): Percent = sensors(port).ambientMode().readAmbient()
}