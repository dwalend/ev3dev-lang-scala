package net.walend.cargoconnect

import net.walend.lessons.Move

import net.walend.lessons.{GyroSetHeading,GyroDriveDistanceBackward,LeftBackwardPivot,GyroDriveDistanceForward,LeftForwardPivot}

import ev3dev4s.measure.Conversions.*
import ev3dev4s.actuators.MotorStopCommand

object SortingCenter:

/**
 * Start with the robot in contact with the west slot
 * Set gyro to 90 (for running the mission in isolation)
 * 
 * * Drive forks forward and up
 * * Back up 16 studs (16*8 mm)
 * * Left back pivot to 0
 * * Forward 80 mm at 0
 * * Left forward pivot to -90
 * * Forward 16 cm at -90 (to wall)
 * * Backward 11 cm at -90
 * * Pull forks down and in
 * * Beep in triumph
 */
  val deliverBlueFromWestSlot:Seq[Move] = Seq(
    Robot.Brake,
    GyroSetHeading(90.degrees),
    ForkMoves.ForkOutUp,
    Robot.WaitForButton,
    GyroDriveDistanceBackward(90.degrees,-Robot.fineSpeed,-(16*8).mm),
    Robot.WaitForButton,
    LeftBackwardPivot(0.degrees,Robot.fineSpeed),
    Robot.WaitForButton,
    GyroDriveDistanceForward(0.degrees,Robot.fineSpeed,80.mm),
    Robot.WaitForButton,
    LeftForwardPivot(-90.degrees,Robot.cruiseSpeed),
    Robot.WaitForButton,
    GyroDriveDistanceForward(-90.degrees,Robot.cruiseSpeed,160.mm),
    Robot.WaitForButton,
    GyroDriveDistanceBackward(-90.degrees,-Robot.cruiseSpeed,-110.mm),
    Robot.Hold,
    ForkMoves.ForkIn,
    Robot.Beep,
    Robot.Coast
  )

