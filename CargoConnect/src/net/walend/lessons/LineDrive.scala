package net.walend.lessons

import net.walend.cargoconnect.Robot
import ev3dev4s.measure.{Degrees, DegreesPerSecond, MilliMeters, Percent}
import ev3dev4s.measure.Conversions._
import ev3dev4s.Log
import ev3dev4s.actuators.{Ev3Led, Motor}
import ev3dev4s.sensors.Ev3ColorSensor
import net.walend.lessons.GyroDrive.driveAdjust
import scala.annotation.tailrec
import scala.collection.mutable.{Map => MutableMap}


/**
 * Use the gyroscope and a light sensor to follow a straight line
 */
object LineDrive {

  private def driveAdjust(goalHeading: Degrees, goalSpeed: DegreesPerSecond, blackOn: BlackSide, colorSensor: Ev3ColorSensor)(initial: GyroAndTrackColorReading)(sensorResults: GyroAndTrackColorReading): Unit = {

    val calibrationCenter = CalibrateReflect.colorSensorsToCalibrated(colorSensor).middle

    val gyroSteerAdjust = ((goalHeading - sensorResults.heading).value * goalSpeed.abs.value / 30).degreesPerSecond
    val colorSteerAdjust = (blackOn.steerSign * (sensorResults.trackIntensity - calibrationCenter).value * goalSpeed.abs.value / 300).degreesPerSecond //todo 600 seems really high

    val steerAdjust = gyroSteerAdjust + colorSteerAdjust
    Robot.directDrive(goalSpeed + steerAdjust, goalSpeed - steerAdjust)
  }

  def driveForwardUntilDistance(
                                 goalHeading: Degrees,
                                 trackColorSensor: Ev3ColorSensor,
                                 blackOn: BlackSide,
                                 goalSpeed: DegreesPerSecond,
                                 distance: MilliMeters,
                                 tachometer: Motor = Robot.leftDriveMotor
                               ): Move =
    FeedbackMove(
      name = s"LineF $blackOn $distance",
      sense = GyroColorTachometer.sense(trackColorSensor, tachometer),
      complete = GyroDrive.forwardUntilDistance(distance),
      drive = driveAdjust(goalHeading, goalSpeed, blackOn, trackColorSensor),
      start = GyroDrive.start(goalSpeed),
      end = GyroDrive.end
    )

  def driveForwardUntilBlack(
                              goalHeading: Degrees,
                              trackColorSensor: Ev3ColorSensor,
                              blackOn: BlackSide,
                              goalSpeed: DegreesPerSecond,
                              distance: MilliMeters,
                              tachometer: Motor = Robot.leftDriveMotor,
                              tripColorSensor: Ev3ColorSensor = Robot.leftColorSensor
                            ): Move =
    FeedbackMove(
      name = s"LineF to $blackOn $distance or black",
      sense = GyroColorTrackingTripTachometerReading.sense(trackColorSensor, tripColorSensor, tachometer),
      complete = GyroColorTrackingTripTachometerReading.complete(distance, CalibrateReflect.colorSensorsToCalibrated(tripColorSensor).dark),
      drive = driveAdjust(goalHeading, goalSpeed, blackOn, trackColorSensor),
      start = GyroDrive.start(goalSpeed),
      end = GyroDrive.end
    )
}

trait TrackColorReading {
  def trackIntensity: Percent
}

trait GyroAndTrackColorReading extends GyroHeading with TrackColorReading with TachometerAngle

final case class GyroColorTachometerReading(heading:Degrees, trackIntensity:Percent, tachometerAngle:Degrees)
  extends GyroAndTrackColorReading

object GyroColorTachometer {

  def sense(colorSensor: Ev3ColorSensor, tachometer: Motor)(): GyroColorTachometerReading =
    GyroColorTachometerReading(
      Robot.gyroscope.headingMode().readHeading(),
      colorSensor.reflectMode().readReflect(),
      tachometer.readPosition()
    )
}

sealed case class BlackSide(steerSign:Int)

object BlackSide {
  val Left = BlackSide(-1)
  val Right = BlackSide(1)
}

trait TripColorReading {
  def tripIntensity: Percent
}

final case class GyroColorTrackingTripTachometerReading(heading:Degrees,trackIntensity:Percent,tripIntensity:Percent,tachometerAngle: Degrees)
  extends GyroAndTrackColorReading with TripColorReading

object GyroColorTrackingTripTachometerReading {
  def sense(trackSensor: Ev3ColorSensor, tripSensor: Ev3ColorSensor, tachometer: Motor)(): GyroColorTrackingTripTachometerReading =
    GyroColorTrackingTripTachometerReading(
      Robot.gyroscope.headingMode().readHeading(),
      trackSensor.reflectMode().readReflect(),
      tripSensor.reflectMode().readReflect(),
      tachometer.readPosition()
    )

  def complete(distance: MilliMeters, trip: Percent => Boolean)
              (initialSense: GyroColorTrackingTripTachometerReading)
              (sensorResults: GyroColorTrackingTripTachometerReading): Boolean = {
    GyroDrive.forwardUntilDistance(distance)(initialSense)(sensorResults) || trip(sensorResults.tripIntensity)
  }
}

object WhiteBlackWhite {

  def createComplete(tripColorSensor: Ev3ColorSensor, limitDistance: MilliMeters)(initialSense: GyroColorTachometerReading): GyroColorTachometerReading => Boolean = {

    import net.walend.lessons.CalibrateReflect.CalibratedReflect

    sealed case class State()

    object State {
      val ExpectFirstWhite = State()
      val ExpectBlack = State()
      val ExpectSecondWhite = State()
    }

    var state: State = State.ExpectFirstWhite

    def setState(s: State): Unit = {
      state = s
      state match {
        case State.ExpectFirstWhite => Ev3Led.Left.writeGreen()
        case State.ExpectBlack => Ev3Led.Left.writeYellow()
        case State.ExpectSecondWhite => Ev3Led.Left.writeRed()
      }
    }

    setState(State.ExpectFirstWhite)

    def distanceCheck: TachometerAngle => Boolean = GyroDrive.forwardUntilDistance(limitDistance)(initialSense) _

    val calibrateReflect: CalibratedReflect = CalibrateReflect.colorSensorsToCalibrated(tripColorSensor)

    def tripWhiteBlackWhite(sensorResults: GyroColorTachometerReading): Boolean = {
      val seenWBW: Boolean = state match {
        case State.ExpectFirstWhite =>
          if (calibrateReflect.bright(sensorResults.trackIntensity)) setState(State.ExpectBlack)
          false
        case State.ExpectBlack =>
          if (calibrateReflect.dark(sensorResults.trackIntensity)) setState(State.ExpectSecondWhite)
          false
        case State.ExpectSecondWhite =>
          if (calibrateReflect.bright(sensorResults.trackIntensity)) true
          else false
      }
      seenWBW || distanceCheck(sensorResults)
    }

    tripWhiteBlackWhite
  }

  def driveForwardToWhiteBlackWhite(
                                     goalHeading: Degrees,
                                     goalSpeed: DegreesPerSecond,
                                     limitDistance: MilliMeters,
                                     tachometer: Motor = Robot.leftDriveMotor,
                                     tripColorSensor: Ev3ColorSensor = Robot.leftColorSensor
                                   ): Move =
    FeedbackMove(
      name = s"Forward to White-Black-White",
      sense = GyroColorTachometer.sense(tripColorSensor, tachometer),
      complete = createComplete(tripColorSensor, limitDistance),
      drive = GyroDrive.driveAdjust(goalHeading, goalSpeed),
      start = GyroDrive.start(goalSpeed),
      end = GyroDrive.end
    )
}

case class AcquireLine(
  goalHeading:Degrees,
  colorSensor:Ev3ColorSensor,
  blackSide:BlackSide,
  goalSpeed: DegreesPerSecond

) extends Move {

  def move(): Unit = {
    //Find white
    //Find black while adjusting heading
    //Settle in with high constant
    //Use low constant
    ???
  }
}

object CalibrateReflect extends Move {
  case class CalibratedReflect(darkest: Percent, brightest: Percent) {
    val darkFuzz = 15.percent
    val brightFuzz = 15.percent
    lazy val middle = ((darkest.value + brightest.value) / 2).percent

    def dark(sensed: Percent): Boolean = sensed < darkest + darkFuzz

    def bright(sensed: Percent): Boolean = sensed > brightest - brightFuzz

    def between(sensed: Percent): Boolean = !dark(sensed) && !bright(sensed)
  }

  val bestGuess = CalibratedReflect(10.percent, 90.percent)

  val colorSensorsToCalibrated: MutableMap[Ev3ColorSensor, CalibratedReflect] = MutableMap.empty[Ev3ColorSensor, CalibratedReflect].withDefault(_ => bestGuess)

  def move(): Unit = {
    //Move forward 300mm while crossing a white and black line
    val thread = new Thread {
      override def run(): Unit = {
        GyroSetHeading(0.degrees).move()
        GyroDrive.driveForwardDistance(0.degrees, 100.degreesPerSecond, 300.mm).move()
        Robot.Coast.move()
      }
    }
    thread.start()

    @tailrec
    def findMinAndMax(thread: Thread, bestVals: (Percent, Percent, Percent, Percent)): (Percent, Percent, Percent, Percent) = {
      if (!thread.isAlive) bestVals
      else {
        Thread.`yield`()
        val left = Robot.leftColorSensor.reflectMode().readReflect()
        val right = Robot.rightColorSensor.reflectMode().readReflect()
        Log.log(s"left $left right $right")

        import ev3dev4s.measure.Measured.{min, max}

        val newBestVals = (min(bestVals._1, left), max(bestVals._2, left), min(bestVals._3, right), max(bestVals._4, right))
        Log.log(s"newBestVals $newBestVals")

        findMinAndMax(thread, newBestVals)
      }
    }

    val finalVals = findMinAndMax(thread, (100.percent, 0.percent, 100.percent, 0.percent))

    colorSensorsToCalibrated.put(Robot.leftColorSensor, CalibratedReflect(finalVals._1, finalVals._2))
    colorSensorsToCalibrated.put(Robot.rightColorSensor, CalibratedReflect(finalVals._3, finalVals._4))
    Log.log(s"Calibration results: $colorSensorsToCalibrated")
  }
}