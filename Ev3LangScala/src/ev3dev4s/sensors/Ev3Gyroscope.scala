package ev3dev4s.sensors

import ev3dev4s.sysfs.ChannelRereader

import java.nio.file.Path

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
case class Ev3Gyroscope(port:SensorPort,sensorDir:Path) extends MultiModeSensor(sensorDir):

  def headingMode():HeadingMode =
    getOrElseChangeMode(HeadingMode.apply)

  def rateMode():HeadingMode =
    getOrElseChangeMode(HeadingMode.apply)

  //todo calibrate for two seconds, then switch back to the previous mode. see https://github.com/ev3dev/ev3dev-lang-python/blob/f84152ca9b952a7a47a3f477542f878f3b69b824/ev3dev2/sensor/lego.py
  def calibrateMode():CalibrateMode =
    getOrElseChangeMode(CalibrateMode.apply)

  /**
   * Angle in degrees
   */
  case class HeadingMode() extends Mode:
    val name = "GYRO-ANG"

    @volatile var offset = 0

    private lazy val headingReader = ChannelRereader(sensorDir.resolve("value0"))
    override private[sensors] def init():Unit =
      headingReader.path
      zero()

    /**
     * @return Angle (-32768 to 32767)
     */
    def readRawHeading():Int = this.synchronized{
      headingReader.readAsciiInt()
    }

    def readHeading():Int = this.synchronized{
      readRawHeading() - offset
    }

    def zero():Unit = this.synchronized{
      offset = readRawHeading()
    }

    override def close(): Unit = this.synchronized{
      headingReader.close()
    }

  /**
   * Angle change rate in degrees per second
   */
  case class RateMode() extends Mode:
    val name = "GYRO-RATE"

    private lazy val rateReader = ChannelRereader(sensorDir.resolve("value0"))
    override private[sensors] def init():Unit =
      rateReader.path

    def readRate():Int = this.synchronized{
      rateReader.readAsciiInt()
    }

    override def close(): Unit = this.synchronized{
      rateReader.close()
    }

  //todo calibrate via GYRO-RATE, then GYRO-ANG as a separate method - if it works

  /**
   * Calibrate the gyroscope
   *
   * Note that it needs to sit in CAL for at least two seconds according to https://github.com/ev3dev/ev3dev-lang-python/blob/f84152ca9b952a7a47a3f477542f878f3b69b824/ev3dev2/sensor/lego.py
   */
  case class CalibrateMode() extends Mode:
    override def name: String = "GYRO-CAL"

    override def init(): Unit = ()

    override def close(): Unit = ()

  /* todo
GYRO-RATE	Rotational Speed	d/s (degrees per second)	0	1	value0: Rotational Speed (-440 to 440) [22]
GYRO-FAS	Rotational Speed	none	0	1	value0: Rotational Speed (-1464 to 1535) [22]
GYRO-G&A [23]	Angle and Rotational Speed	none	0	2
value0: Angle (-32768 to 32767) [21] [22]

value1: Rotational Speed (-440 to 440) [22]


TILT-RATE [24]	Rotational Speed (2nd axis)	d/s (degrees per second)	0	1	value0: Rotational Speed (-440 to 440) [25]
TILT-ANG [24]	Angle (2nd axis)	deg (degrees)	0	1	value0: Angle (-32768 to 32767) [25]
   */

object Ev3Gyroscope:
  val driverName = "lego-ev3-gyro"
