package ev3dev4s.actuators.examples

import ev3dev4s.Ev3System
import ev3dev4s.actuators.{Motor, MotorCommand, MotorPort, MotorPortScanner, MotorStopCommand}
import ev3dev4s.sysfs.UnpluggedException

/**
 * Unplung and plug in the motor to see the behavior
 *
 * @author David Walend
 * @since v0.0.0
 */
object MotorUnplugExample {

  def main(args: Array[String]): Unit = {

    val firstMotor: Motor = Ev3System.portsToMotors.values.head
    println(firstMotor)

    //unplug the motor, plug it back in, to see the behavior
    while (true) {
      Thread.sleep(1000)
      try {
        //println(firstMotor.readPosition())
        firstMotor.brake()
        println("firstMotor.brake()")
      }
      catch {
        case x: UnpluggedException => x.printStackTrace()
      }
    }
  }
}
