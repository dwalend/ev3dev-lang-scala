package ev3dev4s.sysfs

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.charset.StandardCharsets
import java.nio.file.{AccessDeniedException,Path,StandardOpenOption}
import scala.annotation.tailrec

/**
 *
 * Rewrites the same channel for structured data of known length.
 *
 * @author David Walend
 */
case class ChannelRewriter(path: Path,bufferLength:Int = 32) extends AutoCloseable {

  @tailrec
  private def keepTryingFileChannel(path: Path):FileChannel =

    try
      FileChannel.open(path,StandardOpenOption.WRITE)
    catch
      case x:AccessDeniedException =>
        val openRetryDelay = 10

        println(s"${x.getClass.getSimpleName} while opening $path. Will retry in $openRetryDelay ms")
        Thread.sleep(openRetryDelay)
        keepTryingFileChannel(path)
  private val channel = keepTryingFileChannel(path)
  private val byteBuffer = ByteBuffer.allocate(bufferLength)

  def writeString(string: String):Unit = this.synchronized{
    byteBuffer.clear ()
    byteBuffer.put (string.getBytes (StandardCharsets.UTF_8) )
    byteBuffer.flip ()
    channel.truncate (0)
    channel.write (byteBuffer, 0)
    channel.force (false)
  }

  def writeAsciiInt(i: Int):Unit = writeString(Integer.toString(i))

  def close():Unit = this.synchronized{
    channel.close()
  }
}