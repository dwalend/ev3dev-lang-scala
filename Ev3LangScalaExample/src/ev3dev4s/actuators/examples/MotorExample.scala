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

    val motorA: Motor = Ev3System.motorA()
    println(motorA)
    motorA.howru()

    motorA.writeStopAction(MotorStopCommand.BRAKE)
    motorA.writeDutyCycle(50.dutyCyclePercent)
    motorA.writeCommand(MotorCommand.RUN_DIRECT)
    Thread.sleep(1000)
    motorA.writeCommand(MotorCommand.STOP)
    Thread.sleep(1000)

    motorA.writeDutyCycle(10.dutyCyclePercent)
    motorA.writeCommand(MotorCommand.RUN_DIRECT)
    Thread.sleep(1000)
    motorA.writeDutyCycle(100.dutyCyclePercent)
    Thread.sleep(1000)
    motorA.writeDutyCycle(-50.dutyCyclePercent)
    Thread.sleep(1000)
    motorA.writeDutyCycle(10.dutyCyclePercent)
    Thread.sleep(1000)
    motorA.brake()
    Thread.sleep(1000)

    motorA.howru()

    /*
        motorA.run(500)
        Thread.sleep(1000)
        motorA.brake()
    */
    println(motorA.readPosition())
  }
}
