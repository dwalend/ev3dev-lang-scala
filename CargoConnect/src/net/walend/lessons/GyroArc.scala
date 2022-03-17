package net.walend.lessons

import net.walend.cargoconnect.Robot
import ev3dev4s.measure.Degrees
import ev3dev4s.measure.DegreesPerSecond
import ev3dev4s.measure.Conversions.*
import ev3dev4s.Log
import ev3dev4s.actuators.{Ev3LargeMotor, Ev3Led, Motor}
import ev3dev4s.measure.MilliMeters
import scala.annotation.tailrec

abstract class GyroArc() extends Move:
  
  @tailrec
  final def gyroArc(
             goalHeading: Degrees,
             goalOdo:MilliMeters,
             goalSpeed: DegreesPerSecond,
             radius:MilliMeters,
             arcSpecifier: ArcSpecifier,
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
      val outerMotorPosition = arcSpecifier.outerMotor.readPosition() //todo convert this to mm in Robot
      val remainingDistance = goalOdo - ((outerMotorPosition * Robot.wheelCircumference)/360).mm

      val expectedRemainingDegrees = ((arcSpecifier.turnSign * remainingDistance.value * 360) /
                                      (radius.value * 2 * Math.PI.toFloat))
                                      .degrees
      Log.log(s"remainingDegrees $remainingDegrees remainingDistance $remainingDistance expectedRemainingDegrees $expectedRemainingDegrees")

      //todo steer adjust 

      val steerAdjust = ((remainingDegrees - expectedRemainingDegrees).value * 6 * arcSpecifier.turnSign).degreesPerSecond //todo should also be proportional to goal speed
      val outerMotorSpeed: DegreesPerSecond = goalSpeed + steerAdjust
      val innerMotorSpeed = (((goalSpeed - steerAdjust).value * (radius - Robot.wheelToWheel).value)/radius.value).degreesPerSecond
      Log.log(s"gaf steerAdjust is $steerAdjust")

      //todo maybe pass in the Robot.drive function and steer adjust ???
      //todo duty cycle instead
      if(Robot.leftDriveMotor == arcSpecifier.outerMotor) Robot.drive(outerMotorSpeed,innerMotorSpeed)
      else Robot.drive(innerMotorSpeed,outerMotorSpeed)
      Thread.`yield`()
      gyroArc(goalHeading,goalOdo,goalSpeed,radius,arcSpecifier,keepGoing,setLeds)

  def driveArc(
            goalHeading: Degrees,
            goalSpeed: DegreesPerSecond,
            radius:MilliMeters,
            arcSpecifier:ArcSpecifier,
            keepGoing: () => Boolean,
            setLeds: () => Unit = Ev3Led.writeBothGreen
          ): Unit =
    val heading:Degrees = Robot.gyroHeading.readHeading()
    val remainingDegrees = goalHeading - heading

    val outerMotorDegrees = arcSpecifier.outerMotor.readPosition() //todo convert this to mm in Robot
    val initialOdo = ((outerMotorDegrees * Robot.wheelCircumference)/360).mm
    val deltaOdo = (goalSpeed.value.sign * (((radius.value * 2 * Math.PI.toFloat) * remainingDegrees.value.abs) /360)).mm
    val goalOdo = initialOdo + deltaOdo

    Log.log(s"daf remainingDegrees $remainingDegrees goalOdo $goalOdo")
    gyroArc(goalHeading,goalOdo,goalSpeed,radius,arcSpecifier,keepGoing,setLeds)

  def notRightEnough(goalHeading:Degrees):Boolean = Robot.gyroHeading.readHeading() < goalHeading

  def notLeftEnough(goalHeading:Degrees):Boolean = Robot.gyroHeading.readHeading() > goalHeading

  sealed trait ArcSpecifier:
    def outerMotor:Motor
    def turnSign:Int

  object ForwardRight extends ArcSpecifier:
    override val outerMotor:Motor = Robot.leftDriveMotor
    override val turnSign: Int = 1

  object ForwardLeft extends ArcSpecifier:
    override val outerMotor:Motor = Robot.rightDriveMotor
    override val turnSign: Int = -1

  object BackwardRight extends ArcSpecifier:
    override val outerMotor:Motor = Robot.rightDriveMotor
    override val turnSign: Int = -1

  object BackwardLeft extends ArcSpecifier:
    override val outerMotor:Motor = Robot.leftDriveMotor
    override val turnSign: Int = 1


case class GyroArcForwardRight(
            goalHeading: Degrees,
            radius: MilliMeters,
            goalSpeed: DegreesPerSecond,  //speed of the outer (left) wheel
            setLeds: () => Unit = Ev3Led.writeBothGreen
          ) extends GyroArc:

  val outerMotor: Ev3LargeMotor = Robot.leftDriveMotor

  def move():Unit =
//todo what to do for "not far enough" ? distance and/or angle?
    def notFarEnough():Boolean = notRightEnough(goalHeading)

    driveArc(goalHeading,goalSpeed,radius,ForwardRight,notFarEnough,setLeds)

case class GyroArcForwardLeft(
                                goalHeading: Degrees,
                                radius: MilliMeters,
                                goalSpeed: DegreesPerSecond,  //speed of the outer (left) wheel
                                setLeds: () => Unit = Ev3Led.writeBothGreen
                              ) extends GyroArc:

  val outerMotor: Ev3LargeMotor = Robot.rightDriveMotor
  def move():Unit =
    //todo what to do for "not far enough" ? distance and/or angle?
    def notFarEnough():Boolean = notLeftEnough(goalHeading)

    driveArc(goalHeading,goalSpeed,radius,ForwardLeft,notFarEnough,setLeds)

case class GyroArcBackwardRight(
                                goalHeading: Degrees,
                                radius: MilliMeters,
                                goalSpeed: DegreesPerSecond,  //speed of the outer (left) wheel
                                setLeds: () => Unit = Ev3Led.writeBothGreen
                              ) extends GyroArc:

  val outerMotor: Ev3LargeMotor = Robot.rightDriveMotor

  def move():Unit =
    //todo what to do for "not far enough" ? distance and/or angle?
    def notFarEnough():Boolean = notRightEnough(goalHeading)

    driveArc(goalHeading,goalSpeed,radius,BackwardRight,notFarEnough,setLeds)

case class GyroArcBackwardLeft(
                               goalHeading: Degrees,
                               radius: MilliMeters,
                               goalSpeed: DegreesPerSecond,  //speed of the outer (left) wheel
                               setLeds: () => Unit = Ev3Led.writeBothGreen
                             ) extends GyroArc:

  val outerMotor: Ev3LargeMotor = Robot.leftDriveMotor
  def move():Unit =
    //todo what to do for "not far enough" ? distance and/or angle?
    def notFarEnough():Boolean = notLeftEnough(goalHeading)

    driveArc(goalHeading,goalSpeed,radius,BackwardLeft,notFarEnough,setLeds)

object TestGyroArc extends Runnable:
  val actions: Array[TtyMenuAction] = Array(
    MovesMenuAction("SetGyro-45",GyroSetHeading(-45.degrees)),
    MovesMenuAction("ArcFR45",Seq(GyroArcForwardRight(0.degrees,100.mm+Robot.wheelToWheel,Robot.fineSpeed),Robot.Hold)),
    MovesMenuAction("SetGyro0",Seq(GyroSetHeading(0.degrees))),
    MovesMenuAction("ArcFR",Seq(GyroArcForwardRight(90.degrees,500.mm,Robot.fineSpeed),Robot.Hold)),
    MovesMenuAction("ArcFL",Seq(GyroArcForwardLeft(-90.degrees,500.mm,Robot.fineSpeed),Robot.Hold)),
    MovesMenuAction("ArcBL",Seq(GyroArcBackwardLeft(-90.degrees,500.mm,-Robot.fineSpeed),Robot.Hold)),
    MovesMenuAction("ArcBR",Seq(GyroArcBackwardRight(90.degrees,500.mm,-Robot.fineSpeed),Robot.Hold)),
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