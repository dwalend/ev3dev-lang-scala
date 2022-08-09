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
object Menu extends Runnable {

  val actions: Seq[Runnable] = Seq(
    Green,
    Yellow,
    Red,
    Off,
    Reload
  )

  @volatile var selectedAction: Runnable = actions.head
  @volatile var keepGoing: Boolean = true

  override def run(): Unit = {
    drawScreen()
    Green.run()

    Log.log("start menu loop")
    while (keepGoing) {
      val key: (Ev3KeyPad.Key, Ev3KeyPad.State) = Ev3System.keyPad.blockUntilAnyKey()
      key match {
        case (Ev3KeyPad.Key.Enter, Ev3KeyPad.State.Released) => doAction()
        case (Ev3KeyPad.Key.Right, Ev3KeyPad.State.Released) => incrementMenu()
        case (Ev3KeyPad.Key.Left, Ev3KeyPad.State.Released) => decrementMenu()
        case (Ev3KeyPad.Key.Escape, Ev3KeyPad.State.Released) => stopLoop()
        case _ => //do nothing
      }
    }
    Log.log("end menu loop")
  } //run

  def doAction(): Unit = {
    selectedAction.run()
    System.gc()
    drawScreen()
  }

  def stopLoop(): Unit = {
    keepGoing = false
  }

  def incrementMenu(): Unit = {
    selectedAction = if(selectedAction == actions.last) actions.head
                      else actions(actions.indexOf(selectedAction)+1)
    drawScreen()
  }

  def decrementMenu(): Unit = {
    selectedAction = if(selectedAction == actions.head) actions.last
                      else actions(actions.indexOf(selectedAction)-1)
    drawScreen()
  }

  def drawScreen(): Unit = {
    Lcd.clear()
    drawActionWidget(3)
    Lcd.flush()
  }

  def drawActionWidget(row: Int): Unit = {
    val label = selectedAction match {
      case menuAction: MenuAction => menuAction.label
      case runnable: Runnable => runnable.getClass.getSimpleName.dropRight(1)
    }

    Lcd.set(row, label, Lcd.CENTER)
    Lcd.set(row, 0, '<')
    Lcd.set(row, 10, '>')
  }

  object Reload extends MenuAction {
    val label = "Reload"

    override def run(): Unit = stopLoop()
  }

  def main(args: Array[String]): Unit = {
    run()
  }
}

trait MenuAction extends Runnable {
  /**
   * @return 9-or-fewer characters as the label for this menu action
   */
  def label: String
}

object Green extends MenuAction {
  val label = "Green"

  override def run(): Unit = {
    Ev3System.leftLed.writeGreen()
    Ev3System.rightLed.writeGreen()
  }
}

object Red extends MenuAction {
  val label = "Red"

  override def run(): Unit = {
    Ev3System.leftLed.writeRed()
    Ev3System.rightLed.writeRed()
  }
}

object Yellow extends MenuAction {
  val label = "Yellow"

  override def run(): Unit = {
    Ev3System.leftLed.writeYellow()
    Ev3System.rightLed.writeYellow()
  }
}

object Off extends MenuAction {
  val label = "Off"

  override def run(): Unit = {
    Ev3System.leftLed.writeOff()
    Ev3System.rightLed.writeOff()
  }
}