package net.walend.lessons

import net.walend.cargoconnect.Robot
import ev3dev4s.measure.Degrees
import ev3dev4s.measure.DegreesPerSecond
import ev3dev4s.measure.Conversions.*

import ev3dev4s.Log

import ev3dev4s.actuators.Ev3Led
import scala.annotation.tailrec
import ev3dev4s.measure.MilliMeters

abstract class GyroDrive extends Move:
  
  @tailrec
  final def gyroDriveStraight(
             goalHeading: Degrees,
             goalSpeed: DegreesPerSecond,
             keepGoing: () => Boolean,
             setLeds: () => Unit = Ev3Led.writeBothGreen
  ):Unit = 
    setLeds()
    if(!keepGoing()) ()
    else
      val heading:Degrees = Robot.gyroHeading.readHeading()
      val steerAdjust = 
        if(heading == goalHeading) 0.degreesPerSecond
        else 
          ((goalHeading - heading).value * 6).degreesPerSecond //todo should also be proportional to goal speed
      Log.log(s"gyroDriveStraight $goalHeading $heading $steerAdjust drive(${goalSpeed + steerAdjust},${goalSpeed - steerAdjust})")
      Robot.drive(goalSpeed + steerAdjust, goalSpeed - steerAdjust) //todo duty cycle instead?    
      gyroDriveStraight(goalHeading,goalSpeed,keepGoing,setLeds)

case class GyroDriveDistanceForward(
            goalHeading: Degrees,
            goalSpeed: DegreesPerSecond,
            distance: MilliMeters, 
            setLeds: () => Unit = Ev3Led.writeBothGreen 
          ) extends GyroDrive:

  def move():Unit = 
    val initialPosition = Robot.leftDriveMotor.readPosition()
    def notFarEnough():Boolean =
      Robot.leftDriveMotor.readPosition() < initialPosition + ((distance.value * 360)/Robot.wheelCircumference.value).degrees

    gyroDriveStraight(goalHeading,goalSpeed,notFarEnough)    

case class GyroDriveDistanceBackward(
            goalHeading: Degrees,
            goalSpeed: DegreesPerSecond,
            distance: MilliMeters, 
            setLeds: () => Unit = Ev3Led.writeBothGreen 
          ) extends GyroDrive:

  def move():Unit = 
    val initialPosition = Robot.leftDriveMotor.readPosition()
    def notBackEnough():Boolean =
      Log.log(s"notBackEnough ${Robot.leftDriveMotor.readPosition()} $initialPosition + ${((distance.value * 360)/Robot.wheelCircumference.value).degrees}")
      Robot.leftDriveMotor.readPosition() > initialPosition + ((distance.value * 360)/Robot.wheelCircumference.value).degrees

    gyroDriveStraight(goalHeading,goalSpeed,notBackEnough)   

case class GyroSetHeading(heading:Degrees) extends Move:
  def move():Unit = Robot.gyroscope.headingMode().setHeading(heading)  

object DespinGyro extends Move:
  def move(): Unit =
    Robot.gyroscope.despin()

object TestGyroDrive:
  val actions: Array[TtyMenuAction] = Array(
      MovesMenuAction("SetGyro0",Seq(GyroSetHeading(0.degrees))),
      MovesMenuAction("GyroForward0",Seq(GyroDriveDistanceForward(0.degrees,Robot.fineSpeed,500.mm),Robot.Hold)),
      MovesMenuAction("GyroBack0",Seq(GyroDriveDistanceBackward(0.degrees,-Robot.fineSpeed,-500.mm),Robot.Hold)),
      MovesMenuAction("SetGyro90",Seq(GyroSetHeading(90.degrees))),
      MovesMenuAction("GyroForward90",Seq(GyroDriveDistanceForward(90.degrees,Robot.fineSpeed,500.mm),Robot.Hold)),
      MovesMenuAction("GyroBack90",Seq(GyroDriveDistanceBackward(90.degrees,-Robot.fineSpeed,-500.mm),Robot.Hold)),
      MovesMenuAction("Coast",Seq(Robot.Coast)),
      MovesMenuAction("Despin",Seq(DespinGyro))
    )

  def setSensorRows():Unit =
    import ev3dev4s.lcd.tty.Lcd
    import ev3dev4s.sysfs.UnpluggedException

    Lcd.set(0,s"${lcdView.elapsedTime}s",Lcd.RIGHT)
    val heading:String = UnpluggedException.safeString(() => s"${Robot.gyroscope.headingMode().readHeading().value}")
    Lcd.set(0,heading,Lcd.LEFT)

    val leftMotorText = UnpluggedException.safeString(() => s"${Robot.leftDriveMotor.readPosition().value}")
    Lcd.set(1,leftMotorText,Lcd.LEFT)
    val rightMotorText = UnpluggedException.safeString(() => s"${Robot.rightDriveMotor.readPosition().value}")
    Lcd.set(1,rightMotorText,Lcd.RIGHT)

  val lcdView:Controller = Controller(actions,setSensorRows)

  def main(args: Array[String]): Unit =
    lcdView.run()