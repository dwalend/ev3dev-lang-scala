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
   * Underlying fixed framebuffer info.
   */
    //todo only uses line_length and
  private val fixinfo: NativeFramebufferStructures.fb_fix_screeninfo = device.getFixedScreenInfo
  Log.log("JavaFramebuffer fixinfo")
  /**
   * Underlying variable framebuffer info.
   */
  private val varinfo: NativeFramebufferStructures.fb_var_screeninfo = device.getVariableScreenInfo
  Log.log("JavaFramebuffer varinfo")
  /**
   * Memory-mapped memory from Linux framebuffer device.
   */
  private val videomem: Pointer = device.mmap(getBufferSize)
  Log.log("JavaFramebuffer videomem")
  /**
   * Whether to enable display output.
   */
  private var flushEnabled = true
  /**
   * Framebuffer backup for VT switches.
   */
  private val backup = new Array[Byte](getBufferSize.toInt)
  Log.log("JavaFramebuffer backup")
  /**
   * Cache blank image.
   */
  private val blank:BufferedImage = {
    val willBeBlank = createCompatibleBuffer()
    val gfx = willBeBlank.createGraphics
    gfx.setColor(Color.WHITE)
    gfx.fillRect(0, 0, getWidth, getHeight)
    gfx.dispose()
    willBeBlank
  }

  varinfo.xres_virtual = varinfo.xres
  varinfo.yres_virtual = varinfo.yres
  varinfo.xoffset = 0
  varinfo.yoffset = 0
  device.setVariableScreenInfo(varinfo)
  Log.log("Opened JavaFramebuffer, mode " + varinfo.xres + "x" + varinfo.yres + "x" + varinfo.bits_per_pixel + "bpp")

  def close(): Unit = {
    Log.log("Closing LinuxFB")
    device.munmap(videomem, getBufferSize)
    device.close()
    display.releaseFramebuffer(this)
  }

  def getWidth: Int = varinfo.xres

  def getHeight: Int = varinfo.yres

  def getStride: Int = {
    Log.log(s"stride is ${fixinfo.line_length}")
    fixinfo.line_length
  }

  def createCompatibleBuffer(): BufferedImage = createCompatibleBuffer(getWidth, getHeight, getStride)

  def createCompatibleBuffer(width: Int, height: Int, stride: Int): BufferedImage = createCompatibleBuffer(width, height, stride, new Array[Byte](height * stride))

  def createCompatibleBuffer(width: Int, height: Int): BufferedImage = {
    val stride = 4 * width
    createCompatibleBuffer(width, height, stride, new Array[Byte](stride * height))
  }

  def createCompatibleBuffer(width: Int, height: Int, stride: Int, buffer: Array[Byte]): BufferedImage =
    ImageUtils.createXRGBImage(width, height, stride, getComponentOffsets, buffer)

  def flushScreen(compatible: BufferedImage): Unit = {
    if (flushEnabled) {
      videomem.write(0, ImageUtils.getImageBytes(compatible), 0, getBufferSize.toInt)
      device.msync(videomem, getBufferSize, NativeConstants.MS_SYNC)
    }
    else Log.log("Not drawing frame on framebuffer")
  }

  def setFlushEnabled(rly: Boolean): Unit = {
    flushEnabled = rly
  }

  def storeData(): Unit = {
    Log.log("Storing framebuffer snapshot")
    videomem.read(0, backup, 0, getBufferSize.toInt)
  }

  def restoreData(): Unit = {
    Log.log("Restoring framebuffer snapshot")
    videomem.write(0, backup, 0, getBufferSize.toInt)
    device.msync(videomem, getBufferSize, NativeConstants.MS_SYNC)
  }

  def clear(): Unit = {
    Log.log("Clearing framebuffer")
    flushScreen(blank)
  }

  def getDisplay: DisplayInterface = display

  /**
   * Get Linux framebuffer variable info.
   *
   * @return Variable information about the framebuffer.
   */
  def getVariableInfo: NativeFramebufferStructures.fb_var_screeninfo = varinfo

  /**
   * Get the underlying native device.
   *
   * @return Linux device
   */
  def getDevice: NativeFramebuffer = device

  /**
   * Get direct access to the video memory.
   *
   * @return JNA pointer to the framebuffer
   * @see LinuxFramebuffer#getBufferSize() for memory size.
   */
  def getMemory: Pointer = videomem

  /**
   * Get video memory size.
   *
   * @return Size of video memory in bytes.
   * @see LinuxFramebuffer#getMemory() for memory pointer.
   */
  def getBufferSize: Long = getHeight.toLong * getStride

  /**
   * get color offsets, use the not-used-one for alpha
   *
   * @return Offsets: { R, G, B, A }
   */
  private def getComponentOffsets = {
    val offsets = new Array[Int](4)
    offsets(0) = getVariableInfo.red.toLEByteOffset
    offsets(1) = getVariableInfo.green.toLEByteOffset
    offsets(2) = getVariableInfo.blue.toLEByteOffset
    val set = util.Arrays.asList(0, 1, 2, 3)
    val avail = new util.ArrayList[Int](set)
    avail.remove(offsets(0).asInstanceOf[Integer]) //todo really need these casts?
    avail.remove(offsets(1).asInstanceOf[Integer])
    avail.remove(offsets(2).asInstanceOf[Integer])
    offsets(3) = avail.get(0)
    offsets
  }
}