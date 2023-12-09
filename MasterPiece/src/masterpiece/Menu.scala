package masterpiece

import ev3dev4s.Log
import ev3dev4s.actuators.{MotorPort, MotorStopCommand, Sound}
import ev3dev4s.lego.{Display, Motors}
import ev3dev4s.sensors.Ev3KeyPad

import java.lang.Runnable
import scala.Predef._
import scala.{List, Unit}
import scala.StringContext
import scala.annotation.tailrec

/**
 * The entrypoint from Ev3LangScala's JarRunner
 */
//noinspection ScalaUnusedSymbol
object Menu extends Runnable {

  val trips: List[Runnable] = List(
    PinkOrange,
    SceneChangeTrip,
    OldLadyOnABoat,
    RunToBlueHome,
    IzzyTrip,
    StuffToMuseum,
    Reload
  )

  var currentTrip: Runnable = trips.head

  override def run(): Unit = {
    recursiveRun()
  }

  @tailrec
  private final def recursiveRun():Unit = {
    showTrip()
    Log.log(s"waiting for key $currentTrip")
    waitForKey() match {

      case Ev3KeyPad.Key.Enter =>
        currentTrip.run()
        nextTrip()
      case Ev3KeyPad.Key.Down => nextTrip()
      case Ev3KeyPad.Key.Up => previousTrip()
      case _ => Log.log(s"No key??")
    }
    //release the lift motors
    Motors.setStopCommand(MotorPort.B, MotorStopCommand.COAST)
    Motors.stop(MotorPort.B)
    Motors.setStopCommand(MotorPort.D, MotorStopCommand.COAST)
    Motors.stop(MotorPort.D)

    if (!Reload.done) recursiveRun()
  }

  /**
   * A command to exit the recursiveRun()
   */
  object Reload extends Runnable{
    var done = false
    override def run(): Unit = {
      done = true
      Log.log(s"done is $done")
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

  def showTrip(): Unit = {
    Display.clearLcd()
    Display.write(PinkOrange.color, row = 3)
    Display.write(tripName(currentTrip), row=2)
  }

  def tripName(trip:Runnable): String ={
     trip.getClass.getSimpleName.take(currentTrip.getClass.getSimpleName.length - 1)
    }

  @tailrec
  def waitForKey(): Ev3KeyPad.Key = {
    val keyPressed: (Ev3KeyPad.Key, Ev3KeyPad.State) = Ev3KeyPad.blockUntilAnyKey()
    if(keyPressed._2 == Ev3KeyPad.State.Released) waitForKey()
    else keyPressed._1
  }
}