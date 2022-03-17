package net.walend.cargoconnect

import ev3dev4s.{Ev3System, Log}
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.sensors.{Ev3Gyroscope, Ev3KeyPad}
import ev3dev4s.sysfs.UnpluggedException
import ev3dev4s.sensors.Ev3ColorSensor
import ev3dev4s.sensors.SensorPort
import ev3dev4s.measure.Conversions.*
import net.walend.lessons.{CalibrateReflect, Controller, DespinGyro, GyroDriveDistanceBackward, GyroDriveDistanceForward, GyroSetHeading, MovesMenuAction, TtyMenu, TtyMenuAction}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object CargoConnect extends Runnable:
  val actions: Array[TtyMenuAction] = Array(
      SortingCenterMenu,
      MovesMenuAction("ToEastSlot",SortingCenter.startToEastSlot),
      MovesMenuAction("WSort-Blu",SortingCenter.deliverBlueFromWestSlot),
      MovesMenuAction("CSort-Blu",SortingCenter.deliverBlueFromCenterSlot),
      MovesMenuAction("ESort-Blu",SortingCenter.deliverBlueFromEastSlot),
      MovesMenuAction("Stop",Robot.Coast),
      MovesMenuAction("Despin",Seq(DespinGyro)),
      MovesMenuAction("ColorCalibrate",Seq(CalibrateReflect)),
  )

  //todo add color sensors
  def setSensorRows():Unit =
    Lcd.set(0,s"${lcdView.elapsedTime}s",Lcd.RIGHT)
    val heading:String = UnpluggedException.safeString(() => s"${Robot.gyroscope.headingMode().readHeading().value}d")
    Lcd.set(0,heading,Lcd.LEFT)

    val forkDegrees = UnpluggedException.safeString(() => s"Fork ${Robot.forkMotor.readPosition().value}d")
    Lcd.set(1,forkDegrees,Lcd.LEFT)

  val lcdView:Controller = Controller(actions,setSensorRows)

  override def run():Unit = lcdView.run()

  def main(args: Array[String]): Unit = run()