package ev3dev4s.lego

import ev3dev4s.actuators.{Motor, MotorCommand, MotorPort, MotorPortScanner, MotorStopCommand}
import ev3dev4s.scala2measure.{Degrees, DegreesPerSecond, MilliSeconds}
import ev3dev4s.scala2measure.Conversions._
import ev3dev4s.os.Time

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Motors {

  private[lego] var motors: Map[MotorPort, Motor] = _

  private def scanMotors(): Unit = {
    motors = MotorPortScanner.scanMotors
    //induce lego default halt behavior
    motors.values.foreach(_.writeStopAction(MotorStopCommand.HOLD))
  }

  scanMotors()

  private[lego] def handleUnpluggedMotor[A](block: => A): A = {
    handleUnplugged(block, scanMotors)
  }

  private[lego] def watchForStop(motor: Motor): Unit =
    while ( {
      motor.readIsRunning()
    }) {
      //Hand over control to the OS to let it update motor state
      Time.pause(1.milliseconds)
    }

  def runForDegrees(port: MotorPort, degrees: Degrees): Unit = handleUnpluggedMotor {
    val motor: Motor = motors(port)
    motor.writeGoalPosition(degrees)
    motor.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION)
    watchForStop(motor)
  }

  def start(port: MotorPort): Unit = handleUnpluggedMotor {
    val motor: Motor = motors(port)
    motor.writeCommand(MotorCommand.RUN)
  }

  def setSpeed(port: MotorPort, speed: DegreesPerSecond): Unit = handleUnpluggedMotor {
    val motor: Motor = motors(port)
    motor.writeSpeed(speed)
  }

  def stop(port: MotorPort): Unit = handleUnpluggedMotor {
    val motor: Motor = motors(port)
    motor.writeCommand(MotorCommand.STOP)
  }

  def runForDegrees(port: MotorPort, degrees: Degrees, speed: DegreesPerSecond): Unit = handleUnpluggedMotor {
    val motor: Motor = motors(port)
    motor.writeSpeed(speed)
    motor.writeGoalPosition(degrees)
    motor.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION)
    watchForStop(motor)
  }

  def runForDuration(port: MotorPort, duration: MilliSeconds, speed: DegreesPerSecond): Unit = handleUnpluggedMotor {
    val motor: Motor = motors(port)
    motor.writeSpeed(speed)
    motor.writeDuration(duration)
    motor.writeCommand(MotorCommand.RUN_TIME)
    watchForStop(motor)
  }

  def start(port: MotorPort, speed: DegreesPerSecond): Unit = handleUnpluggedMotor {
    val motor: Motor = motors(port)
    motor.writeSpeed(speed)
    motor.writeCommand(MotorCommand.RUN)
  }

  def resetDegreesCounted(port: MotorPort): Unit = handleUnpluggedMotor {
    val motor: Motor = motors(port)
    motor.writePosition(0.degrees)
  }

  def degreesCounted(port: MotorPort): Degrees = handleUnpluggedMotor {
    val motor: Motor = motors(port)
    motor.readPosition()
  }

  def setStopCommand(port: MotorPort, stopCommand: MotorStopCommand): Unit = handleUnpluggedMotor {
    val motor = motors(port)
    motor.writeStopAction(stopCommand)
  }
}
