package ev3dev4s.sensors

import ev3dev4s.Ev3System
import ev3dev4s.sysfs.{ChannelRereader, ChannelRewriter, UnpluggedException}

import java.io.File
import java.nio.file.Path
import scala.collection.immutable.ArraySeq

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
case class Ev3Gyroscope(override val port:SensorPort,initialSensorDir:Option[Path])
  extends MultiModeSensor(port,initialSensorDir.map(MultiModeSensorFS.Value0SensorFS(_))): //todo change to Value01SensorFS to support GYRO-G&A

  override def findGadgetFS(): Option[MultiModeSensorFS.Value0SensorFS] =
    SensorPortScanner.findGadgetDir(port,Ev3Gyroscope.driverName)
      .map(MultiModeSensorFS.Value0SensorFS(_))

  private lazy val onlyHeadingMode = HeadingMode()
  def headingMode():HeadingMode =
    setMaybeWriteMode(onlyHeadingMode)

  private lazy val onlyRateMode = RateMode()
  def rateMode():RateMode =
    setMaybeWriteMode(onlyRateMode)

  /**
   * Simulate unplugging then plugging in the gyroscope - in software
   * to recalibrate it and stop it spinning
   *
   * @see https://www.aposteriori.com.sg/ev3dev-research-notes/
   * @see https://github.com/ev3dev/ev3dev/issues/1387
   */
  def calibrate():Unit =
    val foundMode: Option[Mode] = currentMode

    Ev3System.leftLed.writeRed()
    Ev3System.rightLed.writeRed()

    //scan the /sys/class/lego-port/port*/address ('ev3-ports:in2' format)  for the right SensorPort ending
    val legoPortsDir:File = new File("/sys/class/lego-port")
    val legoPortDir:Path = ArraySeq.unsafeWrapArray(legoPortsDir.listFiles()).map { (dir: File) =>
      //read the address to learn which port
      val addressPath = dir.toPath.resolve("address")
      val portChar: Char = ChannelRereader.readString(addressPath).last
      (portChar -> dir.toPath)
    }.toMap.get(port.name).getOrElse(throw UnpluggedException(port))

    //todo maybe : https://www.aposteriori.com.sg/ev3dev-research-notes/ tries this 10 times with an awkward timeout

    //write  "auto" to the right port in /sys/class/lego-port/port1/mode
    val writer: ChannelRewriter = ChannelRewriter(legoPortDir.resolve("mode"))
    writer.writeString("auto")
    writer.close

    //sensor should appear unplugged, but timing is tricky
    Thread.sleep(1000)
    //sleepy loop for the other side of unplugged

    Ev3System.leftLed.writeYellow()
    Ev3System.rightLed.writeYellow()

    while(SensorPortScanner.findGadgetDir(port,Ev3Gyroscope.driverName).isEmpty) do
      System.gc()
      Thread.sleep(200)

    //set the mode back to what it was before
    foundMode.map{m => setMaybeWriteMode(m)}

    Ev3System.leftLed.writeGreen()
    Ev3System.rightLed.writeGreen()

  /**
   * Angle in degrees
   */
  case class HeadingMode() extends Mode:
    val name = "GYRO-ANG"

    @volatile var offset = 0
    zero()

    /**
     * @return Angle (-32768 to 32767)
     */
    def readRawHeading():Int = this.synchronized{
      checkPort(_.readValue0Int())
    }

    def readHeading():Int = this.synchronized{
      readRawHeading() - offset
    }

    def zero():Unit = this.synchronized{
      offset = readRawHeading()
    }

  /**
   * Angle change rate in degrees per second
   */
  case class RateMode() extends Mode:
    val name = "GYRO-RATE"

    def readRate():Int = this.synchronized{
      checkPort(_.readValue0Int())
    }

  /* todo
GYRO-CAL - does not work. Not sure what does do, but it doesn't recalibrate the gyro

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
