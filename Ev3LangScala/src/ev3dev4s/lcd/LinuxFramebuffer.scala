package ev3dev4s.lcd

import com.sun.jna.LastErrorException
import com.sun.jna.Pointer
import ev3dev4s.Log
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage

/**
 * <p>Linux Java2D framebuffer.</p>
 *
 * @param device      Framebuffer device (e.g. /dev/fb0)
 * @param display Display manager (e.g. /dev/tty)
 */
abstract class LinuxFramebuffer(val device: NativeFramebuffer,val display: DisplayInterface) extends JavaFramebuffer {

  /**
   * Underlying fixed framebuffer info.
   */
  private var fixinfo = device.getFixedScreenInfo
  /**
   * Underlying variable framebuffer info.
   */
  private var varinfo = device.getVariableScreenInfo
  /**
   * Memory-mapped memory from Linux framebuffer device.
   */
  private var videomem = device.mmap(getBufferSize)
  /**
   * Whether to enable display output.
   */
  private var flushEnabled = true
  /**
   * Framebuffer backup for VT switches.
   */
  private var backup = new Array[Byte](getBufferSize.toInt)
  /**
   * Cache blank image.
   */
  private var blank:BufferedImage = null
  /**
   * Whether to close the nativeframebuffer device when closing this framebuffer.
   */
  private var closeDevice = false

  setDeviceClose(false)
  varinfo.xres_virtual = varinfo.xres
  varinfo.yres_virtual = varinfo.yres
  varinfo.xoffset = 0
  varinfo.yoffset = 0
  device.setVariableScreenInfo(varinfo)
  blank = null
  Log.log("Opened LinuxFB, mode " + varinfo.xres + "x" + varinfo.yres + "x" + varinfo.bits_per_pixel + "bpp")


  @throws[LastErrorException]
  override def close(): Unit = {
    Log.log("Closing LinuxFB")
    if (videomem != null) device.munmap(videomem, getBufferSize)
    if (closeDevice && device != null) device.close()
    // free objects
    if (display != null) display.releaseFramebuffer(this)
//todo delete    display = null
    blank = null
//todo delete    device = null
    backup = null
    fixinfo = null
    varinfo = null
  }

  override def getWidth: Int = varinfo.xres

  override def getHeight: Int = varinfo.yres

  override def getStride: Int = fixinfo.line_length

  override def createCompatibleBuffer: BufferedImage = createCompatibleBuffer(getWidth, getHeight, getFixedInfo.line_length)

  override def createCompatibleBuffer(width: Int, height: Int):BufferedImage

  override def createCompatibleBuffer(width: Int, height: Int, stride: Int): BufferedImage = createCompatibleBuffer(width, height, stride, new Array[Byte](height * stride))

  override def createCompatibleBuffer(width: Int, height: Int, stride: Int, backed: Array[Byte]):BufferedImage

  override def flushScreen(compatible: BufferedImage): Unit = {
    if (flushEnabled) {
      videomem.write(0, ImageUtils.getImageBytes(compatible), 0, getBufferSize.toInt)
      device.msync(videomem, getBufferSize, NativeConstants.MS_SYNC)  //todo demiter
    }
    else Log.log("Not drawing frame on framebuffer")
  }

  override def setFlushEnabled(rly: Boolean): Unit = {
    flushEnabled = rly
  }

  override def storeData(): Unit = {
    Log.log("Storing framebuffer snapshot")
    videomem.read(0, backup, 0, getBufferSize.toInt)
  }

  override def restoreData(): Unit = {
    Log.log("Restoring framebuffer snapshot")
    videomem.write(0, backup, 0, getBufferSize.toInt)
    device.msync(videomem, getBufferSize, NativeConstants.MS_SYNC)
  }

  override def clear(): Unit = {
    Log.log("Clearing framebuffer")
    if (blank == null) {
      blank = createCompatibleBuffer
      val gfx = blank.createGraphics
      gfx.setColor(Color.WHITE)
      gfx.fillRect(0, 0, getWidth, getHeight)
      gfx.dispose()
    }
    flushScreen(blank)
  }

  override def getDisplay: DisplayInterface = display

  /**
   * Get Linux framebuffer fixed info.
   *
   * @return Fixed information about the framebuffer.
   */
  def getFixedInfo: NativeFramebuffer.fb_fix_screeninfo = fixinfo

  /**
   * Get Linux framebuffer variable info.
   *
   * @return Variable information about the framebuffer.
   */
  def getVariableInfo: NativeFramebuffer.fb_var_screeninfo = varinfo

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
   * Set whether to close the underlying device on exit.
   */
  protected def setDeviceClose(rly: Boolean): Unit = closeDevice = rly
}