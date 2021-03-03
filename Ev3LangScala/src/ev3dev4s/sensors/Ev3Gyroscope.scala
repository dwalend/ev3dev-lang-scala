package ev3dev4s.sensors

import ev3dev4s.sysfs.{ChannelRereader, ChannelRewriter}

import java.nio.file.Path
import scala.reflect.ClassTag

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
case class Ev3Gyroscope(port:SensorPort, sensorDir:Path) extends Sensor {

  private val modeWriter = ChannelRewriter(sensorDir.resolve("mode"))

  //todo maybe writeMode should be on the write side of a ReadWriteLock - and all others can be on the Read side?
  private def writeMode(mode: Mode):Mode = this.synchronized {
    modeWriter.writeString(mode.name)
    mode
  }

  private var mode:Option[Mode] = None

  def getCurrentMode:Option[Mode] = this.synchronized {
    mode
  }

  private def getOrElseChangeMode[M <: Mode: ClassTag](create:() => M):M = this.synchronized{
    mode.collect{case m:M => m}.getOrElse{
      val toMode = create()
      mode.foreach(_.close())
      writeMode(toMode)
      toMode.init()
      mode = Option(toMode)
      toMode
    }
  }

  def headingMode():HeadingMode = {
    getOrElseChangeMode(HeadingMode.apply)
  }

  def calibrateMode():CalibrateMode = {
    getOrElseChangeMode(CalibrateMode.apply)
  }

  override def close(): Unit = this.synchronized {
    modeWriter.close()
    mode.foreach(_.close())
  }

  trait Mode extends AutoCloseable {
    def name:String

    def init():Unit
  }

  case class HeadingMode() extends Mode {
    val name = "GYRO-ANG"

    private lazy val headingReader = ChannelRereader(sensorDir.resolve("value0"))
    override def init():Unit = headingReader.path

    /**
     * GYRO-ANG	Angle	deg (degrees)	0	1	value0: Angle (-32768 to 32767)
     */
    def readHeading():Int = this.synchronized{
      headingReader.readAsciiInt()
    }

    override def close(): Unit = this.synchronized{
      headingReader.close()
    }
  }

  /**
   * GYRO-CAL	Calibration ???	none	0	4
   */
  case class CalibrateMode() extends Mode {
    override def name: String = "GYRO-CAL"

    override def init(): Unit = ()

    override def close(): Unit = ()
  }

  /* todo
GYRO-RATE	Rotational Speed	d/s (degrees per second)	0	1	value0: Rotational Speed (-440 to 440) [22]
GYRO-FAS	Rotational Speed	none	0	1	value0: Rotational Speed (-1464 to 1535) [22]
GYRO-G&A [23]	Angle and Rotational Speed	none	0	2
value0: Angle (-32768 to 32767) [21] [22]

value1: Rotational Speed (-440 to 440) [22]


TILT-RATE [24]	Rotational Speed (2nd axis)	d/s (degrees per second)	0	1	value0: Rotational Speed (-440 to 440) [25]
TILT-ANG [24]	Angle (2nd axis)	deg (degrees)	0	1	value0: Angle (-32768 to 32767) [25]
   */
}

object Ev3Gyroscope {
  val driverName = "lego-ev3-gyro"
}
