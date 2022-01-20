package net.walend.cargoconnect

import net.walend.lessons.Move

import net.walend.lessons.{GyroSetHeading,GyroDriveDistanceBackward,LeftBackwardPivot,GyroDriveDistanceForward,LeftForwardPivot}

import ev3dev4s.measure.Conversions.*
import ev3dev4s.actuators.MotorStopCommand
import net.walend.lessons.RightRotate

object SortingCenter:

/**
 * Start with the robot in contact with a slot
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
 * 
 */ 

  lazy val deliverBlueFromWestSlot:Seq[Move] = deliverBlueFromSlot(GyroDriveDistanceBackward(180.degrees,-Robot.fineSpeed,-68.mm)) 
  lazy val deliverBlueFromCenterSlot:Seq[Move] = deliverBlueFromSlot(GyroDriveDistanceForward(180.degrees,Robot.fineSpeed,44.mm)) 
  lazy val deliverBlueFromEastSlot:Seq[Move] = deliverBlueFromSlot(GyroDriveDistanceForward(180.degrees,Robot.fineSpeed,(44+68).mm))

  def deliverBlueFromSlot(eastWestCorrection:Move):Seq[Move] = 
    captureBlueFromASlot ++ Seq(
      GyroSetHeading(90.degrees),
      Robot.Hold,
      ForkMoves.ForkOutUp,
      GyroDriveDistanceBackward(90.degrees,-Robot.fineSpeed,-(5*8).mm),
      RightRotate(180.degrees,Robot.fineSpeed),
      eastWestCorrection,
  ) ++ deliverBlueFromSouthOfBlueCircle

  lazy val deliverBlueFromSouthOfBlueCircle = Seq(
    RightRotate(270.degrees,Robot.fineSpeed),
    GyroDriveDistanceForward(270.degrees,Robot.fineSpeed,(800 + 80 -(2*33*8)).mm), //touches back wall - might stall
    GyroDriveDistanceBackward(270.degrees,-Robot.fineSpeed,-104.mm),
    Robot.Hold,
    ForkMoves.ForkIn,
    Robot.Beep,
    Robot.Coast  
  )

  lazy val captureBlueFromASlot = Seq(
    GyroSetHeading(90.degrees),
    Robot.Hold,
    ForkMoves.ForkOutUp,
  )
