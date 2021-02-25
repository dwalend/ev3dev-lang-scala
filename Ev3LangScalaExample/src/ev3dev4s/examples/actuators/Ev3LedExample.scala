package ev3dev4s.examples.actuators

import ev3dev4s.actuators.Ev3Led

/**
 *
 * @author David Walend
 */
object Ev3LedExample {
  def main(args: Array[String]): Unit = {
    Ev3Led.LEFT.writeOff()
    Ev3Led.RIGHT.writeOff()
    Thread.sleep(500)

    Ev3Led.LEFT.writeGreen()
    Ev3Led.RIGHT.writeRed()
    Thread.sleep(500)

    Ev3Led.LEFT.writeYellow()
    Ev3Led.RIGHT.writeYellow()
    Thread.sleep(500)

    Ev3Led.LEFT.writeRed()
    Ev3Led.RIGHT.writeGreen()
    Thread.sleep(500)

    for(_ <- 1 to 3) {
      for (i <- Ev3Led.LEFT.darkest to Ev3Led.LEFT.brightest) {
        Ev3Led.LEFT.writeBrightness(i, Ev3Led.LEFT.brightest - i)
        Ev3Led.RIGHT.writeBrightness(Ev3Led.LEFT.brightest - i, i)
        Thread.sleep(10)
      }
    }
    Ev3Led.LEFT.writeYellow()
    Ev3Led.RIGHT.writeYellow()
  }
}
