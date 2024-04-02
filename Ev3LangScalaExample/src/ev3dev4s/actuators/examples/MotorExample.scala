package ev3dev4s.actuators.examples

import ev3dev4s.Ev3System
import ev3dev4s.actuators.{Motor, MotorCommand, MotorStopCommand}
import ev3dev4s.measured.dimension.Dimensions.unitless

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object MotorExample {

  def main(args: Array[String]): Unit = {

    val firstMotor: Motor = Ev3System.portsToMotors.values.head
    println(firstMotor)
    println(firstMotor.readPosition())

    firstMotor.writeStopAction(MotorStopCommand.BRAKE)
    firstMotor.writeDutyCycle(0.5f * unitless)
    firstMotor.writeCommand(MotorCommand.RUN_DIRECT)
    Thread.sleep(1000)
    firstMotor.writeCommand(MotorCommand.STOP)
    Thread.sleep(1000)

    firstMotor.writeDutyCycle(0.1f * unitless)
    firstMotor.writeCommand(MotorCommand.RUN_DIRECT)
    Thread.sleep(1000)
    firstMotor.writeDutyCycle(1 * unitless)
    Thread.sleep(1000)
    firstMotor.writeDutyCycle(-0.5f * unitless)
    Thread.sleep(1000)
    firstMotor.writeDutyCycle(0.1f * unitless)
    Thread.sleep(1000)
    firstMotor.brake()
    Thread.sleep(1000)

    /*
        firstMotor.run(500)
        Thread.sleep(1000)
        firstMotor.brake()
    */
    println(firstMotor.readPosition())
  }
}


