package ev3dev4s.lcd.examples

import ev3dev4s.lcd.{GraphicsLCD, Lcd}

/**
 * @author David Walend
 * @since v0.0.0
 */
object HelloLcd {

  def main(args: Array[String]): Unit = {
    val lcd: GraphicsLCD = Lcd.getInstance()

    //clear the LCD
    lcd.setColor(GraphicsLCD.WHITE)
    lcd.fillRect(0, 0, lcd.getWidth, lcd.getHeight)

    lcd.setColor(GraphicsLCD.BLACK)
    lcd.drawString("Hello World!",0,30,GraphicsLCD.LEFT+GraphicsLCD.BOTTOM)
    lcd.refresh()

    Thread.sleep(10000)
  }

}
