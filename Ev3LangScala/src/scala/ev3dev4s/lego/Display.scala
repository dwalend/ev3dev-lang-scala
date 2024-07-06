package ev3dev4s.lego

import ev3dev4s.actuators.Ev3Led
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.scala2measure.LedIntensity

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Display{

  sealed case class LedColor(redIntensity: LedIntensity, greenIntensity: LedIntensity)

  object LedColor {
    val Red: LedColor = LedColor(Ev3Led.brightest,Ev3Led.darkest)
    val Green: LedColor = LedColor(Ev3Led.darkest,Ev3Led.brightest)
    val Orange: LedColor = LedColor(Ev3Led.brightest,Ev3Led.brightest)
    val Off:LedColor = LedColor(Ev3Led.darkest,Ev3Led.darkest)
  }
  def setLedsTo(color:LedColor): Unit = setLedsTo(color,color)

  def setLedsTo(left:LedColor,right:LedColor): Unit = {
    Ev3Led.Left.writeBrightness(left.redIntensity,left.greenIntensity)
    Ev3Led.Right.writeBrightness(right.redIntensity,right.greenIntensity)
  }

  def clearLcd(): Unit = {
    Lcd.clear()
    Lcd.flush()
  }

  def write(string: String, row: Int): Unit = {
    Lcd.set(row, string)
    Lcd.flush()
  }

  def write(string: String, row: Int, column:Int): Unit = {
    Lcd.set(row, column, string)
    Lcd.flush()
  }
}
