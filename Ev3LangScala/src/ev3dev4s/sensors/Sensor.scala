package ev3dev4s.sensors

import ev3dev4s.sysfs.ChannelRewriter

import java.nio.file.Path
import scala.reflect.ClassTag

/**
 * @author David Walend
 * @since v0.0.0
 */
trait Sensor extends AutoCloseable:
  def port: SensorPort

abstract class MultiModeSensor(sensorDir:Path) extends Sensor:

  private val modeWriter = ChannelRewriter(sensorDir.resolve("mode"))

  private def writeMode(mode: Mode):Mode = this.synchronized {
    modeWriter.writeString(mode.name)
    mode
  }

  private var mode:Option[Mode] = None

  def getCurrentMode:Option[Mode] = this.synchronized {
    mode
  }

  private[sensors] def getOrElseChangeMode[M <: Mode: ClassTag](create:() => M):M = this.synchronized {
    mode.collect{case m:M => m}.getOrElse{
      val toMode = create()
      mode.foreach(_.close())
      writeMode(toMode)
      toMode.init()
      mode = Option(toMode)
      toMode
    }
  }

  trait Mode extends AutoCloseable:
    def name:String

    private[sensors] def init():Unit

  override def close(): Unit = this.synchronized {
    modeWriter.close()
    mode.foreach(_.close())
  }

/*
 * On unplug fails with
 *
 * Exception in thread "Thread-0" java.io.IOException: No such device
	at java.base/sun.nio.ch.FileDispatcherImpl.pread0(Native Method)
	at java.base/sun.nio.ch.FileDispatcherImpl.pread(FileDispatcherImpl.java:54)
	at java.base/sun.nio.ch.IOUtil.readIntoNativeBuffer(IOUtil.java:274)
	at java.base/sun.nio.ch.IOUtil.read(IOUtil.java:245)
	at java.base/sun.nio.ch.FileChannelImpl.readInternal(FileChannelImpl.java:811)
	at java.base/sun.nio.ch.FileChannelImpl.read(FileChannelImpl.java:796)
	at ev3dev4s.sysfs.ChannelRereader.readBytes(ChannelRereader.scala:21)
	at ev3dev4s.sysfs.ChannelRereader.readString(ChannelRereader.scala:30)
	at ev3dev4s.sysfs.ChannelRereader.readAsciiInt(ChannelRereader.scala:40)
	at ev3dev4s.sensors.Ev3Gyroscope$HeadingMode.readRawHeading(Ev3Gyroscope.scala:46)

*
* Maybe provide a stream of ChannelRereaders and ChannelRewriters as a general solution ??
 */
