package ev3dev4s.lcd

import com.sun.jna.{LastErrorException, Pointer}
import ev3dev4s.Log
import ev3dev4s.lcd.NativeConstants.FBIOGET_CON2FBMAP
import ev3dev4s.lcd.NativeConstants.FBIOGET_FSCREENINFO
import ev3dev4s.lcd.NativeConstants.FBIOGET_VSCREENINFO
import ev3dev4s.lcd.NativeConstants.FBIOPUT_VSCREENINFO
import ev3dev4s.lcd.NativeConstants.MAP_SHARED
import ev3dev4s.lcd.NativeConstants.O_RDWR
import ev3dev4s.lcd.NativeConstants.PROT_READ
import ev3dev4s.lcd.NativeConstants.PROT_WRITE

/**
 * Linux framebuffer wrapper class
 *
 * @param devicePath path to the device
 * @param flags Opening mode, e.g. read, write or both.
 */
class NativeFramebuffer(devicePath:String, flags:Int = O_RDWR) extends AutoCloseable {

  Log.log(s"Start NativeFramebuffer $devicePath ")
  private val nativeFile:NativeFile = new NativeFile(devicePath,flags,0)
  Log.log(s"NativeFramebuffer $devicePath constructed")

  import NativeFramebufferStructures.{fb_fix_screeninfo,fb_var_screeninfo,fb_con2fbmap}

  /**
   * Fetch fixed screen info.
   *
   * @return Non-changing info about the display.
   */
  def getFixedScreenInfo: fb_fix_screeninfo = {
    val info = new fb_fix_screeninfo
    nativeFile.ioctl(FBIOGET_FSCREENINFO, info.getPointer)
    info.read()
    info
  }

  /**
   * Fetch variable screen info.
   *
   * @return Changeable info about the display.
   * @throws LastErrorException when operations fails
   */
  def getVariableScreenInfo: fb_var_screeninfo = {
    val info = new fb_var_screeninfo
    nativeFile.ioctl(FBIOGET_VSCREENINFO, info.getPointer)
    info.read()
    info
  }

  /**
   * Send variable screen info.
   *
   * @param info Changeable info about the display.
   * @throws LastErrorException when operations fails
   */
  def setVariableScreenInfo(info: fb_var_screeninfo): Int = {
    info.write()
    nativeFile.ioctl(FBIOPUT_VSCREENINFO, info.getPointer)
  }

  /**
   * Identify which framebuffer is connected to a specified VT.
   *
   * @param console VT number.
   * @return Framebuffer number or -1 if console has no framebuffer.
   * @throws LastErrorException when operations fails
   */
  def mapConsoleToFramebuffer(console: Int): Int = {
    val map = new fb_con2fbmap
    map.console = console
    map.write()
    nativeFile.ioctl(FBIOGET_CON2FBMAP, map.getPointer)
    map.read()
    map.framebuffer
  }

  /**
   * Map a portion of the device into memory and return a pointer which can be
   * used to read/write the device.
   *
   * @param len number of bytes to map
   * @return a pointer that can be used to access the device memory
   */
  def mmap(len: Long): Pointer = nativeFile.mmap(len, PROT_READ | PROT_WRITE, MAP_SHARED, 0)

  /**
   * Synchronize mapped memory region.
   *
   * @param addr  Mapped address.
   * @param len   Region length.
   * @param flags Synchronization flags
   * @throws LastErrorException when operations fails
   */
  def msync(addr: Pointer, len: Long, flags: Int): Int = nativeFile.msync(addr, len, flags)

  /**
   * Unmap mapped memory region.
   *
   * @param addr Mapped address.
   * @param len  Region length.
   * @throws LastErrorException when operations fails
   */
  def munmap(addr: Pointer, len: Long): Int = nativeFile.munmap(addr, len)

  override def close(): Unit = nativeFile.close()
}