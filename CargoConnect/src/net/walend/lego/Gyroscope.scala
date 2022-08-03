package net.walend.lego

import ev3dev4s.measure.Degrees
import ev3dev4s.sensors.{Ev3Gyroscope, SensorPort, SensorPortScanner}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Gyroscope {

  val sensors: Map[SensorPort, Ev3Gyroscope] = SensorPortScanner.scanSensors.collect {
    case (port:SensorPort, sensor:Ev3Gyroscope) => port -> sensor
  }

  def readHeading(port: SensorPort): Degrees = sensors(port).headingMode().readHeading()

  def reset(port: SensorPort): Unit = sensors(port).headingMode().zero()
}

  