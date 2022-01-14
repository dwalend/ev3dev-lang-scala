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
  val minSpeed = 10.degreesPerSecond

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

  def leftRotate(speed:DegreesPerSecond) = Robot.drive((speed/2.unitless).degreesPerSecond,(-(speed/2.unitless)).degreesPerSecond)
  def rightRotate(speed:DegreesPerSecond) = Robot.drive((-(speed/2.unitless)).degreesPerSecond,(speed/2.unitless).degreesPerSecond)

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
    val heading = Robot.gyroHeading.readHeading() //read sensors into a structure
    if(!keepGoing(heading)) () //return condition takes the sensor structure
    else
      val headingDiff = (heading - goalHeading).abs //proportional bit
      setLeds(headingDiff) //indicators - add to drive

      //proportional value
      val speed = if (headingDiff > fullSpeedThreshold) goalSpeed
                  else if (headingDiff > minSpeedThreshold) (goalSpeed * headingDiff / fullSpeedThreshold.value).degreesPerSecond 
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