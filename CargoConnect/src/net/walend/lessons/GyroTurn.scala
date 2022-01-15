package net.walend.lessons

import net.walend.cargoconnect.Robot
import ev3dev4s.measure.Degrees
import ev3dev4s.measure.DegreesPerSecond
import ev3dev4s.measure.Conversions.*

import ev3dev4s.Log

import ev3dev4s.actuators.Ev3Led
import scala.annotation.tailrec
import ev3dev4s.measure.MilliMeters

object GyroTurn:
 
  val fullSpeedThreshold = 45.degrees
  val minSpeedThreshold = 5.degrees
  val minSpeed = 50.degreesPerSecond

  def showLedsForPivot(changing:Ev3Led,fixed:Ev3Led)(headingDiff:Degrees):Unit = 
    if (headingDiff > fullSpeedThreshold) changing.writeGreen()
    else if (headingDiff > minSpeedThreshold) changing.writeYellow()
    else changing.writeBrightness(255.ledIntensity,127.ledIntensity)
    fixed.writeRed()  
  
  def keepTurningRight(headingDiff:Degrees) = headingDiff > 0.degrees
  def keepTurningLeft(headingDiff:Degrees) = headingDiff < 0.degrees
  
  def leftWheelPivot(speed:DegreesPerSecond) = Robot.drive(speed,0.degreesPerSecond)
  def rightWheelPivot(speed:DegreesPerSecond) = Robot.drive(0.degreesPerSecond,speed)

  def showLedsForRotate(headingDiff:Degrees):Unit = 
    if (headingDiff > fullSpeedThreshold) 
      Ev3Led.Left.writeGreen()
      Ev3Led.Right.writeGreen()
    else if (headingDiff > minSpeedThreshold) 
      Ev3Led.Left.writeYellow()
      Ev3Led.Right.writeYellow()
    else 
      Ev3Led.Left.writeBrightness(255.ledIntensity,127.ledIntensity)  
      Ev3Led.Right.writeBrightness(255.ledIntensity,127.ledIntensity)  

  def leftRotate(speed:DegreesPerSecond) = Robot.drive((-speed/2.unitless).degreesPerSecond,(speed/2.unitless).degreesPerSecond)
  def rightRotate(speed:DegreesPerSecond) = Robot.drive((speed/2.unitless).degreesPerSecond,(-speed/2.unitless).degreesPerSecond)

abstract class GyroTurn(
    goalHeading: Degrees,
    goalSpeed: DegreesPerSecond,
    keepGoing: (Degrees) => Boolean,
    driveWheels: (DegreesPerSecond) => Unit,
    setLeds: (Degrees) => Unit
  ) extends Move:

  def move():Unit = gyroTurnRecursive()

  //todo extract this into a control structure
  @tailrec
  final def gyroTurnRecursive():Unit = 
    import GyroTurn.*
    val headingDiff = goalHeading - Robot.gyroHeading.readHeading()  //read sensors into a structure
    if(!keepGoing(headingDiff)) () //return condition takes the sensor structure
    else
      val absHeadingDiff = headingDiff.abs //proportional bit
      setLeds(absHeadingDiff) //indicators - add to drive

      //proportional value
      val speed = if (absHeadingDiff > fullSpeedThreshold) goalSpeed
                  else if (absHeadingDiff > minSpeedThreshold) (goalSpeed * absHeadingDiff / fullSpeedThreshold.value).degreesPerSecond 
                  else (minSpeed * goalSpeed.sign).degreesPerSecond
      driveWheels(speed)  
      gyroTurnRecursive() //recur

case class LeftForwardPivot(goalHeading:Degrees,goalSpeed:DegreesPerSecond) 
  extends GyroTurn(
    goalHeading = goalHeading,
    goalSpeed = goalSpeed,
    keepGoing = GyroTurn.keepTurningLeft,
    driveWheels = GyroTurn.rightWheelPivot,
    setLeds = GyroTurn.showLedsForPivot(Ev3Led.Right,Ev3Led.Left)
  )
  
case class RightForwardPivot(goalHeading:Degrees,goalSpeed:DegreesPerSecond) 
  extends GyroTurn(
    goalHeading = goalHeading,
    goalSpeed = goalSpeed,
    keepGoing = GyroTurn.keepTurningRight,
    driveWheels = GyroTurn.leftWheelPivot,
    setLeds = GyroTurn.showLedsForPivot(Ev3Led.Left,Ev3Led.Right)
  )

case class LeftBackwardPivot(goalHeading:Degrees,goalSpeed:DegreesPerSecond) 
  extends GyroTurn(
    goalHeading = goalHeading,
    goalSpeed = goalSpeed,
    keepGoing = GyroTurn.keepTurningLeft,
    driveWheels = GyroTurn.leftWheelPivot,
    setLeds = GyroTurn.showLedsForPivot(Ev3Led.Left,Ev3Led.Right)
  )
  
case class RightBackwardPivot(goalHeading:Degrees,goalSpeed:DegreesPerSecond) 
  extends GyroTurn(
    goalHeading = goalHeading,
    goalSpeed = goalSpeed,
    keepGoing = GyroTurn.keepTurningRight,
    driveWheels = GyroTurn.rightWheelPivot,
    setLeds = GyroTurn.showLedsForPivot(Ev3Led.Right,Ev3Led.Left)
  )

case class LeftRotate(goalHeading:Degrees,goalSpeed:DegreesPerSecond) 
  extends GyroTurn(
    goalHeading = goalHeading,
    goalSpeed = goalSpeed,
    keepGoing = GyroTurn.keepTurningLeft,
    driveWheels = GyroTurn.leftRotate,
    setLeds = GyroTurn.showLedsForRotate
  )
  
case class RightRotate(goalHeading:Degrees,goalSpeed:DegreesPerSecond) 
  extends GyroTurn(
    goalHeading = goalHeading,
    goalSpeed = goalSpeed,
    keepGoing = GyroTurn.keepTurningRight,
    driveWheels = GyroTurn.rightRotate,
    setLeds = GyroTurn.showLedsForRotate
  )

object TestGyroTurn extends Runnable:
  val actions: Array[TtyMenuAction] = Array(
      MovesMenuAction("SetGyro0",Seq(GyroSetHeading(0.degrees))),
      MovesMenuAction("LeftForward",Seq(LeftForwardPivot(-90.degrees,Robot.fineSpeed),Robot.Hold)),
      MovesMenuAction("RightForward",Seq(RightForwardPivot(90.degrees,Robot.fineSpeed),Robot.Hold)),
      MovesMenuAction("LeftRotate",Seq(LeftRotate(-90.degrees,Robot.fineSpeed),Robot.Hold)),
      MovesMenuAction("RightRotate",Seq(RightRotate(90.degrees,Robot.fineSpeed),Robot.Hold)),
      MovesMenuAction("LeftBackward",Seq(LeftBackwardPivot(-90.degrees,-Robot.fineSpeed),Robot.Hold)),
      MovesMenuAction("RightBackward",Seq(RightBackwardPivot(90.degrees,-Robot.fineSpeed),Robot.Hold)),
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

  override def run():Unit = lcdView.run()

  def main(args: Array[String]): Unit = run()