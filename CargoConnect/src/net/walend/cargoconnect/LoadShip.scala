package net.walend.cargoconnect

import net.walend.lessons.{BlackSide, Controller, GyroArcFeedback, GyroDrive, GyroSetHeading, GyroTurn, LineDriveFeedback, Move, TtyMenu, TtyMenuAction}
import ev3dev4s.measure.Conversions.*
import ev3dev4s.actuators.MotorStopCommand
import ev3dev4s.lcd.tty.Lcd
import net.walend.cargoconnect
import net.walend.cargoconnect.ForkMoves

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object LoadShip:
  val shipRoadToShip:Seq[Move] = Seq(
    //todo forward 4 studs

    GyroTurn.leftForwardPivot(-90.degrees,Robot.fineSpeed),
    GyroDrive.driveForwardDistance(-90.degrees,Robot.fineSpeed,80.mm),
    Robot.Hold
  )
  
  val putContainersOnShip:Seq[Move] = Seq(
    ForkMoves.ForkOut,
    ForkMoves.ForkIn,
  )
  
  val escapeShip:Seq[Move] = Seq(
    GyroDrive.driveBackwardDistance(-90.degrees,-Robot.fineSpeed,-30.mm),
    Robot.Coast
  )

  val raiseCrane:Seq[Move] = Seq(
    //todo instead left forward pivot to -135, forward 80mm, left backward pivot to -180, then back up
    //todo then maybe find the black line, then back up 200mm


    GyroTurn.leftBackwardPivot(-180.degrees,-Robot.fineSpeed),
    //todo lower arm to right side to push crane
    GyroDrive.driveBackwardDistance(-180.degrees,-Robot.fineSpeed,160.mm),
    Robot.Coast
  )

  val escapeCrane:Seq[Move] = Seq(
    GyroDrive.driveForwardDistance(-180.degrees,Robot.fineSpeed,104.mm),
    //todo raise arm out of way
    Robot.Coast
  )

  
  
  
    
