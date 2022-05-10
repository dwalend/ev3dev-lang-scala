package net.walend.lessons

import ev3dev4s.actuators.Sound
import ev3dev4s.{Ev3System, Log}
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.sensors.{Ev3Gyroscope, Ev3KeyPad}
import ev3dev4s.sysfs.UnpluggedException
import ev3dev4s.sensors.Ev3ColorSensor
import ev3dev4s.sensors.SensorPort
import ev3dev4s.measure.Conversions.*
import net.walend.cargoconnect.Robot

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
case class Controller(actions:Array[TtyMenuAction],setSensorRows:() => Unit) extends Runnable:

  override def run(): Unit =
    Robot.check()
    val timeThread = new Thread(UpdateScreen)
    timeThread.setDaemon(true)
    timeThread.start()

    Sound.beep()
    ttyMenu.loop()

    UpdateScreen.keepGoing = false

  val ttyMenu: TtyMenu = TtyMenu(actions:+Reload, setLcd)

  def setLcd(ttyMenu: TtyMenu):Unit =
    ttyMenu.setActionRow(3)
    setSensorRows()

  var startTime: Long = System.currentTimeMillis()
  def elapsedTime: Long = (System.currentTimeMillis() - startTime)/1000

  object UpdateScreen extends Runnable:
    @volatile var keepGoing = true

    override def run(): Unit =
      while(keepGoing)
        if(!ttyMenu.doingAction) ttyMenu.drawScreen()
        Thread.sleep(500)

  object Reload extends TtyMenuAction:
    override def act(ttyMenu: TtyMenu): Unit = ttyMenu.stopLoop()

