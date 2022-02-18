package net.walend.lessons

import net.walend.cargoconnect.Robot
import ev3dev4s.measure.Degrees
import ev3dev4s.measure.DegreesPerSecond
import ev3dev4s.measure.Conversions.*

import ev3dev4s.Log

import ev3dev4s.actuators.{Ev3Led,Motor}
import scala.annotation.tailrec
import ev3dev4s.measure.MilliMeters

abstract class GyroArc() extends Move:
  
  @tailrec
  final def gyroArc(
             goalHeading: Degrees,
             goalOdo:MilliMeters,
             goalSpeed: DegreesPerSecond,
             radius:MilliMeters,
             outerMotor:Motor,
             keepGoing: () => Boolean,
             setLeds: () => Unit = Ev3Led.writeBothGreen
  ):Unit = 
    setLeds()
    if(!keepGoing()) ()
    else
      //figure out expected remaining distance
      val heading:Degrees = Robot.gyroHeading.readHeading()
      val remainingDegrees = goalHeading - heading
      //val expectedRemainingDistance = ((goalHeading - heading) * radius / 360).mm

      //figure out actual remaining distance
      val outerMotorDegrees = outerMotor.readPosition() //todo convert this to mm in Robot
      val remainingDistance = goalOdo - ((outerMotorDegrees * Robot.wheelCircumference)/360).mm 

      val expectedRemainingDegrees = ((remainingDistance.value * 360) / (radius.value * 2 * Math.PI.toFloat)).degrees

      //todo steer adjust 

      val steerAdjust = 
        if(remainingDegrees == expectedRemainingDegrees) 0.degreesPerSecond
        else 
          ((remainingDegrees - expectedRemainingDegrees).value * 6).degreesPerSecond //todo should also be proportional to goal speed
      val outerMotorSpeed = goalSpeed + steerAdjust
      val innerMotorSpeed = (((goalSpeed - steerAdjust).value * (radius - Robot.wheelToWheel).value)/radius.value).degreesPerSecond

      //todo maybe pass in the Robot.drive function and steer adjust ???
      //todo duty cycle instead
      val innerMotor = if(Robot.leftDriveMotor == outerMotor) Robot.drive(outerMotorSpeed,innerMotorSpeed)
                        else Robot.drive(innerMotorSpeed,outerMotorSpeed)
      gyroArc(goalHeading,goalOdo,goalSpeed,radius,outerMotor,keepGoing,setLeds)

case class GyroArcForwardRight(
            goalHeading: Degrees,
            radius: MilliMeters,
            goalSpeed: DegreesPerSecond,  //speed of the outer (left) wheel
            setLeds: () => Unit = Ev3Led.writeBothGreen 
          ) extends GyroArc:

  val outerMotor = Robot.leftDriveMotor
  def move():Unit = 
    val heading:Degrees = Robot.gyroHeading.readHeading()
    val remainingDegrees = goalHeading - heading
  
    val outerMotorDegrees = outerMotor.readPosition() //todo convert this to mm in Robot
    val initialOdo = ((outerMotorDegrees * Robot.wheelCircumference)/360).mm 
    val deltaOdo = (((radius.value * 2 * Math.PI.toFloat) * remainingDegrees.value) /360).mm
    val goalOdo = initialOdo + deltaOdo

//todo what to do for "not far enough" ? distance and/or angle? 
    def notFarEnough():Boolean =
      Robot.gyroHeading.readHeading() < goalHeading

    gyroArc(goalHeading,goalOdo,goalSpeed,radius,outerMotor,notFarEnough,setLeds)

object TestGyroArc extends Runnable:
  val actions: Array[TtyMenuAction] = Array(
      MovesMenuAction("SetGyro0",Seq(GyroSetHeading(0.degrees))),
      MovesMenuAction("ArcForward",Seq(GyroArcForwardRight(90.degrees,500.mm,Robot.fineSpeed),Robot.Hold)),
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