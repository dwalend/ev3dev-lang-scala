package ev3dev4s.sensors

import ev3dev4s.os.Time
import ev3dev4s.{Ev3System, Log}
import ev3dev4s.sysfs.{ChannelRereader, ChannelRewriter, GadgetUnplugged, UnpluggedException}

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
  def calibrate():Unit = despin()

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

  val ledProgress: Seq[() => Any] = Seq(() =>
    Ev3System.leftLed.writeRed()
      Ev3System.rightLed.writeRed(),
    () =>
      Ev3System.leftLed.writeRed()
        Ev3System.rightLed.writeYellow(),
    () =>
      Ev3System.leftLed.writeYellow()
        Ev3System.rightLed.writeYellow(),
    () =>
      Ev3System.leftLed.writeGreen()
        Ev3System.rightLed.writeGreen()
  )

  /**
   *
   * @param reportProgress
   */
  def despin(reportProgress: Seq[() => Any] = ledProgress):Unit =
    def scanForPortDir():Path =
      val legoPortsDir:File = new File("/sys/class/lego-port")
      Log.log(s"Scan $legoPortsDir for the right address")
      ArraySeq.unsafeWrapArray(legoPortsDir.listFiles()).map { (dir: File) =>
        //read the address to learn which port
        val addressPath = dir.toPath.resolve("address")
        val portChar: Char = ChannelRereader.readString(addressPath).last
        (portChar -> dir.toPath)
      }.toMap.get(port.name).getOrElse(throw UnpluggedException(port))
    end scanForPortDir

    def restartSensorSoftBoot(legoPortDir:Path):Unit =
      //write  "auto" to the right port in /sys/class/lego-port/port1/mode
      Log.log(s"Restart sensor in $legoPortDir by writing auto to mode")
      ChannelRewriter.writeString(legoPortDir.resolve("mode"),"auto")
      Time.pause(2000L)
    end restartSensorSoftBoot

    def scanForSensor(port:SensorPort):Boolean =
      Log.log(s"Look for the sensor in $port for 20 seconds")

      val deadline = System.currentTimeMillis() + 20000L
      //some restarts don't come back give up and try again
      //todo this might be cleaner with @tailrec !
      var found = false
      while
        try
          val maybeSensorPath: Option[Path] = SensorPortScanner.findGadgetDir(port, Ev3Gyroscope.driverName)
          Log.log(s"maybeSensorPath is $maybeSensorPath")
          maybeSensorPath.map { (sensorPath: Path) =>
            //read value0, which should have something when the gyro is ready
            val value0 = ChannelRereader.readString(sensorPath.resolve("value0"))
            Log.log(s"Read $value0 from $sensorPath")
            //todo restore the original mode
            ChannelRewriter.writeString(sensorPath.resolve("mode"),"GYRO-ANG")
            Log.log(s"Wrote mode to $sensorPath")
            found = true
            false //success!
          }.getOrElse(System.currentTimeMillis() < deadline) //or try again if there's time
        catch
          case GadgetUnplugged(x) =>
            Log.log(s"Failed with $x")
            System.currentTimeMillis() < deadline //try again if there's time
          case x:UnpluggedException =>
            Log.log(s"Failed with $x")
            System.currentTimeMillis() < deadline // try again if there's time

      do
        Time.pause()
      found
    end scanForSensor

    Log.log(s"despin $this at $port")
    unsetGadgetFS()

    while
      try
        reportProgress(0)()
        val legoPortDir:Path = scanForPortDir()
        Log.log(s"legoPortDir is $legoPortDir")
        reportProgress(1)()
        restartSensorSoftBoot(legoPortDir)
        reportProgress(2)()
        !scanForSensor(port) //keep looking if no sensor found
      catch
        case GadgetUnplugged(x) => true
    do
      Time.pause()

    reportProgress(3)()
    val heading = headingMode().readHeading() //todo set it to the original mode
    Log.log(s"Successfully calibrated. Heading is $heading")
  end despin

object Ev3Gyroscope:
  val driverName = "lego-ev3-gyro"
