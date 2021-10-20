package ev3dev4s.lcd.tty.examples

import ev3dev4s.{Ev3System, Log}
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.sensors.{Ev3Gyroscope, Ev3KeyPad}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object TtySensorDisplay extends Runnable:
  def main(args: Array[String]): Unit =
    run()

  override def run(): Unit =
    val timeThread = new Thread(UpdateScreen)
    timeThread.setDaemon(true)
    timeThread.start()

    ttyMenu.run()

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
      FixGyro,
      TtyMenu.Reload
    )
    TtyMenu(actions, setLcd)

  def setLcd(ttyMenu: TtyMenu):Unit =
    ttyMenu.setActionRow(2)
    setSensorRows()

  var startTime = System.currentTimeMillis()
  def elapsedTime = (System.currentTimeMillis() - startTime)/1000

  val gyroscope:Ev3Gyroscope = Ev3System.portsToSensors.values.collectFirst{case g:Ev3Gyroscope => g}.get

  def setSensorRows():Unit =
    Lcd.set(0,s"${elapsedTime}s",Lcd.RIGHT)
    val heading = gyroscope.headingMode().readHeading()
    Lcd.set(0,s"${heading}d",Lcd.LEFT)

  object UpdateScreen extends Runnable:
    override def run(): Unit =
      while(true)
        if(!ttyMenu.doingAction) ttyMenu.drawScreen()
        Thread.sleep(500)

object FixGyro extends TtyMenuAction:

  val gyroscope:Ev3Gyroscope = Ev3System.portsToSensors.values.collectFirst{case g:Ev3Gyroscope => g}.get

  override def run(menu: TtyMenu): Unit = {
    Ev3System.rightLed.writeRed()
    Ev3System.leftLed.writeRed()
    gyroscope.calibrate()
    Ev3System.rightLed.writeGreen()
    Ev3System.leftLed.writeGreen()
  }