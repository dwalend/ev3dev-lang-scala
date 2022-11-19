package superpowered

import ev3dev4s.Log
import ev3dev4s.lego.{Display, Sound}
import ev3dev4s.sensors.Ev3KeyPad

import java.lang.Runnable
import scala.Predef._
import scala.{Array, Boolean, Int, List, Seq, Unit, volatile}
import scala.StringContext

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Menu extends Runnable {

  val trips: List[Runnable] = List(
    WindmillTrip,
    dino,
    LastWorld
  )

  var currentTrip = trips.head

  override def run(): Unit = {
    showTrip()
    waitForKey() match {

      case Ev3KeyPad.Key.Enter => currentTrip.run()
      case Ev3KeyPad.Key.Down => nextTrip()
      case Ev3KeyPad.Key.Up => previousTrip()
      case _ => Log.log(s"No key??")

    }


  }
  def nextTrip():Unit = {
    currentTrip = trips.span(_ != currentTrip)
      ._2 .tail.headOption.getOrElse(trips.head)
    showTrip()
  }

   def previousTrip():Unit = {
     currentTrip = trips.span(_ != currentTrip)
       ._1 .lastOption.getOrElse(trips.last)
     showTrip()
   }

  def showTrip() = {
    Display.clearLcd()
    Display.write(currentTrip.getClass.getSimpleName, row=2)
  }

  def waitForKey():Ev3KeyPad.Key = {
    var key: Ev3KeyPad.Key = null

    while ({
      val keyPressed: (Ev3KeyPad.Key, Ev3KeyPad.State) = Ev3KeyPad.blockUntilAnyKey()
      key = keyPressed._1

      Ev3KeyPad.blockUntilAnyKey()._2 != Ev3KeyPad.State.Released}){
      //don't do anything
    }
    Log.log(s"key is $key")
    key
  }
}
