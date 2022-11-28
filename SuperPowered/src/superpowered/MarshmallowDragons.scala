package superpowered

import ev3dev4s.lego.{Display, Sound}
import ev3dev4s.sensors.Ev3KeyPad

import java.lang.Runnable
import scala.Predef._
import scala.{List, Seq, Unit}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object MarshmallowDragons extends Runnable {

  val trips: Seq[Runnable] = List(
    WindmillTrip,
    DinoRun,
    LastWorld
  )

  override def run(): Unit = {
    trips.foreach{trip =>
      Display.write(trip.getClass.getSimpleName,0)
      Display.write("Push Button",1)
      Sound.speak("watermelon choaclate " +
        "cherry never gonna give you up never gonna let you down never gonna run around and desert you")
      waitForKey()
      trip.run()
    }
  }

  def waitForKey():Unit = {
    while (Ev3KeyPad.blockUntilAnyKey()._2 != Ev3KeyPad.State.Released) {
      //don't do anything
    }
  }
}
