package ev3dev4s.actuators

import ev3dev4s.sysfs.ChannelRewriter

import java.nio.file.Path
import ev3dev4s.measure.LedIntensity
import ev3dev4s.measure.Conversions.ledIntensity

/**
 *
 *
 * @author David Walend
 */

enum Ev3Led(side:Int) extends AutoCloseable:
  val rootName = "/sys/class"
  //noinspection SpellCheckingInspection
  val redName = s"leds/led$side:red:brick-status/brightness"
  //noinspection SpellCheckingInspection
  val greenName = s"leds/led$side:green:brick-status/brightness"

  private val redPath = Path.of(rootName,redName)
  private val greenPath = Path.of(rootName,greenName)

  private val redWriter = ChannelRewriter(redPath)
  private val greenWriter = ChannelRewriter(greenPath)
  //todo add readers to read brightness from the same paths maybe someday - it will work, not sure if it has any value

  def writeBrightness(red:LedIntensity, green:LedIntensity):Unit = this.synchronized {
    redWriter.writeAsciiInt(red.round)
    greenWriter.writeAsciiInt(green.round)
  }

  override def close(): Unit = this.synchronized {
    redWriter.close()
    greenWriter.close()
  }

  import Ev3Led.{brightest,darkest}
  def writeOff():Unit = writeBrightness(darkest,darkest)
  def writeRed():Unit = writeBrightness(brightest,darkest)
  def writeGreen():Unit = writeBrightness(darkest,brightest)
  def writeYellow():Unit = writeBrightness(brightest,brightest)

  case Left extends Ev3Led(0)
  case Right extends Ev3Led(1)


object Ev3Led:
  val darkest = 0.ledIntensity
  val brightest = 255.ledIntensity

  def writeBothGreen(): Unit =
    Left.writeGreen()
    Right.writeGreen()

  def writeBothRed(): Unit =
    Left.writeRed()
    Right.writeRed()

  def writeBothYellow(): Unit =
    Left.writeYellow()
    Right.writeYellow()
