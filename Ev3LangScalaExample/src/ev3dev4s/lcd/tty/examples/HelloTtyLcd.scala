package ev3dev4s.lcd.tty.examples

import ev3dev4s.lcd.tty.Lcd

/**
 * @author David Walend
 * @since v0.0.0
 */
object HelloTtyLcd extends Runnable {

  def main(args: Array[String]): Unit = run()

  override def run(): Unit = {
    Lcd.clear()
    Lcd.set(0, 0, 'B')
    Lcd.set(1, 1, 'l')
    Lcd.set(2, 2, 'ä')
    Lcd.set(3, 3, 'r')
    Lcd.set(2, 4, 't')
    Lcd.set(1, 5, 'y')
    Lcd.set(0, 6, 'b')
    Lcd.set(1, 7, 'l')
    Lcd.set(2, 8, 'a')
    Lcd.set(3, 9, 'r')
    Lcd.set(2, 10, 't')
    Lcd.flush()
    Thread.sleep(5000)

    Lcd.clear()
    Lcd.set(0, "Blart?", Lcd.LEFT)
    Lcd.set(1, "Blårt!", Lcd.CENTER)
    Lcd.set(2, "Blärt?", Lcd.RIGHT)
    Lcd.set(3, "Blærty!", Lcd.CENTER)
    Lcd.flush()

    Thread.sleep(5000)
  }
}

