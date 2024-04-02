package ev3dev4s.sensors

import ev3dev4s.os.Time
import ev3dev4s.Log
import ev3dev4s.measured.dimension.{Angle, milli}
import ev3dev4s.measured.dimension.Dimensions.{degree, second, `*`, given}
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
  extends MultiModeSensor(port,initialSensorDir.map(MultiModeSensorFS.Value01SensorFS.apply)){ //todo change to Value01SensorFS to support GYRO-G&A

  override def findGadgetFS(): Option[MultiModeSensorFS.Value01SensorFS] =
    SensorPortScanner.findGadgetDir(port,Ev3Gyroscope.driverName)
      .map(MultiModeSensorFS.Value01SensorFS.apply)

  private lazy val onlyHeadingMode: HeadingMode = HeadingMode()
  def headingMode():HeadingMode = setMaybeWriteMode(onlyHeadingMode)

  private lazy val onlyRateMode: RateMode = RateMode()
  def rateMode():RateMode = setMaybeWriteMode(onlyRateMode)

  private lazy val onlyHeadingAndRateMode = HeadingAndRateMode()
  def headingAndRateMode():HeadingAndRateMode = setMaybeWriteMode(onlyHeadingAndRateMode)

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
  sealed case class HeadingMode() extends Mode {
    val name = "GYRO-ANG"

    @volatile var offset: Angle = 0 * degree
    zero()

    /**
     * @return Angle (-32768 to 32767)
     */
    def readRawHeading(): Angle = this.synchronized {
      checkPort(_.readValue0Int()) * degree
    }

    def readHeading(): Angle = this.synchronized {
      readRawHeading() + offset
    }

    def zero(): Unit =
      setHeading(0 * degree)

    def setHeading(heading: Angle): Unit = this.synchronized {
      offset = heading - readRawHeading()
    }

    def unwind(): Unit = this.synchronized {
      offset = offset.normalized
    }
  }
  /**
   * Angle change rate in degrees per second
   */
  sealed case class RateMode() extends Mode {
    val name = "GYRO-RATE"

    def readRate(): Int = this.synchronized {
      checkPort(_.readValue0Int())
    }
  }

  /**
   * Heading in degrees and angle change rate in degrees per second
   */
  sealed case class HeadingAndRateMode() extends Mode {
    val name = "GYRO-G&A"

    def readHeading(): Angle = this.synchronized {
      checkPort(_.readValue0Int()) * degree
    }

    def readRate() = this.synchronized {
      checkPort(_.readValue1Int()) * degree / second
    }
  }

  /* todo
GYRO-CAL - does not work. Not sure what it does do, but it doesn't recalibrate the gyro

GYRO-RATE	Rotational Speed	d/s (degrees per second)	0	1	value0: Rotational Speed (-440 to 440) [22]
GYRO-FAS	Rotational Speed	none	0	1	value0: Rotational Speed (-1464 to 1535) [22]
GYRO-G&A [23]	Angle and Rotational Speed	none	0	2
value0: Angle (-32768 to 32767) [21] [22]

value1: Rotational Speed (-440 to 440) [22]


TILT-RATE [24]	Rotational Speed (2nd axis)	d/s (degrees per second)	0	1	value0: Rotational Speed (-440 to 440) [25]
TILT-ANG [24]	Angle (2nd axis)	deg (degrees)	0	1	value0: Angle (-32768 to 32767) [25]
 */

  import ev3dev4s.Ev3System
  val ledProgress: Seq[() => Any] = Seq(
    () => {
      Ev3System.leftLed.writeRed()
      Ev3System.rightLed.writeRed()
    },
    () => {
      Ev3System.leftLed.writeRed()
      Ev3System.rightLed.writeYellow()
    },
    () => {
      Ev3System.leftLed.writeYellow()
      Ev3System.rightLed.writeYellow()
    },
    () => {
      Ev3System.leftLed.writeGreen()
      Ev3System.rightLed.writeGreen()
    }
  )

  def despin(reportProgress: Seq[() => Any] = ledProgress):Unit = {
    def scanForPortDir():Path = {
      val legoPortsDir: File = new File("/sys/class/lego-port")
      Log.log(s"Scan $legoPortsDir for the right address")
      ArraySeq.unsafeWrapArray(legoPortsDir.listFiles()).map { (dir: File) =>
        //read the address to learn which port
        val addressPath = dir.toPath.resolve("address")
        val portChar: Char = ChannelRereader.readString(addressPath).last
        portChar -> dir.toPath
      }.toMap.get(port.name).getOrElse(throw UnpluggedException(port))
    }

    def restartSensorSoftBoot(legoPortDir:Path):Unit = {
      //write  "auto" to the right port in /sys/class/lego-port/port1/mode
      Log.log(s"Restart sensor in $legoPortDir by writing auto to mode")
      ChannelRewriter.writeString(legoPortDir.resolve("mode"), "auto")
      Time.pause(2 * second)
    }

    def scanForSensor(port:SensorPort):Boolean = {
      Log.log(s"Look for the sensor in $port for 20 seconds")
      val deadline = System.currentTimeMillis() + 20000L
      //some restarts don't come back give up and try again
      //todo this might be cleaner with @tailrec !
      var found = false
      while ({
        try {
          val maybeSensorPath: Option[Path] = SensorPortScanner.findGadgetDir(port, Ev3Gyroscope.driverName)
          Log.log(s"maybeSensorPath is $maybeSensorPath")
          maybeSensorPath.map { (sensorPath: Path) =>
            //read value0, which should have something when the gyro is ready
            val value0 = ChannelRereader.readString(sensorPath.resolve("value0"))
            Log.log(s"Read $value0 from $sensorPath")
            ChannelRewriter.writeString(sensorPath.resolve("mode"), "GYRO-ANG")
            Log.log(s"Wrote mode to $sensorPath")
            found = true
            false //success!
          }.getOrElse(System.currentTimeMillis() < deadline)
        } //or try again if there's time
        catch {
          case GadgetUnplugged(x) =>
            Log.log(s"Failed with $x")
            System.currentTimeMillis() < deadline //try again if there's time
          case x: UnpluggedException =>
            Log.log(s"Failed with $x")
            System.currentTimeMillis() < deadline // try again if there's time
        }
      }){Time.pause(500 * milli(second)) }
      found
    }

    val maybeOldMode = currentMode

    Log.log(s"despin $this at $port")
    unsetGadgetFS()

    while ({
      try {
        reportProgress(0)()
        val legoPortDir: Path = scanForPortDir()
        Log.log(s"legoPortDir is $legoPortDir")
        reportProgress(1)()
        restartSensorSoftBoot(legoPortDir)
        reportProgress(2)()
        !scanForSensor(port) //keep looking if no sensor found
      } catch {
        case GadgetUnplugged(_) => true
      }
    }){
      Time.pause()
    }

    maybeOldMode.map(setMaybeWriteMode(_))

    reportProgress(3)()

    Log.log(s"Successfully despun")
  }
}

object Ev3Gyroscope {
  val driverName = "lego-ev3-gyro"
}

  /* todo another way for despin to fail - a new kind of unplugged thing
1643172523163 Read 0 from /sys/class/lego-sensor/sensor4
java.io.IOException: Invalid argument
	at java.base/sun.nio.ch.FileDispatcherImpl.pwrite0(Native Method)
	at java.base/sun.nio.ch.FileDispatcherImpl.pwrite(FileDispatcherImpl.java:68)
	at java.base/sun.nio.ch.IOUtil.writeFromNativeBuffer(IOUtil.java:109)
	at java.base/sun.nio.ch.IOUtil.write(IOUtil.java:79)
	at java.base/sun.nio.ch.FileChannelImpl.writeInternal(FileChannelImpl.java:850)
	at java.base/sun.nio.ch.FileChannelImpl.write(FileChannelImpl.java:836)
	at ev3dev4s.sysfs.ChannelRewriter.writeString(ChannelRewriter.scala:25)
	at ev3dev4s.sysfs.ChannelRewriter$.writeString(ChannelRewriter.scala:40)
	at ev3dev4s.sensors.Ev3Gyroscope.scanForSensor$2$$anonfun$1(Ev3Gyroscope.scala:149)
	at scala.Option.map(Option.scala:242)
	at ev3dev4s.sensors.Ev3Gyroscope.scanForSensor$1(Ev3Gyroscope.scala:152)
	at ev3dev4s.sensors.Ev3Gyroscope.despin(Ev3Gyroscope.scala:179)

  */
