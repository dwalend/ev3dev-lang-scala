package ev3dev4s.actuators

import ev3dev4s.sysfs.ChannelRewriter

import java.nio.file.Path

/**
 *
 *
 * @author David Walend
 */
//todo Scala 3 use an enum
object Ev3Led {

  lazy val LEFT: Ev3Led = Ev3Led(0)
  lazy val RIGHT: Ev3Led = Ev3Led(1)
}

sealed case class Ev3Led(side:Int) extends AutoCloseable {

  //    /leds/led0:red:brick-status/brightness
  val rootName = "/sys/class"
  val redName = s"leds/led$side:red:brick-status/brightness"
  val greenName = s"leds/led$side:green:brick-status/brightness"

  private val redPath = Path.of(rootName,redName)
  private val greenPath = Path.of(rootName,greenName)

  private val redWriter = ChannelRewriter(redPath)
  private val greenWriter = ChannelRewriter(greenPath)

  def writeBrightness(red:Int,green:Int):Unit = this.synchronized {
    redWriter.writeAsciiInt(red)
    greenWriter.writeAsciiInt(green)
  }

  override def close(): Unit = this.synchronized {
    redWriter.close()
    greenWriter.close()
  }

  val darkest = 0
  val brightest = 255 //todo double-check value of max_brightness

  def writeOff():Unit = writeBrightness(darkest,darkest)
  def writeRed():Unit = writeBrightness(brightest,darkest)
  def writeGreen():Unit = writeBrightness(darkest,brightest)
  def writeYellow():Unit = writeBrightness(brightest,brightest)
}