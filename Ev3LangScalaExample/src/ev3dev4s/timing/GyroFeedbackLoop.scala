package ev3dev4s.timing

import ev3dev4s.sensors.gyroscope.examples.{Robot,GyroDriveStraight}
import ev3dev4s.os.Time
import ev3dev4s.Log

object GyroFeedbackLoop extends Runnable:
  def main(args: Array[String]): Unit =
    run()

  override def run(): Unit =
//    Robot.gyroscope.despin()
    Robot.headingMode.zero()
    driveGyroFeedbackTime(0,0,20000)

    Robot.hold()
    driveGyroFeedbackTime(0,0,60000)

    Robot.hold()

  /**
   * Gyro straight with the duty cycle
   *
   * @param goalHeading that the gyroscope should read during this traverse
   * @param dutyCycle degrees per second to turn the motors
   * @param distanceMm distance to travel
   */
  def driveGyroFeedbackTime(
                                 goalHeading: Int,
                                 dutyCycle: Int,
                                 milliseconds: Int
                           ): Unit =
    val startTime = Time.now()

    var count = 0
    def notDoneYet():Boolean =
      val tac = Robot.leftMotor.readPosition()
      count = count + 1
      Time.now() < startTime + milliseconds

    GyroDriveStraight.driveGyroFeedback(goalHeading,dutyCycle,notDoneYet)
    Log.log(s"$count loop closures in $milliseconds for ${milliseconds.toFloat/count} milliseconds per loop closure")
