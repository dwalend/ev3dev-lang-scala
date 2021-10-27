package ev3dev4s.timing

import ev3dev4s.lcd.javaframebuffer.Lcd

/**
 * Run via
 *
 * brickrun -r -- java -cp Ev3LangScala.jar ev3dev4s.JarRunner Ev3LangScalaExample.jar ev3dev4s.timing.Simplest
 *
 * @author David Walend
 * @since v0.0.0
 */
object LCD extends Runnable:
  override def run(): Unit =
    //clear the LCD
    Lcd.setColor(Lcd.WHITE)
    Lcd.fillRect(0, 0, Lcd.getWidth, Lcd.getHeight)

    Lcd.setColor(Lcd.BLACK)
    Lcd.drawString("Hello World!",0,30,Lcd.LEFT+Lcd.BOTTOM)
    Lcd.refresh()
    System.exit(0)

  def main(args: Array[String]): Unit =
    run()
