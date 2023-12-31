package ev3dev4s.lcd.tty.examples

import ev3dev4s.{Ev3System, Log}
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.sensors.Ev3KeyPad

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
case class TtyMenu(actions:Array[_ <: TtyMenuAction],setLcd:TtyMenu => Unit) extends Runnable {

  @volatile var index = 0
  @volatile var keepGoing = true
  @volatile var doingAction = false
  @volatile var drawCount = 0

  override def run(): Unit = {

    drawScreen()
    Ev3System.leftLed.writeOff()
    Ev3System.rightLed.writeOff()
    Ev3System.leftLed.writeGreen()
    Ev3System.rightLed.writeGreen()

    Log.log("start menu loop")
    while (keepGoing) {
      val key: (Ev3KeyPad.Key, Ev3KeyPad.State) = Ev3System.keyPad.blockUntilAnyKey()
      key match {
        case (Ev3KeyPad.Key.Enter, Ev3KeyPad.State.Released) => doAction()
        case (Ev3KeyPad.Key.Escape, Ev3KeyPad.State.Released) => stopLoop()
        case (Ev3KeyPad.Key.Left, Ev3KeyPad.State.Released) => decrementMenu()
        case (Ev3KeyPad.Key.Right, Ev3KeyPad.State.Released) => incrementMenu()
        case _ => ;
      }
    }
    Log.log("end memu loop")
  }

  def doAction(): Unit = {
    doingAction = true
    actions(index).run(this)
    System.gc()
    doingAction = false
    drawScreen()
  }

  def stopLoop(): Unit =
    keepGoing = false

  def decrementMenu(): Unit = {
    index = if (index == 0) actions.length - 1
    else index - 1
    drawScreen()
  }

  def incrementMenu(): Unit = {
    index = if (index == actions.length - 1) 0
    else index + 1
    drawScreen()
  }

  def drawScreen(): Unit = this.synchronized {
    Lcd.clear()
    setLcd(this)
    Lcd.flush()
    drawCount = drawCount + 1
  }

  def setActionRow(row: Int): Unit = {
    Lcd.set(row, actions(index).label, Lcd.CENTER)
    Lcd.set(row, 0, '<')
    Lcd.set(row, 10, '>')
  }
}


trait TtyMenuAction {
  def label: String = this.getClass.getSimpleName.dropRight(1)

  def run(menu: TtyMenu): Unit
}

object TtyMenu extends Runnable {
  def main(args: Array[String]): Unit = {
    run()
  }

  object Reload extends TtyMenuAction {
    override def run(ttyMenu: TtyMenu): Unit = ttyMenu.stopLoop()
  }

  def setLcd(ttyMenu: TtyMenu): Unit = {
    ttyMenu.setActionRow(2)
    Lcd.set(3, ttyMenu.drawCount.toString, Lcd.CENTER)
  }

  override def run(): Unit = {
    val actions: Array[TtyMenuAction] = Array(
      LedAction("Green", {
        () =>
          Ev3System.leftLed.writeGreen()
          Ev3System.rightLed.writeGreen()
      }),
      LedAction("Yellow", {
        () =>
          Ev3System.leftLed.writeYellow()
          Ev3System.rightLed.writeYellow()
      }),
      LedAction("Red", {
        () =>
          Ev3System.leftLed.writeRed()
          Ev3System.rightLed.writeRed()
      }),
      Reload
    )
    TtyMenu(actions, setLcd).run()
  }
}

case class LedAction(aLabel:String,action:() => Unit) extends TtyMenuAction {
  override def label: String = aLabel

  override def run(menu: TtyMenu): Unit =
    action()
}