package ev3dev4s.lego

import ev3dev4s.measure.Degrees
import ev3dev4s.sensors.{Ev3Gyroscope, SensorPort, SensorPortScanner}
import ev3dev4s.sysfs.UnpluggedException

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Gyroscope {

  private var sensors: Map[SensorPort, Ev3Gyroscope] = _
  private def scanSensors():Unit = {
    sensors = SensorPortScanner.scanSensors.collect {
      case (port: SensorPort, sensor: Ev3Gyroscope) => port -> sensor
    }
  }
  scanSensors()

  def readHeading(port: SensorPort): Degrees = handleUnplugged[Degrees](sensors(port).headingMode().readHeading(),scanSensors)

  def reset(port: SensorPort): Unit = handleUnplugged[Unit](sensors(port).headingMode().zero(),scanSensors)

}

  