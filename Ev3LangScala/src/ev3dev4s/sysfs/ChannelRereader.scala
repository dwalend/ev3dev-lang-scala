package ev3dev4s.sysfs

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.{ClosedChannelException, FileChannel}
import java.nio.charset.StandardCharsets
import java.nio.file.Path

/**
 * Rereads the same channel for structured data of known length.
 *
 * @author David Walend
 */
case class ChannelRereader(path: Path, bufferLength: Int = 32) extends AutoCloseable:
  private val byteBuffer = ByteBuffer.allocate(bufferLength)
  @volatile private var channel = FileChannel.open(path)

  private def readBytes():Int = this.synchronized{
    byteBuffer.clear
    try
      channel.read(byteBuffer, 0)
    catch
      case _:ClosedChannelException =>
        channel = FileChannel.open(path)
        channel.read(byteBuffer, 0)
  }

  def readString(): String = this.synchronized {
    val n = readBytes()
    if (n == -1) || (n == 0) then ""
    else if n < -1 then throw new IOException("Unexpected read byte count of " + n + " while reading " + path)
    else
      val bytes = byteBuffer.array
      if bytes(n - 1) == '\n' then new String(bytes, 0, n - 1, StandardCharsets.UTF_8)
      else new String(bytes, 0, n, StandardCharsets.UTF_8)
  }

  def readAsciiInt(): Int = readString().toInt

  override def close(): Unit = this.synchronized {
    channel.close()
  }

object ChannelRereader:
  def readString(path: Path, bufferLength:Int = 32): String =
    val reader = ChannelRereader(path, bufferLength)
    try
      reader.readString()
    finally
      reader.close()

  def readAsciiInt(path: Path, bufferLength:Int = 32): Int =
    val reader = ChannelRereader(path, bufferLength)
    try
      reader.readAsciiInt()
    finally
      reader.close()


/*
ev3.replay.CalibrateGyro$@1353651 finished in 266 milliseconds
java.nio.channels.ClosedChannelException
	at java.base/sun.nio.ch.FileChannelImpl.ensureOpen(FileChannelImpl.java:150)
	at java.base/sun.nio.ch.FileChannelImpl.read(FileChannelImpl.java:790)
	at ev3dev4s.sysfs.ChannelRereader.readString(ChannelRereader.scala:21)
	at ev3dev4s.sysfs.ChannelRereader.readAsciiInt(ChannelRereader.scala:31)
	at ev3dev4s.sensors.Ev3Gyroscope$HeadingMode.readRawHeading(Ev3Gyroscope.scala:41)
	at ev3dev4s.sensors.Ev3Gyroscope$HeadingMode.readHeading(Ev3Gyroscope.scala:45)
	at ev3.replay.Robot$.readHeading(Robot.scala:94)
	at ev3.replay.WileyMenu.drawScreen(WileyMenu.scala:78)
	at ev3.replay.WileyMenu.run(WileyMenu.scala:31)
	at ev3.replay.Sorties$.run(Sorties.scala:43)
	at ev3dev4s.JarRunner$.main(JarRunner.scala:27)
	at ev3dev4s.JarRunner.main(JarRunner.scala)
*/