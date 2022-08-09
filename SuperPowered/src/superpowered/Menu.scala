package superpowered

//todo change over to use lego package instead
//todo force import of things like Runnable
import ev3dev4s.{Ev3System, Log}
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.sensors.Ev3KeyPad

/**
 * Copied from Ev3LangScalaExample as a starting point.
 *
 * @author
 * @since v0.0.0
 */
//todo use an array of Runnables instead
case class Menu(actions:Array[_ <: MenuAction], setLcd:Menu => Unit) extends Runnable {

  @volatile var index = 0
  @volatile var keepGoing = true
  @volatile var doingAction = false

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
        case (Ev3KeyPad.Key.Right, Ev3KeyPad.State.Released) => incrementMenu()
        case (Ev3KeyPad.Key.Left, Ev3KeyPad.State.Released) => decrementMenu()
        case _ => ; //do nothing
      }
    }
    Log.log("end memu loop")
  } //run

  def doAction(): Unit = {
    doingAction = true
    actions(index).run(this)
    System.gc() //ha ha just wait 'til you learn about this!
    doingAction = false
    drawScreen()
  }

  def stopLoop(): Unit = {
    keepGoing = false
  }

  def incrementMenu(): Unit = {
    index = if (index == actions.length - 1) 0
    else index + 1
    drawScreen()
  }

  def decrementMenu(): Unit = {
    index = if (index == 0) actions.length - 1
            else index - 1
    drawScreen()
  }

  def drawScreen(): Unit = this.synchronized {
    Lcd.clear()
    setLcd(this)
    Lcd.flush()
  }

  def drawActionName(row: Int): Unit = {
    Lcd.set(row, actions(index).label, Lcd.CENTER)
    Lcd.set(row, 0, '<')
    Lcd.set(row, 10, '>')
  }
}

trait MenuAction {
  def label: String = this.getClass.getSimpleName.dropRight(1)

  def run(menu: Menu): Unit
}

object Menu extends Runnable {
  def main(args: Array[String]): Unit = {
    run()
  }

  object Reload extends MenuAction {
    override def run(ttyMenu: Menu): Unit = ttyMenu.stopLoop()
  }

  def drawLcd(ttyMenu: Menu): Unit = {
    ttyMenu.drawActionName(3)
  }

  override def run(): Unit = {
    val actions: Array[MenuAction] = Array(
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
    Menu(actions, drawLcd).run()
  }
}

//todo can this just be Runnable - or an extension of Runnable?
case class LedAction(aLabel:String,action:() => Unit) extends MenuAction {
  override def label: String = aLabel

  override def run(menu: Menu): Unit =
    action()
}