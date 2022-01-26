package net.walend.lessons

import net.walend.cargoconnect.Robot
import ev3dev4s.measure.Degrees
import ev3dev4s.measure.DegreesPerSecond
import ev3dev4s.measure.Conversions.*

import ev3dev4s.Log

import ev3dev4s.actuators.Ev3Led
import scala.annotation.tailrec
import ev3dev4s.measure.MilliMeters
import ev3dev4s.sensors.Ev3ColorSensor
import ev3dev4s.measure.Percent

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
      val colorSteerAdjust = (blackSide.steerSign * (brightness - calibrationCenter).value / 3).degreesPerSecond //todo should also be proportional to goal speed
      val steerAdjust = colorSteerAdjust

      Log.log(s"steerAdjust $steerAdjust")

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

enum BlackSide(val steerSign:Int):
  case Left extends BlackSide(-1)
  case Right extends BlackSide(1) 

object TestLineDrive extends Runnable:
  val actions: Array[TtyMenuAction] = Array(
      MovesMenuAction("SetGyro0",Seq(GyroSetHeading(0.degrees))),
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

    val leftMotorText = UnpluggedException.safeString(() => s"${Robot.leftDriveMotor.readPosition()}")
    Lcd.set(2,leftMotorText,Lcd.LEFT)
    val rightMotorText = UnpluggedException.safeString(() => s"${Robot.rightDriveMotor.readPosition()}")
    Lcd.set(2,rightMotorText,Lcd.RIGHT)

  val lcdView:Controller = Controller(actions,setSensorRows)

  override def run():Unit = lcdView.run()

  def main(args: Array[String]): Unit = run()