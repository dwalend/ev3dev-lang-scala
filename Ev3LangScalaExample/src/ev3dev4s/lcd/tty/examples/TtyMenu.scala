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
case class TtyMenu(actions:Array[_ <: TtyMenuAction]) extends Runnable {

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
        case (Ev3KeyPad.ENTER,Ev3KeyPad.RELEASED) => doAction()
        case (Ev3KeyPad.ESCAPE,Ev3KeyPad.RELEASED) => stopLoop()
        case (Ev3KeyPad.LEFT,Ev3KeyPad.RELEASED) => decrementMenu()
        case (Ev3KeyPad.RIGHT,Ev3KeyPad.RELEASED) => incrementMenu()
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

  def stopLoop():Unit = {
    keepGoing = false
  }

  def decrementMenu():Unit = {
    index = if (index == 0) actions.length -1
    else index - 1
    drawScreen()
  }

  def incrementMenu():Unit = {
    index = if (index == actions.length -1) 0
    else index + 1
    drawScreen()
  }

  def drawScreen(): Unit = this.synchronized{
    Lcd.clear()
    drawActionRow()

    Lcd.flush()
  }

  def drawActionRow(): Unit = {
    Lcd.set(2,actions(index).label,Lcd.CENTER)
    Lcd.set(2,0,'<')
    Lcd.set(2,10,'>')
  }

}

trait TtyMenuAction {
  def label:String = this.getClass.getSimpleName.dropRight(1)

  def run(menu: TtyMenu):Unit
}

object TtyMenu extends Runnable {
  def main(args: Array[String]): Unit = {
    run()
  }

  object Reload extends TtyMenuAction {
    override def run(menu: TtyMenu): Unit = menu.stopLoop()
  }

  override def run(): Unit = {
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
      Reload
    )
    TtyMenu(actions).run()
  }
}

case class LedAction(aLabel:String,action:() => Unit) extends TtyMenuAction {
  override def label: String = aLabel

  override def run(menu: TtyMenu): Unit = {
    action()
  }
}