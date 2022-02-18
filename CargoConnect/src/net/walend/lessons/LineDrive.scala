package net.walend.lessons

import net.walend.cargoconnect.Robot
import ev3dev4s.measure.{Degrees, DegreesPerSecond, MilliMeters, Percent}
import ev3dev4s.measure.Conversions.*
import ev3dev4s.Log
import ev3dev4s.actuators.Ev3Led
import ev3dev4s.sensors.Ev3ColorSensor
import scala.annotation.tailrec

/* todo
* Gyro-assisted line drive straight
* Refactor - read sensors, maybe stop, drive motors, repeat - maybe as a trait. 
* Refactor to have a common HoF
* Refactor the gyro feedback
* Gyro-assisted line drive straight backwards

*/

/**
 * Use the gyroscope and a light sensor to follow a staight line
 */
abstract class LineDrive extends Move:
  
  @tailrec
  final def lineDriveStraight(
             colorSensor:Ev3ColorSensor,
             blackSide:BlackSide, //todo use this
             goalSpeed: DegreesPerSecond,
             keepGoing: () => Boolean,
             setLeds: () => Unit = Ev3Led.writeBothGreen
  ):Unit = 
    setLeds()
    if(!keepGoing()) ()
    else
      val brightness:Percent = colorSensor.reflectMode().readReflect()
      val calibrationCenter:Percent = 50.percent //todo calibrate the color sensors and get the value from Robot - which means bring it in with the color sensor
      //todo changanble value for how much to steer - currently 1/3
      val colorSteerAdjust = (blackSide.steerSign * (brightness - calibrationCenter).value / 3).degreesPerSecond //todo should also be proportional to goal speed
      val steerAdjust = colorSteerAdjust

//      Log.log(s"steerAdjust $steerAdjust")

      Robot.drive(goalSpeed + steerAdjust, goalSpeed - steerAdjust) //todo duty cycle instead? 
      Thread.`yield`()   
      lineDriveStraight(colorSensor,blackSide,goalSpeed,keepGoing,setLeds)

case class LineDriveDistanceForward(
            colorSensor:Ev3ColorSensor,
            blackSide:BlackSide,
            goalSpeed: DegreesPerSecond,
            distance: MilliMeters, 
            setLeds: () => Unit = Ev3Led.writeBothGreen 
          ) extends LineDrive:

  def move():Unit = 
    val initialPosition = Robot.leftDriveMotor.readPosition()
    def notFarEnough():Boolean =
      Robot.leftDriveMotor.readPosition() < initialPosition + ((distance.value * 360)/Robot.wheelCircumference.value).degrees

    lineDriveStraight(colorSensor,blackSide,goalSpeed,notFarEnough)    

case class AquireLine(
  goalHeading:Degrees,
  colorSensor:Ev3ColorSensor,
  blackSide:BlackSide,
  goalSpeed: DegreesPerSecond

) extends Move:

  def move():Unit = 
    //Find white
    //Find black while adjusting heading
    //Settle in with high constant
    //Use low constant
    ???

enum BlackSide(val steerSign:Int):
  case Left extends BlackSide(-1)
  case Right extends BlackSide(1) 

object CalibrateReflect extends Move:
  case class CalibratedReflect(darkest:Percent,brightest:Percent):
    val darkFuzz = 15.percent
    val brightFuzz = 15.percent
    lazy val middle = ((darkest.value + brightest.value)/2).percent
    def dark(sensed:Percent):Boolean = sensed < darkest + darkFuzz
    def bright(sensed:Percent):Boolean = sensed > brightest - brightFuzz
    def between(sensed:Percent):Boolean = !dark(sensed) && !bright(sensed)


  val bestGuess = CalibratedReflect(10.percent,90.percent)

  @volatile var leftCalibrated = bestGuess
  @volatile var rightCalibrated = bestGuess

  def move():Unit =
    //Move forward 300mm while crossing a white and black line
    val thread = new Thread:
      override def run(): Unit =
        GyroSetHeading(0.degrees).move()
        GyroDriveDistanceForward(0.degrees,100.degreesPerSecond,300.mm).move()
        Robot.Coast.move()
    thread.run()

    @tailrec
    def findMinAndMax(thread:Thread,bestVals:(Percent,Percent,Percent,Percent)):(Percent,Percent,Percent,Percent) =
      if(!thread.isAlive) bestVals
      else
        Thread.`yield`()
        val left = Robot.leftColorSensor.reflectMode().readReflect()
        val right = Robot.rightColorSensor.reflectMode().readReflect()
        import ev3dev4s.measure.Measured.{min,max}
        val newBestVals = (min(bestVals._1,left),max(bestVals._2,left),min(bestVals._3,right),max(bestVals._4,right))

        findMinAndMax(thread,newBestVals)


    val finalVals = findMinAndMax(thread,(100.percent,0.percent,100.percent,0.percent))

    leftCalibrated = CalibratedReflect(finalVals._1,finalVals._2)
    rightCalibrated = CalibratedReflect(finalVals._3,finalVals._4)


object TestLineDrive extends Runnable:
  val actions: Array[TtyMenuAction] = Array(
      MovesMenuAction("SetGyro0",Seq(GyroSetHeading(0.degrees))),
      MovesMenuAction("ColorCalibrate",Seq(CalibrateReflect)),
      MovesMenuAction("LineForward0",Seq(LineDriveDistanceForward(Robot.rightColorSensor,BlackSide.Right,Robot.fineSpeed,500.mm),Robot.Hold)),
      MovesMenuAction("Coast",Seq(Robot.Coast)),
      MovesMenuAction("Despin",Seq(DespinGyro))
    )

  def setSensorRows():Unit =
    import ev3dev4s.lcd.tty.Lcd
    import ev3dev4s.sysfs.UnpluggedException

    Lcd.set(0,s"${lcdView.elapsedTime}s",Lcd.RIGHT)
    val heading:String = UnpluggedException.safeString(() => s"${Robot.gyroscope.headingMode().readHeading()}")
    Lcd.set(0,heading,Lcd.LEFT)

    val leftColor:String = UnpluggedException.safeString(() => s"${Robot.leftColorSensor.reflectMode().readReflect()}")
    Lcd.set(1,leftColor,Lcd.LEFT)
    val rightColor:String = UnpluggedException.safeString(() => s"${Robot.rightColorSensor.reflectMode().readReflect()}")
    Lcd.set(1,rightColor,Lcd.RIGHT)

    Lcd.set(2,s"${CalibrateReflect.leftCalibrated.darkest}/${CalibrateReflect.leftCalibrated.brightest}",Lcd.LEFT)
    Lcd.set(2,s"${CalibrateReflect.rightCalibrated.darkest}/${CalibrateReflect.rightCalibrated.brightest}",Lcd.RIGHT)

  //    val leftMotorText = UnpluggedException.safeString(() => s"${Robot.leftDriveMotor.readPosition()}")
//    Lcd.set(2,leftMotorText,Lcd.LEFT)
//    val rightMotorText = UnpluggedException.safeString(() => s"${Robot.rightDriveMotor.readPosition()}")
//    Lcd.set(2,rightMotorText,Lcd.RIGHT)

  val lcdView:Controller = Controller(actions,setSensorRows)

  override def run():Unit = lcdView.run()

  def main(args: Array[String]): Unit = run()