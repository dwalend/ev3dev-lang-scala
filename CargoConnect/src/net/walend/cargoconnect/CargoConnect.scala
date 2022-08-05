package net.walend.cargoconnect

import ev3dev4s.{Ev3System, Log}
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.sensors.{Ev3Gyroscope, Ev3KeyPad}
import ev3dev4s.sysfs.UnpluggedException
import ev3dev4s.sensors.Ev3ColorSensor
import ev3dev4s.sensors.SensorPort
import ev3dev4s.measure.Conversions._
import net.walend.lessons.{CalibrateReflect, Controller, DespinGyro, GyroArc, GyroDrive, GyroSetHeading, WhiteBlackWhite, MovesMenuAction, TtyMenu, TtyMenuAction}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object CargoConnect extends Runnable {

  Log.log("Start init of CargoConnect")

  val actions: Array[TtyMenuAction] = Array(
    MovesMenuAction("WarmUp", Seq(GyroArc.WarmUp, GyroDrive.WarmUp)),
/*    SortingCenterMenu,

    MovesMenuAction("WBW Test", Seq(
      WhiteBlackWhite.driveForwardToWhiteBlackWhite(0.degrees, Robot.fineSpeed, 500.millimeters),
      Robot.Hold
    )),
     */
    /*
    MovesMenuAction("Start-Park", SortingCenter.startToParkRoad),
    MovesMenuAction("Park-ShipRd", SortingCenter.parkRoadToShipRoad),

    MovesMenuAction("ShipR-ESlot", SortingCenter.shipRoadToEastSlot),
    MovesMenuAction("WSort-Blu", SortingCenter.deliverBlueFromWestSlot),
    MovesMenuAction("CSort-Blu", SortingCenter.deliverBlueFromCenterSlot),
    MovesMenuAction("ESort-Blu", SortingCenter.deliverBlueFromEastSlot),
    MovesMenuAction("Blu-to-Tr", SortingCenter.blueCircleToEastSlot),

    MovesMenuAction("SR-Ship", LoadShip.shipRoadToShip),
    MovesMenuAction("LoadShip", LoadShip.putContainersOnShip),
    MovesMenuAction("EscapeShip", LoadShip.escapeShip),
    MovesMenuAction("RaiseCrane", LoadShip.raiseCrane),
    MovesMenuAction("EscapeCrane", LoadShip.escapeCrane),
    */
    MovesMenuAction("Despin", Seq(DespinGyro)),
    MovesMenuAction("ColorCalibrate", Seq(CalibrateReflect)),
    MovesMenuAction("Zero", GyroSetHeading(0.degrees)),

    MovesMenuAction("Stop", Robot.Coast),
  )

  Log.log("Made menu actions")

  //todo add color sensors
  def setSensorRows(): Unit = {
    Lcd.set(0, s"${lcdView.elapsedTime}s", Lcd.RIGHT)

    val heading: String = UnpluggedException.safeString(() => s"${Robot.gyroscope.headingMode().readHeading().value}d")
    Lcd.set(0, heading, Lcd.LEFT)

    val forkDegrees = UnpluggedException.safeString(() => s"Fork ${Robot.forkMotor.readPosition().value}d")
    Lcd.set(1, forkDegrees, Lcd.LEFT)
  }
  val lcdView: Controller = Controller(actions, setSensorRows)

  override def run(): Unit = lcdView.run()

  def main(args: Array[String]): Unit = run()
}