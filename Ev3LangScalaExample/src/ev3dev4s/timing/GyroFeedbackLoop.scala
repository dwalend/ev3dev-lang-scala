package ev3dev4s.timing

import ev3dev4s.sensors.gyroscope.examples.{Robot,GyroDriveStraight}
import ev3dev4s.os.Time
import ev3dev4s.Log

import ev3dev4s.measure.Lego.{Degrees,Percents}

import coulomb.CoulombExtendWithUnits
import spire.std.int._
import spire.std.any._ 
import coulomb.Quantity


import ev3dev4s.sensors.Ev3KeyPad
import coulomb.accepted.Degree
import coulomb.accepted.Percent

/**
 * A test of time to run a reasonable control loop for various comparisons
 * 
 * First test - before messing with unit-based numbers in controls
 * 
 * 1638416904531 13929 loop closures in 60000 for 4.30756 milliseconds per loop closure
 */ 
object GyroFeedbackLoop extends Runnable:
  def main(args: Array[String]): Unit =
    run()

  override def run(): Unit =
//    Robot.gyroscope.despin()
    Robot.headingMode.zero()
    driveGyroFeedbackTime(0.withUnit[Degree],0.withUnit[Percent],20000)

    Robot.hold()
    driveGyroFeedbackTime(0.withUnit[Degree],0.withUnit[Percent],60000)

    Robot.hold()
    Robot.hold()
    Log.log(s"holding motors - waiting for button")

    while(Robot.keypad.blockUntilAnyKey()._2 != Ev3KeyPad.State.Released) {}


  /**
   * Gyro straight with the duty cycle
   *
   * @param goalHeading that the gyroscope should read during this traverse
   * @param dutyCycle degrees per second to turn the motors
   * @param distanceMm distance to travel
   */
  def driveGyroFeedbackTime(
                                 goalHeading: Degrees,
                                 dutyCycle: Percents,
                                 milliseconds: Int
                           ): Unit =
    val startTime = Time.now()

    var count = 0
    def notDoneYet():Boolean =
      val tac = Robot.leftMotor.readPosition()
      count = count + 1
      Time.now() < startTime + milliseconds

    Log.log(s"Start timing study")  
    GyroDriveStraight.driveGyroFeedback(goalHeading,dutyCycle,notDoneYet)
    Log.log(s"$count loop closures in $milliseconds for ${milliseconds.toFloat/count} milliseconds per loop closure")
