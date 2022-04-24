package net.walend.cargoconnect

import net.walend.lessons.{BlackSide, Controller, GyroArcForwardRight, GyroDrive, GyroSetHeading, LeftBackwardPivot, LeftForwardPivot, LeftRotate, LineDriveDistanceForward, LineDriveToBlackForward, Move, RightForwardPivot, RightRotate, TtyMenu, TtyMenuAction}
import ev3dev4s.measure.Conversions.*
import ev3dev4s.actuators.MotorStopCommand
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.measure.MilliMeters

/**
 * A collection of moves for the sorting center 
 */
object SortingCenter:

  def startToEastSlot:Seq[Move] = Seq(
    GyroSetHeading(-45.degrees),
    //From home - Forward at -45
    GyroDrive.driveForwardDistance(-45.degrees,Robot.fineSpeed,140.mm), //consider going forward until you see "not white"
    Robot.StopAndWaitForButton,

    //acquire line black-on-left on right sensor at -45
    //follow line black-on-left on right sensor at -45 until left sensor sees black //todo white-black-white?

    LineDriveToBlackForward(-45.degrees,Robot.rightColorSensor,BlackSide.Left,Robot.fineSpeed,240.mm,Robot.leftColorSensor),
    Robot.StopAndWaitForButton,

    //follow line black-on-left on right sensor at  -45 X mm
    LineDriveDistanceForward(-45.degrees,Robot.rightColorSensor,BlackSide.Left,Robot.fineSpeed,76.mm),
    Robot.StopAndWaitForButton,

    //140mm is the right distance to set up a forward left turn
    GyroDrive.driveForwardDistance(-45.degrees,Robot.fineSpeed,80.mm), //consider going forward until you see "not white"
    Robot.StopAndWaitForButton,

    //arc-drive right ? radius until at 0 and aquire black-on-right with right sensor todo radius??
    GyroArcForwardRight(0.degrees,128.mm+Robot.wheelToWheel,Robot.fineSpeed),

    Robot.StopAndWaitForButton,

    //follow line black-on-right with right sensor heading 0 120 mm
    LineDriveDistanceForward(0.degrees,Robot.rightColorSensor,BlackSide.Right,Robot.fineSpeed,160.mm),

    Robot.StopAndWaitForButton,

    //follow line black-on-right with right sensor with heading 0 until black //todo white-black on left sensor
    LineDriveToBlackForward(0.degrees,Robot.rightColorSensor,BlackSide.Right,Robot.fineSpeed,3.studs,Robot.leftColorSensor),

    Robot.StopAndWaitForButton,

    //drive straight at heading 0 2 studs
    GyroDrive.driveForwardDistance(0.degrees,Robot.fineSpeed,2.studs),

    Robot.StopAndWaitForButton,

    LineDriveDistanceForward(0.degrees,Robot.rightColorSensor,BlackSide.Right,Robot.fineSpeed,140.mm),

    Robot.StopAndWaitForButton,

    //follow line black-on-right with right sensor with heading 0 until black //todo white-black on left sensor
    LineDriveToBlackForward(0.degrees,Robot.rightColorSensor,BlackSide.Right,Robot.fineSpeed,3.studs,Robot.leftColorSensor),

    //drive straight at heading 0 2 studs
    GyroDrive.driveForwardDistance(0.degrees,Robot.fineSpeed,2.studs),

    Robot.StopAndWaitForButton,

    //follow line black-on-right with right sensor with heading 0 X mm
    LineDriveDistanceForward(0.degrees,Robot.rightColorSensor,BlackSide.Left,Robot.fineSpeed,40.mm),

    Robot.StopAndWaitForButton,

    //Gyro drive to the turning point
    GyroDrive.driveForwardDistance(0.degrees,Robot.fineSpeed,360.mm), //consider going forward until you see "not white"

    Robot.StopAndWaitForButton,

    //arc drive right ? radius until at 90 and aquire black-on-left with left sensor
    GyroArcForwardRight(90.degrees,200.mm+Robot.wheelToWheel,Robot.fineSpeed),

    Robot.Hold,
    Robot.Beep
  ) //++ southToEastSlot //line drive to east slot

/**
* Line drive/gyro assist parallel to the train tracks to the east slot 
* 
* Starts with left rear corner against helicopter, left color sensor on left edge of line
 */ 
//todo back up 3 studs unless green is in east slot
  def southToEastSlot:Seq[Move] = Seq(
//    GyroSetHeading(90.degrees), //todo remove this gyro set when done testing
    LineDriveDistanceForward(90.degrees,Robot.leftColorSensor,BlackSide.Left,Robot.fineSpeed,40.mm),//,440.mm), //todo stop after stall or distance
    //todo consider switching to both color sensors at ~330mm 
    RightForwardPivot(90.degrees,Robot.fineSpeed),
    LeftForwardPivot(90.degrees,Robot.fineSpeed),
    Robot.Hold
  )

/**
 * Move to the correct slot, capture the blue container and deliver it to the blue circle
 *
 * Start with the robot in contact with the east slot
 */ 
  lazy val deliverBlueFromWestSlot:Seq[Move] =
    eastSlotToWestSlot ++
    deliverBlueFromSlot(GyroDrive.driveBackwardDistance(180.degrees,-Robot.fineSpeed,-(slotToSlotDistance+slotToSlotDistance-eastSlotToBlueCircleCenterOffset)))
  lazy val deliverBlueFromCenterSlot:Seq[Move] =
    eastSlotToCenterSlot ++
    deliverBlueFromSlot(GyroDrive.driveForwardDistance(180.degrees,Robot.fineSpeed,eastSlotToBlueCircleCenterOffset-slotToSlotDistance))
  lazy val deliverBlueFromEastSlot:Seq[Move] =
    deliverBlueFromSlot(GyroDrive.driveForwardDistance(180.degrees,Robot.fineSpeed,eastSlotToBlueCircleCenterOffset))

/**
 * Move from the east slot to another slot (to capture blue) (todo eventually have defs to capture green as well)
 * 
 * Starting point for these is either 5 studs or 2 studs into the east slot of the sorting center (if the green 
 * container is in the east slot)
 * End point is 5 studs into the slot with the blue container (or two studs to pick up the green container)
 */ 
  private lazy val eastSlotToCenterSlot:Seq[Move] = eastSlotToOtherSlot(slotToSlotDistance)
  private lazy val eastSlotToWestSlot:Seq[Move] = eastSlotToOtherSlot(slotToSlotDistance + slotToSlotDistance)
  private lazy val slotToSlotDistance: MilliMeters = 1.studs + 11.studs //+ 4.mm
  private lazy val eastSlotToBlueCircleCenterOffset:MilliMeters = 14.studs

  private def eastSlotToOtherSlot(distanceToSlot:MilliMeters):Seq[Move] = Seq(
    GyroDrive.driveBackwardDistance(90.degrees,-Robot.fineSpeed,-6.studs),
    RightRotate(180.degrees,Robot.fineSpeed),
    GyroDrive.driveForwardDistance(180.degrees,Robot.fineSpeed,distanceToSlot),
    LeftRotate(90.degrees,Robot.fineSpeed),
    GyroDrive.driveForwardDistance(90.degrees,Robot.fineSpeed,6.studs)
  )

  private def deliverBlueFromSlot(eastWestCorrection:Move):Seq[Move] = 
    captureBlueFromAnySlot ++ Seq(
      GyroDrive.driveBackwardDistance(90.degrees,-Robot.fineSpeed,-(5*8).mm),
      RightRotate(180.degrees,Robot.fineSpeed),
      eastWestCorrection,
  ) ++ deliverBlueFromSouthOfBlueCircle

  private lazy val deliverBlueFromSouthOfBlueCircle = Seq(
    RightRotate(270.degrees,Robot.fineSpeed),
    //touches back wall - might stall
    GyroDrive.driveForwardDistance(270.degrees,Robot.fineSpeed,(800 + 80 -(2*Robot.driveAxelToExtendedFork.value)).mm), 
    GyroDrive.driveBackwardDistance(270.degrees,-Robot.fineSpeed,-104.mm),
    Robot.Hold,
    ForkMoves.ForkIn,
    Robot.Beep,
    Robot.Coast  
  )

  private lazy val captureBlueFromAnySlot = Seq(
//    GyroSetHeading(90.degrees), //todo remove this gyro set when done testing
    Robot.Hold,
    ForkMoves.ForkOutUp,
  )

object SortingCenterMenu extends TtyMenuAction:
  //todo up/down arrows would be more intuitive
  //todo remove the reload option as part of refactoring

  //todo move the slot and container enums to SortingCenter
  enum Slot:
    case East
    case Center
    case West

  enum Container:
    case Blue
    case Green
    case Yellow

  import Slot.*
  import Container.*

  private val slotArray: Array[Container] = Array(Blue,Green,Yellow)

  def slotForContainer(container: Container): Slot = Slot.fromOrdinal(slotArray.indexOf(container))

  val actions: Array[TtyMenuAction] = Array(
      Done,
      Swap(Blue,West),
      Swap(Blue,East),
      Swap(Green,West),
      Swap(Green,East)
    )

  object Done extends TtyMenuAction:
    override def act(ttyMenu: TtyMenu): Unit = ttyMenu.stopLoop()

  case class Swap(container: Container,slot: Slot) extends TtyMenuAction:
    override def label:String = s"$container $slot"

    def act(menu: TtyMenu):Unit =
      //What index has the right container?
      val currentIndex = slotArray.indexOf(container)
      //What direction to swap?
      val targetIndex =
        val rawIndex = slot match
          case West => currentIndex +1
          case Center => currentIndex //do nothing
          case East => currentIndex -1
        if(rawIndex < 0) 2
        else if(rawIndex > 2) 0
        else rawIndex

      //What container to replace it with
      val targetContainer = slotArray(targetIndex)
      slotArray(targetIndex) = container
      slotArray(currentIndex) = targetContainer

  def setSensorRows():Unit =
    Slot.values.foreach{ (slot:Slot) =>
      Lcd.set(slot.ordinal,s"${slotArray(slot.ordinal)}",Lcd.LEFT)
    }

  val lcdView:Controller = Controller(actions,setSensorRows) //todo separate timing display from the rest of the Controller

  override def label:String = "Set Boxes"

  def act(menu: TtyMenu):Unit = lcdView.run()
