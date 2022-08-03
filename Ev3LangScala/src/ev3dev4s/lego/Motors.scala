package ev3dev4s.lego

import ev3dev4s.actuators.{Motor, MotorCommand, MotorPort, MotorPortScanner, MotorState, MotorStopCommand}
import ev3dev4s.measure.{Degrees, DegreesPerSecond}
import ev3dev4s.measure.Conversions._
import ev3dev4s.os.Time

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Motors {

  val motors:Map[MotorPort,Motor]  = MotorPortScanner.scanMotors

  def watchForStop(motor:Motor):Unit =
    while({
      motor.readState().contains(MotorState.RUNNING)  //todo use isRunning after recompile
    }) { Time.pause(1.milliseconds)}

  def runForDegrees(port:MotorPort,degrees: Degrees): Unit = {
    val motor: Motor = motors(port)
    motor.writeGoalPosition(degrees)
    motor.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION)
    watchForStop(motor)
  }

  def start(port: MotorPort): Unit = {
    val motor: Motor = motors(port)
    motor.writeCommand(MotorCommand.RUN)
  }

  def setSpeed(port: MotorPort,speed:DegreesPerSecond): Unit = {
    val motor: Motor = motors(port)
    motor.writeSpeed(speed)
  }

  def stop(port: MotorPort): Unit = {
    val motor: Motor = motors(port)
    motor.writeCommand(MotorCommand.STOP)
  }

  def runForDegrees(port:MotorPort,degrees: Degrees,speed:DegreesPerSecond): Unit = {
    val motor: Motor = motors(port)
    motor.writeSpeed(speed)
    motor.writeGoalPosition(degrees)
    motor.writeCommand(MotorCommand.RUN_TO_RELATIVE_POSITION)
    watchForStop(motor)
  }

  def start(port: MotorPort,speed: DegreesPerSecond): Unit = {
    val motor: Motor = motors(port)
    motor.writeSpeed(speed)
    motor.writeCommand(MotorCommand.RUN)
  }

  def resetDegreesCounted(port: MotorPort): Unit = {
    val motor: Motor = motors(port)
    motor.writePosition(0.degrees)
  }

  def degreesCounted(port: MotorPort): Degrees = {
    val motor: Motor = motors(port)
    motor.readPosition()
  }
}
