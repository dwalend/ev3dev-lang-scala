package net.walend.cargoconnect

import ev3dev4s.{Ev3System, Log}
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.sensors.{Ev3Gyroscope, Ev3KeyPad}
import ev3dev4s.sysfs.UnpluggedException
import ev3dev4s.sensors.Ev3ColorSensor
import ev3dev4s.sensors.SensorPort

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object CargoConnect extends Runnable:
  def main(args: Array[String]): Unit =
    run()

  override def run(): Unit =
    val timeThread = new Thread(UpdateScreen)
    timeThread.setDaemon(true)
    timeThread.start()

    ttyMenu.loop()

    UpdateScreen.keepGoing = false

  val ttyMenu =
    val actions: Array[TtyMenuAction] = Array(
      LedAction("Green", { () =>
        Ev3System.leftLed.writeGreen()
        Ev3System.rightLed.writeGreen()
      }),
      LedAction("Yellow", { () =>
        Ev3System.leftLed.writeYellow()
        Ev3System.rightLed.writeYellow()
      }),
      LedAction("Red", { () =>
        Ev3System.leftLed.writeRed()
        Ev3System.rightLed.writeRed()
      }),
      DespinGyro,
      Reload
    )
    TtyMenu(actions, setLcd)

  def setLcd(ttyMenu: TtyMenu):Unit =
    ttyMenu.setActionRow(3)
    setSensorRows()

  var startTime = System.currentTimeMillis()
  def elapsedTime = (System.currentTimeMillis() - startTime)/1000

  //todo add color sensors
  def setSensorRows():Unit =
    Lcd.set(0,s"${elapsedTime}s",Lcd.RIGHT)
    val heading = UnpluggedException.safeString(() => s"${Robot.gyroscope.headingMode().readHeading()}d")
    Lcd.set(0,heading,Lcd.LEFT)

  object UpdateScreen extends Runnable:
    @volatile var keepGoing = true

    override def run(): Unit =
      while(keepGoing)
        if(!ttyMenu.doingAction) ttyMenu.drawScreen()
        Thread.sleep(500)

object DespinGyro extends TtyMenuAction:
  override def act(menu: TtyMenu): Unit =
    Robot.gyroscope.despin()

object Reload extends TtyMenuAction:
    override def act(ttyMenu: TtyMenu): Unit = ttyMenu.stopLoop()

case class LedAction(aLabel:String,action:() => Unit) extends TtyMenuAction {
  override def label: String = aLabel

  override def act(menu: TtyMenu): Unit =
    action()
}