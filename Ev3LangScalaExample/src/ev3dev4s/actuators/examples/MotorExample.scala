package ev3dev4s.actuators.examples

import ev3dev4s.Ev3System
import ev3dev4s.actuators.{Motor, MotorCommand, MotorPort, MotorPortScanner, MotorStopCommand}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object MotorExample:

  def main(args: Array[String]): Unit =

    val firstMotor: Motor = Ev3System.portsToMotors.values.head
    println(firstMotor)
    println(firstMotor.readPosition())

    firstMotor.writeStopAction(MotorStopCommand.COAST)
    firstMotor.writeDutyCycle(50)
    firstMotor.writeCommand(MotorCommand.RUN_DIRECT)
    Thread.sleep(1000)
    firstMotor.writeCommand(MotorCommand.STOP)

    firstMotor.writeDutyCycle(10)
    firstMotor.writeCommand(MotorCommand.RUN_DIRECT)
    Thread.sleep(1000)
    firstMotor.writeDutyCycle(100)
    Thread.sleep(1000)
    firstMotor.writeDutyCycle(-50)
    Thread.sleep(1000)
    firstMotor.writeDutyCycle(10)
    Thread.sleep(1000)
    firstMotor.brake()
    Thread.sleep(1000)


    firstMotor.run(500)
    Thread.sleep(1000)
    firstMotor.brake()

    println(firstMotor.readPosition())


