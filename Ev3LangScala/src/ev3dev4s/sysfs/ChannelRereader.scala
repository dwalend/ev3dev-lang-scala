package ev3dev4s.sysfs

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets
import java.nio.file.Path

/**
 *
 * Rereads the same channel for structured data of known length.
 *
 * @author David Walend
 */
case class ChannelRereader(path: Path, bufferLength: Int = 32) extends AutoCloseable {
  private val byteBuffer = ByteBuffer.allocate(bufferLength)
  private val channel = FileChannel.open(path)

  def readString(): String = this.synchronized {
    byteBuffer.clear
    val n = channel.read(byteBuffer, 0)
    if ((n == -1) || (n == 0)) ""
    else if (n < -1) throw new IOException("Unexpected read byte count of " + n + " while reading " + path)
    else {
      val bytes = byteBuffer.array
      if (bytes(n - 1) == '\n') new String(bytes, 0, n - 1, StandardCharsets.UTF_8)
      else new String(bytes, 0, n, StandardCharsets.UTF_8)
    }
  }

  def readAsciiInt(): Int = readString().toInt

  override def close(): Unit = this.synchronized {
    channel.close()
  }
}