package masterpiece.superpowered

import ev3dev4s.Log
import ev3dev4s.actuators.Ev3Led
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.sensors.Ev3KeyPad

import java.lang.{Runnable, System}
import scala.Predef._
import scala.{Array, Boolean, Int, Seq, Unit, volatile}

/**
 * Copied from Ev3LangScalaExample as a starting point.
 *
 * @author
 * @since v0.0.0
 */
object DadMenu extends Runnable {

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
      val key: (Ev3KeyPad.Key, Ev3KeyPad.State) = Ev3KeyPad.blockUntilAnyKey()
      key match {
        case (Ev3KeyPad.Key.Enter, Ev3KeyPad.State.Released) => doAction()
        case (Ev3KeyPad.Key.Right, Ev3KeyPad.State.Released) => incrementSelected()
        case (Ev3KeyPad.Key.Left, Ev3KeyPad.State.Released) => decrementSelected()
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

  def incrementSelected(): Unit = {
    selectedAction = actions.span(_ != selectedAction)._2.tail.headOption.getOrElse(actions.head)
    drawScreen()
  }

  def decrementSelected(): Unit = {
    selectedAction = actions.span(_ != selectedAction)._1.lastOption.getOrElse(actions.last)
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
    Ev3Led.Left.writeOff()
    Ev3Led.Right.writeOff()
    Ev3Led.Left.writeGreen()
    Ev3Led.Right.writeGreen()
  }
}

object Red extends MenuAction {
  val label = "Red"

  override def run(): Unit = {
    Ev3Led.Left.writeOff()
    Ev3Led.Right.writeOff()
    Ev3Led.Left.writeRed()
    Ev3Led.Right.writeRed()
  }
}

object Yellow extends MenuAction {
  val label = "Yellow"

  override def run(): Unit = {
    Ev3Led.Left.writeOff()
    Ev3Led.Right.writeOff()
    Ev3Led.Left.writeYellow()
    Ev3Led.Right.writeYellow()
  }
}

object Off extends MenuAction {
  val label = "Off"

  override def run(): Unit = {
    Ev3Led.Left.writeOff()
    Ev3Led.Right.writeOff()
  }
}