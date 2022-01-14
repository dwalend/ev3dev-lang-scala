package net.walend.cargoconnect

import ev3dev4s.{Ev3System, Log}
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.sensors.{Ev3Gyroscope, Ev3KeyPad}
import ev3dev4s.sysfs.UnpluggedException
import ev3dev4s.sensors.Ev3ColorSensor
import ev3dev4s.sensors.SensorPort

import ev3dev4s.measure.Conversions.*

import net.walend.lessons.{TtyMenuAction,TtyMenu,MovesMenuAction,GyroDriveDistanceForward,GyroSetHeading,GyroDriveDistanceBackward,DespinGyro,Controller}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object CargoConnect:
  def main(args: Array[String]): Unit =
    lcdView.run()

  val lcdView:Controller = Controller(actions,setSensorRows)
  
  val actions = Array(
      MovesMenuAction("WSortToBlue",SortingCenter.deliverBlueFromWestSlot),
      MovesMenuAction("GyroBack",Seq(GyroDriveDistanceBackward(0.degrees,-Robot.fineSpeed,-(16*8).mm),Robot.Coast)),
      MovesMenuAction("SetGyro0",Seq(GyroSetHeading(0.degrees))),
      MovesMenuAction("SetGyro90",Seq(GyroSetHeading(90.degrees))),
      MovesMenuAction("Gyro90Back",Seq(
        GyroSetHeading(90.degrees),
        GyroDriveDistanceBackward(90.degrees,-Robot.fineSpeed,(16*8).mm),
        Robot.Brake
      )),
      MovesMenuAction("GyroDrive",Seq(GyroDriveDistanceForward(0.degrees,500.degreesPerSecond,1000.mm),Robot.Brake)),
      MovesMenuAction("SlowGyroDrive",Seq(GyroDriveDistanceForward(0.degrees,50.degreesPerSecond,1000.mm),Robot.Brake)),
      MovesMenuAction("ForkUp",Seq(ForkMoves.ForkOutUp)),
      MovesMenuAction("ForkIn",Seq(ForkMoves.ForkIn)),
      MovesMenuAction("ForkOut",Seq(ForkMoves.ForkOut)),
      DespinGyro,
    )

  //todo add color sensors
  def setSensorRows():Unit =
    Lcd.set(0,s"${lcdView.elapsedTime}s",Lcd.RIGHT)
    val heading:String = UnpluggedException.safeString(() => s"${Robot.gyroscope.headingMode().readHeading().value}d")
    Lcd.set(0,heading,Lcd.LEFT)

    val forkDegrees = UnpluggedException.safeString(() => s"Fork ${Robot.forkMotor.readPosition().value}d")
    Lcd.set(1,forkDegrees,Lcd.LEFT)