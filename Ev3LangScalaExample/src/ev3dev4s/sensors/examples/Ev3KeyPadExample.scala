package ev3dev4s.sensors.examples

import ev3dev4s.sensors.Ev3KeyPad

import scala.annotation.tailrec

/**
 *
 * @author David Walend
 */
object Ev3KeyPadExample {
  def main(args: Array[String]): Unit = {
    printButton()
  }

  @tailrec
  def printButton():Unit = {
    println(Ev3KeyPad.blockUntilAnyKey())
    printButton()
  }
}
