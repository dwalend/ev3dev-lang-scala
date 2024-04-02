package ev3dev4s.timing

import ev3dev4s.sensors.gyroscope.examples.{GyroDriveStraight, Robot}
import ev3dev4s.os.Time
import ev3dev4s.Log
import ev3dev4s.measured.dimension.Dimensions.{degree, unitless, *}
import ev3dev4s.measured.dimension.{Uno, Angle}
import ev3dev4s.sensors.Ev3KeyPad

/**
 * A test of time to run a reasonable control loop for various comparisons
 * 
 * Baseline - before messing with unit-based numbers in controls
 * 1638416904531 13929 loop closures in 60000 for 4.30756 milliseconds per loop closure
 * 
 * With my own units for Degrees and Percent (duty cyle)
 * 1639715964387 14436 loop closures in 60000 for 4.1562757 milliseconds per loop closure
 * 
 * With my own universal trait for the arithmetic operations
 * 1640018240566 19308 loop closures in 60000 for 3.10752 milliseconds per loop closure
 *
 * With Floats instead of Ints underlying the units
 * 1645127290993 18331 loop closures in 60000 for 3.2731438 milliseconds per loop closure
 */ 
object GyroFeedbackLoop extends Runnable {
  def main(args: Array[String]): Unit =
    run()

  override def run(): Unit = {
    //    Robot.gyroscope.despin()
    Robot.headingMode.zero()
    driveGyroFeedbackTime(0 * degree, 0 * unitless, 20000)

    Robot.hold()
    driveGyroFeedbackTime(0 * degree, 0 * unitless, 60000)

    Robot.hold()
    Robot.hold()
    Log.log(s"holding motors - waiting for button")

    while (Robot.keypad.blockUntilAnyKey()._2 != Ev3KeyPad.State.Released) {
    }
  }


  /**
   * Gyro straight with the duty cycle
   *
   * @param goalHeading that the gyroscope should read during this traverse
   * @param dutyCycle   degrees per second to turn the motors
   * @param milliseconds  time to travel
   */
  def driveGyroFeedbackTime(
                             goalHeading: Angle,
                             dutyCycle: Uno,
                             milliseconds: Int
                           ): Unit = {
    val startTime = Time.now()

    var count = 0

    def notDoneYet(): Boolean = {
      val tac = Robot.leftMotor.readPosition()
      count = count + 1
      Time.now() < startTime + milliseconds
    }

    Log.log(s"Start timing study")
    GyroDriveStraight.driveGyroFeedback(goalHeading, dutyCycle, notDoneYet)
    Log.log(s"$count loop closures in $milliseconds for ${
      milliseconds.toFloat / count
    } milliseconds per loop closure")
  }
}
