package net.walend.cargoconnect

import net.walend.lessons.{BlackSide, Controller, GyroArc, GyroDrive, GyroSetHeading, GyroTurn, LineDrive, Move, TtyMenu, TtyMenuAction}
import ev3dev4s.measure.Conversions._
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
object LoadShip {
  val shipRoadToShip: Seq[Move] = Seq(
    GyroDrive.driveForwardDistance(0.degrees, Robot.fineSpeed, 56.mm),
    GyroTurn.leftForwardPivot(-90.degrees, Robot.fineSpeed),
    GyroDrive.driveForwardDistance(-90.degrees, Robot.fineSpeed, 112.mm),
    Robot.Hold
  )

  val putContainersOnShip: Seq[Move] = Seq(
    ForkMoves.ForkOut,
    ForkMoves.ForkIn,
  )

  val escapeShip: Seq[Move] = Seq(
    GyroDrive.driveBackwardDistance(-90.degrees, -Robot.fineSpeed, -48.mm),
    Robot.Coast
  )

  val raiseCrane: Seq[Move] = Seq(
    GyroTurn.leftForwardPivot(-135.degrees, Robot.fineSpeed),
    //    GyroDrive.driveForwardDistance(-135.degrees,Robot.fineSpeed,40.mm),
    //todo lower arm
    GyroTurn.leftBackwardPivot(-180.degrees, -Robot.fineSpeed),
    GyroDrive.driveBackwardDistance(-180.degrees, -Robot.fineSpeed, -200.mm),
    Robot.Coast
  )

  val escapeCrane: Seq[Move] = Seq(
    GyroDrive.driveForwardDistance(-180.degrees, Robot.fineSpeed, 200.mm),
    //todo raise arm out of way
    Robot.Coast
  )
}

  
  
  
    
