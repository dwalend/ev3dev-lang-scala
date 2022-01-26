package net.walend.cargoconnect

import net.walend.lessons.Move

import net.walend.lessons.{GyroSetHeading,GyroDriveDistanceBackward,LeftBackwardPivot,GyroDriveDistanceForward,LeftForwardPivot}

import ev3dev4s.measure.Conversions.*
import ev3dev4s.actuators.MotorStopCommand
import net.walend.lessons.RightRotate
import net.walend.lessons.LeftRotate
import ev3dev4s.measure.MilliMeters
import net.walend.lessons.{LineDriveDistanceForward,BlackSide}
import net.walend.lessons.RightForwardPivot

/**
 * A collection of moves for the sorting center 
 */
object SortingCenter:

  enum Slot:
    case East
    case Center
    case West

  var blueSlot:Slot = Slot.West
  var greenSlot:Slot = Slot.Center

/**
* Line drive/gyro assist parallel to the train tracks to the east slot 
* 
* Starts with left rear corner against helicopter, left color sensor on left edge of line
 */ 
//todo back up 3 studs unless green is in east slot
  def southToEastSlot:Seq[Move] = Seq(
    GyroSetHeading(90.degrees), //todo remove this gyro set when done testing
    LineDriveDistanceForward(Robot.leftColorSensor,BlackSide.Left,Robot.fineSpeed,440.mm), //todo stop after stall or distance
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
    deliverBlueFromSlot(GyroDriveDistanceBackward(180.degrees,-Robot.fineSpeed,-(slotToSlotDistance+slotToSlotDistance-eastSlotToBlueCircleCenterOffset)))
  lazy val deliverBlueFromCenterSlot:Seq[Move] =
    eastSlotToCenterSlot ++
    deliverBlueFromSlot(GyroDriveDistanceForward(180.degrees,Robot.fineSpeed,eastSlotToBlueCircleCenterOffset-slotToSlotDistance))
  lazy val deliverBlueFromEastSlot:Seq[Move] =
    deliverBlueFromSlot(GyroDriveDistanceForward(180.degrees,Robot.fineSpeed,eastSlotToBlueCircleCenterOffset))

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
    GyroDriveDistanceBackward(90.degrees,-Robot.fineSpeed,-6.studs),
    RightRotate(180.degrees,Robot.fineSpeed),
    GyroDriveDistanceForward(180.degrees,Robot.fineSpeed,distanceToSlot),
    LeftRotate(90.degrees,Robot.fineSpeed),
    GyroDriveDistanceForward(90.degrees,Robot.fineSpeed,6.studs)
  )

  private def deliverBlueFromSlot(eastWestCorrection:Move):Seq[Move] = 
    captureBlueFromAnySlot ++ Seq(
      GyroDriveDistanceBackward(90.degrees,-Robot.fineSpeed,-(5*8).mm),
      RightRotate(180.degrees,Robot.fineSpeed),
      eastWestCorrection,
  ) ++ deliverBlueFromSouthOfBlueCircle

  private lazy val deliverBlueFromSouthOfBlueCircle = Seq(
    RightRotate(270.degrees,Robot.fineSpeed),
    //touches back wall - might stall
    GyroDriveDistanceForward(270.degrees,Robot.fineSpeed,(800 + 80 -(2*Robot.driveAxelToExtendedFork.value)).mm), 
    GyroDriveDistanceBackward(270.degrees,-Robot.fineSpeed,-104.mm),
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

//  object SortingCenterMenu