package ev3dev4s.actuators

import ev3dev4s.scala2measure.Conversions._
import ev3dev4s.scala2measure.LedIntensity
import ev3dev4s.sysfs.ChannelRewriter

import java.nio.file.Path

/**
 *
 *
 * @author David Walend
 */

sealed case class Ev3Led(side: Int) extends AutoCloseable {

  import ev3dev4s.actuators.Ev3Led.{Color, Off, Red, Green, Yellow} // brightest, darkest

  val rootName = "/sys/class"
  //noinspection SpellCheckingInspection
  val redName = s"leds/led$side:red:brick-status/brightness"
  //noinspection SpellCheckingInspection
  val greenName = s"leds/led$side:green:brick-status/brightness"

  private val redPath = Path.of(rootName, redName)
  private val greenPath = Path.of(rootName, greenName)

  private val redWriter: ChannelRewriter = ChannelRewriter(redPath)
  private val greenWriter: ChannelRewriter = ChannelRewriter(greenPath)
  //todo add readers to read brightness from the same paths maybe someday - it will work, not sure if it has any value

  def writeBrightness(red: LedIntensity, green: LedIntensity): Unit = this.synchronized {
    redWriter.writeAsciiInt(red.round)
    greenWriter.writeAsciiInt(green.round)
  }

  override def close(): Unit = this.synchronized {
    redWriter.close()
    greenWriter.close()
  }

  def writeColor(color: Color): Unit = {
    writeBrightness(color.red, color.green)
  }

  def writeOff(): Unit = writeColor(Off)

  def writeRed(): Unit = writeColor(Red)

  def writeGreen(): Unit = writeColor(Green)

  def writeYellow(): Unit = writeColor(Yellow)
}

object Ev3Led {
  val Left: Ev3Led = Ev3Led(0)
  val Right: Ev3Led = Ev3Led(1)

  val darkest: LedIntensity = 0.ledIntensity
  val brightest: LedIntensity = 255.ledIntensity

  case class Color(red: LedIntensity, green: LedIntensity)

  val Red: Color = Color(brightest, darkest)
  val Yellow: Color = Color(brightest, brightest)
  val Green: Color = Color(darkest, brightest)
  val Off: Color = Color(darkest, darkest)

  def writeBothGreen(): Unit = writeBothColor(Green)

  def writeBothRed(): Unit = writeBothColor(Red)

  def writeBothYellow(): Unit = writeBothColor(Yellow)

  def writeBothOff(): Unit = writeBothColor(Off)

  /**
   * Possible distinguishable values
   *
   * Green Green
   * Green Yellow
   * Green Red
   * Green Off
   * Yellow Yellow
   * Yellow Red
   * Yellow Off
   * */
  def writeBothColor(leftColor: Color, rightColor: Color): Unit = {
    Left.writeColor(leftColor)
    Right.writeColor(rightColor)
  }

  def writeBothColor(colors: (Color, Color)): Unit = {
    Left.writeColor(colors._1)
    Right.writeColor(colors._2)
  }

  def writeBothColor(color: Color): Unit = {
    writeBothColor(color, color)
  }
}
