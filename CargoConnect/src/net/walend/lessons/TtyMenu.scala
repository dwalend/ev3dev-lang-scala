package net.walend.lessons

import ev3dev4s.actuators.{MotorPortScanner,MotorCommand,Ev3Led,Sound}
import ev3dev4s.{Ev3System, Log}
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.sensors.Ev3KeyPad
import ev3dev4s.measure.Conversions._

import scala.util.control.NonFatal

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
case class TtyMenu(actions:Array[_ <: TtyMenuAction],setLcd: TtyMenu => Unit) {

  @volatile var index = 0
  @volatile var keepGoing = true
  @volatile var doingAction = false

  def loop(): Unit = {

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
        case (Ev3KeyPad.Key.Escape, Ev3KeyPad.State.Pressed) => stopLoop()
        case (Ev3KeyPad.Key.Left, Ev3KeyPad.State.Pressed) => decrementMenu()
        case (Ev3KeyPad.Key.Right, Ev3KeyPad.State.Pressed) => incrementMenu()
        case _ => ;
      }
    }
    Log.log("end menu loop")
  }

  def doAction(): Unit = {
    try {
      doingAction = true
      actions(index).act(this)
    }
    catch {
      case NonFatal(x) =>
        x.printStackTrace()
        MotorPortScanner.scanMotors.values.foreach {
          motor => motor.writeCommand(MotorCommand.STOP)
        }
        Ev3Led.writeBothRed()
        Sound.playTone(440, 200.milliseconds)
        Sound.playTone(220, 200.milliseconds)
    }
    finally {
      System.gc()
      doingAction = false
      drawScreen()
    }
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
  }

  def setActionRow(row: Int = 2): Unit = {
    Lcd.set(row, actions(index).label, Lcd.CENTER)
    Lcd.set(row, 0, '<')
    Lcd.set(row, 10, '>')
  }
}

trait TtyMenuAction {
  def label: String = this.getClass.getSimpleName.dropRight(1).take(9) //Drop the $ and take the first 9 characters

  def act(menu: TtyMenu): Unit
}

case class MovesMenuAction(override val label:String,moves:Seq[Move]) extends TtyMenuAction {
  def act(menu: TtyMenu): Unit =
    moves.foreach { (move: Move) =>
      Log.log(s"Start $move")
      move.move()
      Log.log(s"Finished $move")
    }
}

object MovesMenuAction {
  def apply(label: String, move: Move): MovesMenuAction = MovesMenuAction(label, Seq(move))
}