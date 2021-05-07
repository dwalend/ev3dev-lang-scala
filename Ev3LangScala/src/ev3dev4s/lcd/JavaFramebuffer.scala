package ev3dev4s.lcd

import com.sun.jna.Pointer
import ev3dev4s.Log

import java.awt.image.BufferedImage
import java.util

import java.awt.Color

/**
 * Linux XRGB 32bpp framebuffer
 *
 * @param device      The framebuffer device (e.g. /dev/fb0)
 * @param display Display manager (e.g. /dev/tty)
 */
class JavaFramebuffer(val device: NativeFramebuffer, val display: DisplayInterface) {

  Log.log("JavaFramebuffer start")

  /**
   * Underlying variable framebuffer info.
   */
  private val varinfo: NativeFramebufferStructures.fb_var_screeninfo = device.getVariableScreenInfo
  Log.log("JavaFramebuffer varinfo")

  val width: Int = varinfo.xres  //178
  val height: Int = varinfo.yres //128

  val stride: Int = 712
  /**
   * Memory-mapped memory from Linux framebuffer device.
   */

  /**
   * Size of video memory in bytes.
   */
  val bufferSize: Long = height.toLong * stride

  private val videomem: Pointer = device.mmap(bufferSize)
  Log.log("JavaFramebuffer videomem")

  /**
   * JNA pointer to the framebuffer
   */
  def memory: Pointer = videomem

  /**
   * Whether to enable display output.
   */
  private var flushEnabled = true

  /**
   * Framebuffer backup for VT switches.
   */
  private val backup = new Array[Byte](bufferSize.toInt)
  Log.log("JavaFramebuffer backup")

  /**
   * Cache blank image.
   */
  private val blank:BufferedImage = {
    //converting this to bytes did not make a difference in start-up time, but did move things around
    //perhaps replacing Java's BufferedImage with something simpler can save 30 seconds, but stay with this for now
    val willBeBlank = createCompatibleBuffer()
    val gfx = willBeBlank.createGraphics
    gfx.setColor(Color.WHITE)
    gfx.fillRect(0, 0, width, height)
    gfx.dispose()
    willBeBlank
  }
  Log.log("JavaFramebuffer blank")

  //todo are these values different from what we just loaded??
  varinfo.xres_virtual = varinfo.xres
  varinfo.yres_virtual = varinfo.yres
  varinfo.xoffset = 0
  varinfo.yoffset = 0
  device.setVariableScreenInfo(varinfo)
  Log.log("Opened JavaFramebuffer, mode " + varinfo.xres + "x" + varinfo.yres + "x" + varinfo.bits_per_pixel + "bpp") //178x128x32bpp

  def close(): Unit = {
    Log.log("Closing LinuxFB")
    device.munmap(videomem, bufferSize)
    device.close()
    display.releaseFramebuffer(this)
  }

  def createCompatibleBuffer(): BufferedImage = createCompatibleBuffer(width, height, stride)

  def createCompatibleBuffer(width: Int, height: Int, stride: Int): BufferedImage = createCompatibleBuffer(width, height, stride, new Array[Byte](height * stride))

  def createCompatibleBuffer(width: Int, height: Int): BufferedImage = {
    val stride = 4 * width
    createCompatibleBuffer(width, height, stride, new Array[Byte](stride * height))
  }

  def createCompatibleBuffer(width: Int, height: Int, stride: Int, buffer: Array[Byte]): BufferedImage =
    ImageUtils.createXRGBImage(width, height, stride, getComponentOffsets, buffer)

  def flushScreen(compatible: BufferedImage): Unit = {
    if (flushEnabled) {
      videomem.write(0, ImageUtils.getImageBytes(compatible), 0, bufferSize.toInt)
      device.msync(videomem, bufferSize, NativeConstants.MS_SYNC)
    }
    else Log.log("Not drawing frame on framebuffer")
  }

  def setFlushEnabled(rly: Boolean): Unit = {
    flushEnabled = rly
  }

  def storeData(): Unit = {
    Log.log("Storing framebuffer snapshot")
    videomem.read(0, backup, 0, bufferSize.toInt)
  }

  def restoreData(): Unit = {
    Log.log("Restoring framebuffer snapshot")
    videomem.write(0, backup, 0, bufferSize.toInt)
    device.msync(videomem, bufferSize, NativeConstants.MS_SYNC)
  }

  def clear(): Unit = {
    Log.log("Clearing framebuffer")
    flushScreen(blank)
  }

  //todo maybe delete
  def getDisplay: DisplayInterface = display

  /**
   * get color offsets, use the not-used-one for alpha
   *
   * @return Offsets: { R, G, B, A }
   */
  private def getComponentOffsets = {
    val offsets = new Array[Int](4)
    offsets(0) = varinfo.red.toLEByteOffset  //todo these cna be hard-coded
    offsets(1) = varinfo.green.toLEByteOffset
    offsets(2) = varinfo.blue.toLEByteOffset
    val set = util.Arrays.asList(0, 1, 2, 3)
    val avail = new util.ArrayList[Int](set)
    avail.remove(offsets(0).asInstanceOf[Integer]) //todo really need these casts?
    avail.remove(offsets(1).asInstanceOf[Integer])
    avail.remove(offsets(2).asInstanceOf[Integer])
    offsets(3) = avail.get(0)
    offsets
  }
}