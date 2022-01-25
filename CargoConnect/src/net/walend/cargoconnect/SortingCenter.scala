package net.walend.cargoconnect

import net.walend.lessons.Move

import net.walend.lessons.{GyroSetHeading,GyroDriveDistanceBackward,LeftBackwardPivot,GyroDriveDistanceForward,LeftForwardPivot}

import ev3dev4s.measure.Conversions.*
import ev3dev4s.actuators.MotorStopCommand
import net.walend.lessons.RightRotate
import net.walend.lessons.LeftRotate
import ev3dev4s.measure.MilliMeters

/**
 * A collection of moves for the sorting center 
 */
object SortingCenter:

  enum Slot:
    case West
    case Center
    case East

  var blueSlot:Slot = Slot.West
  var greenSlot:Slot = Slot.Center
/**
 * Move to the correct slot, capture the blue container and deliver it to the blue circle
 *
 * Start with the robot in contact with the east slot
 * 
 * Fork forward and up
 * Back up 5 studs
 * Rotate right to 180
 * Forward or backward to correct distance @180 (West: -68mm Center: +44mm East: +44mm + 68mm)
 * Rotate right to 270
 * Forward to wall at 270 800mm - 2* 33*8 mm (that's twice the robot's reach from axels to forks extended) + 80mm (fork length)
 * Backward 104mm at 270
 * Pull forks in 
 * Beep in triumph
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
  private lazy val slotToSlotDistance: MilliMeters = 1.studs + 11.studs + 4.mm
  private lazy val eastSlotToBlueCircleCenterOffset:MilliMeters = 14.studs

  private def eastSlotToOtherSlot(distanceToSlot:MilliMeters):Seq[Move] = Seq(
    GyroSetHeading(90.degrees), //todo remove this gyro set when done testing
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
    GyroSetHeading(90.degrees), //todo remove this gyro set when done testing
    Robot.Hold,
    ForkMoves.ForkOutUp,
  )
