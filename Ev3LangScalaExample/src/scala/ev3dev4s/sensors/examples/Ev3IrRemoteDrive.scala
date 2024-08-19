package ev3dev4s.sensors.examples

import ev3dev4s.actuators.{Motor, MotorPort}
import ev3dev4s.sensors.Ev3InfraredSensor
import ev3dev4s.Ev3System
import ev3dev4s.scala2measure.Conversions._
import ev3dev4s.scala2measure.DegreesPerSecond

import scala.annotation.tailrec

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */

object Ev3IrRemoteDrive extends Runnable {

  def main(args: Array[String]): Unit = run()
   
  private val remote = {
    val sensor:Ev3InfraredSensor = Ev3System.portsToSensors.values.collectFirst { case ir: Ev3InfraredSensor => ir }.get
    sensor.remoteMode()
  }

  private val speed: DegreesPerSecond = 700.degreesPerSecond
  private val leftMotor: Motor = Ev3System.portsToMotors(MotorPort.B)
  private val rightMotor: Motor = Ev3System.portsToMotors(MotorPort.C)

  @tailrec
  override def run(): Unit = {
    import Ev3InfraredSensor.RemoteButton._
    
    val buttonSet: Set[Ev3InfraredSensor.RemoteButton] = remote.readRemote1()
    if(buttonSet.contains(LeftUp)) leftMotor.run(speed)
    else if(buttonSet.contains(LeftDown)) leftMotor.run(-speed)
    else leftMotor.brake()

    if(buttonSet.contains(RightUp)) rightMotor.run(speed)
    else if(buttonSet.contains(RightDown)) rightMotor.run(-speed)
    else rightMotor.brake()

    if(!buttonSet.contains(Beacon)) run()
  }
}




