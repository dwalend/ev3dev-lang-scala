package net.walend.cargoconnect

import net.walend.lessons.{BlackSide, Controller, GyroArc, GyroDrive, GyroSetHeading, GyroTurn, GyroUnwind, LineDrive, Move, TtyMenu, TtyMenuAction,WhiteBlackWhite}
import ev3dev4s.measure.Conversions._
import ev3dev4s.actuators.MotorStopCommand
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.measure.MilliMeters

/**
 * A collection of moves for the sorting center 
 */
object SortingCenter {

  val startToParkRoad: Seq[Move] = Seq(
    GyroSetHeading(-45.degrees),
    //From home - Forward at -45
    GyroDrive.driveForwardDistance(-45.degrees, Robot.fineSpeed, 140.mm), //consider going forward until you see "not white"

    //acquire line black-on-left on right sensor at -45
    //follow line black-on-left on right sensor at -45 until left sensor sees black

    WhiteBlackWhite.driveForwardToWhiteBlackWhite(-45.degrees, Robot.fineSpeed, 240.mm, Robot.leftDriveMotor, Robot.leftColorSensor),

    //follow line black-on-left on right sensor at  -45 X mm
    LineDrive.driveForwardUntilDistance(-45.degrees, Robot.rightColorSensor, BlackSide.Left, Robot.fineSpeed, 76.mm),

    //set up a forward left turn
    GyroDrive.driveForwardDistance(-45.degrees, Robot.fineSpeed, 80.mm), //consider going forward until you see "not white"

    //arc-drive right ? radius until at 0 and aquire black-on-right with right sensor
    GyroArc.driveArcForwardRight(0.degrees, Robot.fineSpeed, 128.mm + Robot.wheelToWheel),

    //todo maybe replace all of this business with "find the line and line follow it for (find distance - 640mm) -then gyro drive out to bump the train
    //follow line black-on-right with right sensor heading 0 120 mm
    LineDrive.driveForwardUntilDistance(0.degrees, Robot.rightColorSensor, BlackSide.Right, Robot.fineSpeed, 168.mm),

    WhiteBlackWhite.driveForwardToWhiteBlackWhite(0.degrees, Robot.fineSpeed, 5.studs, Robot.leftDriveMotor, Robot.leftColorSensor),

    Robot.Hold,
  )

  val parkRoadToShipRoad: Seq[Move] = Seq(
    LineDrive.driveForwardUntilDistance(0.degrees, Robot.rightColorSensor, BlackSide.Right, Robot.fineSpeed, 140.mm),

    WhiteBlackWhite.driveForwardToWhiteBlackWhite(0.degrees, Robot.fineSpeed, 5.studs, Robot.leftDriveMotor, Robot.leftColorSensor),

    Robot.Hold
  )

  val shipRoadToEastSlot: Seq[Move] = Seq(
    //Gyro drive to the train and bump it
    GyroDrive.driveForwardDistance(0.degrees, Robot.fineSpeed, 630.mm),
    Robot.StopAndWaitForButton,

    //back up a bit
    GyroDrive.driveBackwardDistance(0.degrees, -Robot.fineSpeed, -30.mm),
    Robot.StopAndWaitForButton,

    //todo maybe a more acute angle - was about three studs to the right (west) at the sorting center
    GyroTurn.rightBackwardPivot(45.degrees, -Robot.fineSpeed),
    Robot.StopAndWaitForButton,

    //todo try a bit further forward
    //    GyroDrive.driveForwardDistance(45.degrees,Robot.fineSpeed,120.mm),
    GyroDrive.driveForwardDistance(45.degrees, Robot.fineSpeed, 140.mm),
    Robot.StopAndWaitForButton,

    //todo this didn't get the motors going, and crashed with a stack trace with duty cycles > 100% - maybe change driveArc to use setSpeed instead of direct

    /*
    1654018045765 Finished net.walend.cargoconnect.Robot$StopAndWaitForButton$@18ee631
1654018045773 Start FeedbackMove(Arc FR,net.walend.lessons.GyroArc$$$Lambda$47/0xb114fa28@c77c7,net.walend.lessons.GyroArc$$$Lambda$48/0xb114e028@dda9cc,net.walend.lessons.GyroArc$$$Lambda$49/0xb114d028@ee5251,net.walend.lessons.GyroArc$$$Lambda$50/0xb1159028@13d61fb,net.walend.lessons.GyroArc$$$Lambda$51/0xb1158a28@909414)
1654018087993 abs duty cycle 100.12462% is greater than 100.0%
1654018088015 abs duty cycle 107.236305% is greater than 100.0%
1654018088396 caught java.io.IOException: Invalid argument with 'Invalid argument'
java.io.IOException: Invalid argument
	at java.base/sun.nio.ch.FileDispatcherImpl.pwrite0(Native Method)
	at java.base/sun.nio.ch.FileDispatcherImpl.pwrite(FileDispatcherImpl.java:68)
	at java.base/sun.nio.ch.IOUtil.writeFromNativeBuffer(IOUtil.java:109)
	at java.base/sun.nio.ch.IOUtil.write(IOUtil.java:79)
	at java.base/sun.nio.ch.FileChannelImpl.writeInternal(FileChannelImpl.java:850)
	at java.base/sun.nio.ch.FileChannelImpl.write(FileChannelImpl.java:836)
	at ev3dev4s.sysfs.ChannelRewriter.writeString(ChannelRewriter.scala:25)
	at ev3dev4s.sysfs.ChannelRewriter.writeAsciiInt(ChannelRewriter.scala:29)
	at ev3dev4s.actuators.MotorFS.writeDutyCycle(MotorFS.scala:45)
	at ev3dev4s.actuators.Motor.writeDutyCycle$$anonfun$1(Motor.scala:30)
	at scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	at scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	at scala.Option.fold(Option.scala:263)
	at ev3dev4s.sysfs.Gadget.liftedTree1$1(Gadget.scala:41)
	at ev3dev4s.sysfs.Gadget.checkPort(Gadget.scala:47)
	at ev3dev4s.actuators.Motor.writeDutyCycle(Motor.scala:30)
	at net.walend.cargoconnect.Robot$.directDrive(Robot.scala:66)
	at net.walend.lessons.GyroArc$.arcDrive(GyroArc.scala:33)
	at net.walend.lessons.GyroArc$.driveArcForwardRight$$anonfun$3$$anonfun$1(GyroArc.scala:59)
	at scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	at scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	at net.walend.lessons.FeedbackLoop$.feedback(FeedbackDrive.scala:21)
	at net.walend.lessons.FeedbackMove.move(FeedbackDrive.scala:39)
	at net.walend.lessons.MovesMenuAction.act$$anonfun$1(TtyMenu.scala:79)
	at scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
	at scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
	at scala.collection.immutable.List.foreach(List.scala:333)
	at net.walend.lessons.MovesMenuAction.act(TtyMenu.scala:80)
	at net.walend.lessons.TtyMenu.doAction(TtyMenu.scala:40)
	at net.walend.lessons.TtyMenu.loop(TtyMenu.scala:31)
	at net.walend.lessons.Controller.run(Controller.scala:28)
	at net.walend.cargoconnect.CargoConnect$.run(CargoConnect.scala:59)
    */

    //todo this is badly behaved for some reason
    GyroArc.driveArcForwardRight(90.degrees, Robot.fineSpeed, Robot.wheelToWheel + 30.mm),
    Robot.StopAndWaitForButton,

    //todo not needed if the green block is the furthest forward
    LineDrive.driveForwardUntilDistance(90.degrees, Robot.leftColorSensor, BlackSide.Left, Robot.fineSpeed, 30.mm),

    Robot.Hold,
    Robot.Beep
  ) //++ southToEastSlot //line drive to east slot

  /**
   * Line drive/gyro assist parallel to the train tracks to the east slot
   *
   * Starts with left rear corner against helicopter, left color sensor on left edge of line
   */
  //todo back up 3 studs unless green is in east slot
  def southToEastSlot: Seq[Move] = Seq(
    //    GyroSetHeading(90.degrees), //todo remove this gyro set when done testing
    LineDrive.driveForwardUntilDistance(90.degrees, Robot.leftColorSensor, BlackSide.Left, Robot.fineSpeed, 40.mm), //,440.mm), //todo stop after stall or distance
    //todo consider switching to both color sensors at ~330mm
    GyroTurn.rightForwardPivot(90.degrees, Robot.fineSpeed),
    GyroTurn.leftForwardPivot(90.degrees, Robot.fineSpeed),
    Robot.Hold
  )

  /**
   * Move to the correct slot, capture the blue container and deliver it to the blue circle
   *
   * Start with the robot in contact with the east slot
   */
  lazy val deliverBlueFromWestSlot: Seq[Move] =
    eastSlotToWestSlot ++
      deliverBlueFromSlot(GyroDrive.driveBackwardDistance(180.degrees, -Robot.fineSpeed, -(slotToSlotDistance + slotToSlotDistance - eastSlotToBlueCircleCenterOffset)))
  lazy val deliverBlueFromCenterSlot: Seq[Move] =
    eastSlotToCenterSlot ++
      deliverBlueFromSlot(GyroDrive.driveForwardDistance(180.degrees, Robot.fineSpeed, eastSlotToBlueCircleCenterOffset - slotToSlotDistance))
  lazy val deliverBlueFromEastSlot: Seq[Move] =
    deliverBlueFromSlot(GyroDrive.driveForwardDistance(180.degrees, Robot.fineSpeed, eastSlotToBlueCircleCenterOffset))

  /**
   * Move from the east slot to another slot (to capture blue) (todo eventually have defs to capture green as well)
   *
   * Starting point for these is either 5 studs or 2 studs into the east slot of the sorting center (if the green
   * container is in the east slot)
   * End point is 5 studs into the slot with the blue container (or two studs to pick up the green container)
   */
  private lazy val eastSlotToCenterSlot: Seq[Move] = eastSlotToOtherSlot(slotToSlotDistance)
  private lazy val eastSlotToWestSlot: Seq[Move] = eastSlotToOtherSlot(slotToSlotDistance + slotToSlotDistance)
  private lazy val slotToSlotDistance: MilliMeters = 1.studs + 11.studs //+ 4.mm
  private lazy val eastSlotToBlueCircleCenterOffset: MilliMeters = 14.studs

  private def eastSlotToOtherSlot(distanceToSlot: MilliMeters): Seq[Move] = Seq(
    GyroDrive.driveBackwardDistance(90.degrees, -Robot.fineSpeed, -6.studs),
    GyroTurn.rightRotate(180.degrees, Robot.fineSpeed),
    GyroDrive.driveForwardDistance(180.degrees, Robot.fineSpeed, distanceToSlot),
    GyroTurn.leftRotate(90.degrees, Robot.fineSpeed),
    GyroDrive.driveForwardDistance(90.degrees, Robot.fineSpeed, 6.studs)
  )

  private def deliverBlueFromSlot(eastWestCorrection: Move): Seq[Move] =
    captureBlueFromAnySlot ++ Seq(
      GyroDrive.driveBackwardDistance(90.degrees, -Robot.fineSpeed, -(5 * 8).mm),
      GyroTurn.rightRotate(180.degrees, Robot.fineSpeed),
      eastWestCorrection,
    ) ++ deliverBlueFromSouthOfBlueCircle

  private lazy val deliverBlueFromSouthOfBlueCircle = Seq(
    GyroTurn.rightRotate(270.degrees, Robot.fineSpeed),
    //touches back wall - might stall
    GyroDrive.driveForwardDistance(270.degrees, Robot.fineSpeed, (800 + 80 - (2 * Robot.driveAxelToExtendedFork.v)).mm),
    GyroDrive.driveBackwardDistance(270.degrees, -Robot.fineSpeed, -104.mm),
    Robot.Hold,
    ForkMoves.ForkIn,
    Robot.Beep,
  )

  private lazy val captureBlueFromAnySlot = Seq(
    Robot.Hold,
    ForkMoves.ForkOutUp,
  )

  /*
    Crashed with

  1654018825852 Start FeedbackMove(GyroB -40.0mm,net.walend.lessons.GyroDrive$$$Lambda$52/0xb115f828@dec4a,net.walend.lessons.GyroDrive$$$Lambda$53/0xb115e828@16def03,net.walend.lessons.GyroDrive$$$Lambda$54/0xb115d828@cddd20,net.walend.lessons.GyroDrive$$$Lambda$55/0xb115c828@d1993a,net.walend.lessons.GyroDrive$$$Lambda$56/0xb115c028@13f4916)
1654018825896 abs duty cycle -371.42856% is greater than 100.0%
1654018826046 caught java.io.IOException: Invalid argument with 'Invalid argument'
java.io.IOException: Invalid argument
	at java.base/sun.nio.ch.FileDispatcherImpl.pwrite0(Native Method)
	at java.base/sun.nio.ch.FileDispatcherImpl.pwrite(FileDispatcherImpl.java:68)
	at java.base/sun.nio.ch.IOUtil.writeFromNativeBuffer(IOUtil.java:109)
	at java.base/sun.nio.ch.IOUtil.write(IOUtil.java:79)
	at java.base/sun.nio.ch.FileChannelImpl.writeInternal(FileChannelImpl.java:850)
	at java.base/sun.nio.ch.FileChannelImpl.write(FileChannelImpl.java:836)
	at ev3dev4s.sysfs.ChannelRewriter.writeString(ChannelRewriter.scala:25)
	at ev3dev4s.sysfs.ChannelRewriter.writeAsciiInt(ChannelRewriter.scala:29)
	at ev3dev4s.actuators.MotorFS.writeDutyCycle(MotorFS.scala:45)
	at ev3dev4s.actuators.Motor.writeDutyCycle$$anonfun$1(Motor.scala:30)

  */

  val blueCircleToEastSlot: Seq[Move] = Seq(
    GyroUnwind,
    GyroDrive.driveBackwardDistance(-90.degrees, -Robot.fineSpeed, -40.mm),
    GyroTurn.rightRotate(0.degrees, Robot.fineSpeed),
    GyroTurn.rightForwardPivot(90.degrees, Robot.fineSpeed),
    //todo lower arm to push train
    LineDrive.driveForwardUntilDistance(90.degrees, Robot.leftColorSensor, BlackSide.Left, Robot.fineSpeed, 500.mm),
    //todo raise arm
    Robot.Hold
  )
}

object SortingCenterMenu extends TtyMenuAction {
  //todo up/down arrows would be more intuitive
  //todo remove the reload option as part of refactoring

  //todo move the slot and container enums to SortingCenter
  sealed case class Slot(ordinal:Int)

  object Slot {
    val East = Slot(0)
    val Center = Slot(1)
    val West = Slot(2)

    val values = Array(East,Center,West)

    def fromOrdinal(i:Int) = values(i)
  }

  sealed case class Container()

  object Container {
    val Blue = Container()
    val Green = Container()
    val Yellow = Container()
  }

  import Slot._
  import Container._

  private val slotArray: Array[Container] = Array(Blue, Green, Yellow)

  def slotForContainer(container: Container): Slot = Slot.fromOrdinal(slotArray.indexOf(container))

  val actions: Array[TtyMenuAction] = Array(
    Done,
    Swap(Blue, West),
    Swap(Blue, East),
    Swap(Green, West),
    Swap(Green, East)
  )

  object Done extends TtyMenuAction {
    override def act(ttyMenu: TtyMenu): Unit = ttyMenu.stopLoop()
  }

  case class Swap(container: Container, slot: Slot) extends TtyMenuAction {
    override def label: String = s"$container $slot"

    def act(menu: TtyMenu): Unit = {
      //What index has the right container?
      val currentIndex = slotArray.indexOf(container)
      //What direction to swap?
      val targetIndex = {
        val rawIndex = slot match {
          case West => currentIndex + 1
          case Center => currentIndex //do nothing
          case East => currentIndex - 1
        }
        if (rawIndex < 0) 2
        else if (rawIndex > 2) 0
        else rawIndex
      }

      //What container to replace it with
      val targetContainer = slotArray(targetIndex)
      slotArray(targetIndex) = container
      slotArray(currentIndex) = targetContainer
    }
  }
  def setSensorRows(): Unit =
    Slot.values.foreach { (slot: Slot) =>
      Lcd.set(slot.ordinal, s"${slotArray(slot.ordinal)}", Lcd.LEFT)
    }

  val lcdView: Controller = Controller(actions, setSensorRows) //todo separate timing display from the rest of the Controller

  override def label: String = "Set Boxes"

  def act(menu: TtyMenu): Unit = lcdView.run()
}
