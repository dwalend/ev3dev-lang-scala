package ev3dev4s.examples

import ev3dev4s.Ev3System
import ev3dev4s.actuators.Motor
import ev3dev4s.sensors.{Ev3ColorSensor, Ev3Gyroscope, Ev3TouchSensor, Sensor}
import ev3dev4s.sysfs.UnpluggedException

/**
 * Exercise all the gadgets on the robot - in a loop
 *
 * @author David Walend
 * @since v0.0.0
 */
//noinspection RedundantBlock
object AllGadgetsExample extends Runnable {
  def describeMotors(): Unit = {
    val motors: Iterable[Motor] = Ev3System.portsToMotors.values
    motors.foreach { (motor: Motor) => motor.howru() }
  }

  def initializeSensors(): Unit = {
    val sensors: Iterable[Sensor[_]] = Ev3System.portsToSensors.values
    sensors.collect { case gyroscope: Ev3Gyroscope => gyroscope.headingMode() }
    sensors.collect { case colorSensor: Ev3ColorSensor => colorSensor.reflectMode() }
  }

  def describeSensors(): Unit = {
    val sensors: Iterable[Sensor[_]] = Ev3System.portsToSensors.values
    sensors.foreach {
      (sensor: Sensor[_]) => {
        try {
          sensor match {
            case gyroscope: Ev3Gyroscope => { gyroscope.howru() }
            case colorSensor: Ev3ColorSensor => { colorSensor.howru() }
            case touchSensor: Ev3TouchSensor => { touchSensor.howru() }
          }
        }
        catch { case ux: UnpluggedException => println(s"sensor $sensor unplugged") }
      }
    }
  }

  override def run(): Unit = {
    Ev3System.leftLed.writeOff()
    Ev3System.rightLed.writeOff()
    initializeSensors()

    while (true) {
      describeSensors()
      describeMotors()
      System.gc()
      Thread.sleep(1000)
      println()
    }
  }
}
