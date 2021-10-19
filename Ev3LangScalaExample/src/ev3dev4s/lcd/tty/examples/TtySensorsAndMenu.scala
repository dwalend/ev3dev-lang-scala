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

  object Reload extends TtyMenuAction:
    override def run(ttyMenu: TtyMenu): Unit = ttyMenu.stopLoop()

  override def run(): Unit =
    val actions: Array[TtyMenuAction] = Array(
      LedAction("Green",{() =>
        Ev3System.leftLed.writeGreen()
        Ev3System.rightLed.writeGreen()
      }),
      LedAction("Yellow",{() =>
        Ev3System.leftLed.writeYellow()
        Ev3System.rightLed.writeYellow()
      }),
      LedAction("Red",{() =>
        Ev3System.leftLed.writeRed()
        Ev3System.rightLed.writeRed()
      }),
      TtyMenu.Reload,
      FixGyro
    )
    TtyMenu(actions,setLcd).run()

  def setLcd(ttyMenu: TtyMenu):Unit =
    ttyMenu.setActionRow(2)
    setSensorRows()

  val gyroscope:Ev3Gyroscope = Ev3System.portsToSensors.values.collectFirst{case g:Ev3Gyroscope => g}.get
  def setSensorRows():Unit =
    val heading = gyroscope.headingMode().readHeading()
    Lcd.set(1,heading.toString,Lcd.CENTER)

object FixGyro extends TtyMenuAction:

  val gyroscope:Ev3Gyroscope = Ev3System.portsToSensors.values.collectFirst{case g:Ev3Gyroscope => g}.get

  override def run(menu: TtyMenu): Unit = {
    Ev3System.rightLed.writeRed()
    Ev3System.leftLed.writeRed()
    gyroscope.calibrate()
    Ev3System.rightLed.writeGreen()
    Ev3System.leftLed.writeGreen()
  }