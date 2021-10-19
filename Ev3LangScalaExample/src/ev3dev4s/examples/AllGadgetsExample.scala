package ev3dev4s.examples

import ev3dev4s.{Ev3System, sensors}
import ev3dev4s.actuators.{Ev3Led, Motor}
import ev3dev4s.sensors.{Ev3ColorSensor, Ev3Gyroscope, Ev3TouchSensor, Mode, Sensor}
import ev3dev4s.sysfs.UnpluggedException

/**
 * Excercise all the gadgets on the robot - in a loop
 *
 * @author David Walend
 * @since v0.0.0
 */
object AllGadgetsExample extends Runnable:
  override def run(): Unit =
    Ev3System.leftLed.writeOff()
    Ev3System.rightLed.writeOff()

    val motors: Iterable[Motor] = Ev3System.portsToMotors.values
    val sensors: Iterable[Sensor[_]] = Ev3System.portsToSensors.values

    sensors.collect{case gyroscope: Ev3Gyroscope => gyroscope.headingMode()}
    sensors.collect{case colorSensor: Ev3ColorSensor => colorSensor.reflectMode()}

    while(true) do
      motors.foreach{ (motor:Motor) =>
        try
          println(s"motor $motor ${motor.readPosition()}")
        catch
          case ux:UnpluggedException => println(s"motor $motor unplugged")
      }
      sensors.foreach{ (sensor:Sensor[_]) =>
        try
          sensor match
            case gyroscope: Ev3Gyroscope =>
              val number = gyroscope.currentMode.collect{
                case m:gyroscope.HeadingMode => m.readHeading()
              }
              println(s"sensor $sensor $number")
            case colorSensor: Ev3ColorSensor =>
              val number = colorSensor.currentMode.collect{
                case m:colorSensor.ReflectMode => m.readReflect()
              }
              println(s"sensor $sensor $number")
            case touchSensor: Ev3TouchSensor => println(s"sensor $sensor ${touchSensor.readTouch()}")
        catch
          case ux:UnpluggedException => println(s"sensor $sensor unplugged")

      }
      System.gc()
      Thread.sleep(1000)
      println()