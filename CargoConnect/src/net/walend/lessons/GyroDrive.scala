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

object DespinGyro extends TtyMenuAction:
  override def act(menu: TtyMenu): Unit =
    Robot.gyroscope.despin()