package ev3dev4s.lcd

import com.sun.jna.LastErrorException
import com.sun.jna.ptr.IntByReference
import ev3dev4s.lcd.NativeConstants.KDGKBMODE
import ev3dev4s.lcd.NativeConstants.KDSETMODE
import ev3dev4s.lcd.NativeConstants.KDSKBMODE
import ev3dev4s.lcd.NativeConstants.VT_GETMODE
import ev3dev4s.lcd.NativeConstants.VT_GETSTATE
import ev3dev4s.lcd.NativeConstants.VT_RELDISP
import ev3dev4s.lcd.NativeConstants.VT_SETMODE

/**
 * Wrapper for basic actions on Linux VT/TTY
 *
 * @author Jakub Vaněk
 *
 * @param devicePath Path to TTY device.
 * @param flags Opening mode, e.g. read, write or both.
 */
final class NativeTTY(val devicePath: String, val flags: Int) extends AutoCloseable {

  import NativeTTYStructures.{vt_mode,vt_stat}
  val nativeFile = new NativeFile(devicePath, flags)

  /**
   * Get current TTY mode. TTY mode is mostly about VT switching.
   *
   * @return TTY mode.
   * @throws LastErrorException when the operation fails.
   */
  @throws[LastErrorException]
  def getVTmode: vt_mode = {
    val mode = new vt_mode
    nativeFile.ioctl(VT_GETMODE, mode.getPointer)
    mode.read()
    mode
  }

  /**
   * Set current TTY mode. TTY mode is mostly about VT switching.
   *
   * @param mode TTY mode.
   * @throws LastErrorException when the operation fails.
   */
  @throws[LastErrorException]
  def setVTmode(mode: vt_mode): Any = {
    mode.write()
    nativeFile.ioctl(VT_SETMODE, mode.getPointer)
  }

  /**
   * Get current TTY state.
   *
   * @return TTY state.
   * @throws LastErrorException when the operation fails.
   */
  @throws[LastErrorException]
  def getVTstate: vt_stat = {
    val stat = new vt_stat
    nativeFile.ioctl(VT_GETSTATE, stat.getPointer)
    stat.read()
    stat
  }

  /**
   * Get current keyboard mode.
   *
   * @return Keyboard mode (raw, transformed or off) - K_* constants.
   * @throws LastErrorException when the operation fails.
   */
  @throws[LastErrorException]
  def getKeyboardMode: Int = {
    val kbd = new IntByReference(0)
    nativeFile.ioctl(KDGKBMODE, kbd)
    kbd.getValue
  }

  /**
   * Set keyboard mode.
   *
   * @param mode Keyboard mode (raw, transformed or off) - K_* constants.
   * @throws LastErrorException when the operation fails.
   */
  @throws[LastErrorException]
  def setKeyboardMode(mode: Int): Int = nativeFile.ioctl(KDSKBMODE, mode)

  /**
   * Set console mode.
   *
   * @param mode Console mode - graphics or text mode - KD_* constants.
   * @throws LastErrorException when the operation fails.
   */
  @throws[LastErrorException]
  def setConsoleMode(mode: Int): Int = nativeFile.ioctl(KDSETMODE, mode)

  /**
   * Signal VT switch to the kernel.
   *
   * @param mode VT switching signal - VT_* constants.
   * @throws LastErrorException when the operation fails.
   */
  @throws[LastErrorException]
  def signalSwitch(mode: Int): Int = nativeFile.ioctl(VT_RELDISP, mode)

  private[lcd] def isOpen = nativeFile.isOpen

  override def close(): Unit = nativeFile.close()
}