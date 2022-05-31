package net.walend.cargoconnect

import net.walend.lessons.{BlackSide, Controller, GyroArc, GyroDrive, GyroSetHeading, GyroTurn, LineDrive, Move, TtyMenu, TtyMenuAction}
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
    GyroTurn.leftForwardPivot(-135.degrees,Robot.fineSpeed),
    GyroDrive.driveForwardDistance(-135.degrees,Robot.fineSpeed,80.mm),
    GyroTurn.leftBackwardPivot(-180.degrees,-Robot.fineSpeed),
    GyroDrive.driveBackwardDistance(-180.degrees,-Robot.fineSpeed,-200.mm),
    Robot.Coast
  )

  val escapeCrane:Seq[Move] = Seq(
    GyroDrive.driveForwardDistance(-180.degrees,Robot.fineSpeed,104.mm),
    //todo raise arm out of way
    Robot.Coast
  )

  
  
  
    
