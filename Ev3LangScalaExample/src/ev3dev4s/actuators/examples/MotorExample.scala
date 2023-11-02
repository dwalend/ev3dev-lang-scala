package ev3dev4s.actuators.examples

import ev3dev4s.Ev3System
import ev3dev4s.actuators.{Motor, MotorCommand, MotorPort, MotorPortScanner, MotorStopCommand}

import ev3dev4s.scala2measure.Conversions._

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object MotorExample {

  def main(args: Array[String]): Unit = {

    val firstMotor: Motor = Ev3System.motorA()
    println(firstMotor)
    println(firstMotor.readPosition())

    firstMotor.writeStopAction(MotorStopCommand.BRAKE)
    firstMotor.writeDutyCycle(50.dutyCyclePercent)
    firstMotor.writeCommand(MotorCommand.RUN_DIRECT)
    Thread.sleep(1000)
    firstMotor.writeCommand(MotorCommand.STOP)
    Thread.sleep(1000)

    firstMotor.writeDutyCycle(10.dutyCyclePercent)
    firstMotor.writeCommand(MotorCommand.RUN_DIRECT)
    Thread.sleep(1000)
    firstMotor.writeDutyCycle(100.dutyCyclePercent)
    Thread.sleep(1000)
    firstMotor.writeDutyCycle(-50.dutyCyclePercent)
    Thread.sleep(1000)
    firstMotor.writeDutyCycle(10.dutyCyclePercent)
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


