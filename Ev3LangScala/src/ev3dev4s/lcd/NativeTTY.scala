package ev3dev4s.lcd

import com.sun.jna.LastErrorException
import com.sun.jna.Structure
import com.sun.jna.ptr.IntByReference
import java.util
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
 * @since 2.4.7
 */
object NativeTTY {

  /**
   * Info about an active VT.
   */
  class vt_stat() extends Structure(Structure.ALIGN_GNUC) {
    var v_active = 0
    /* active vt */
    var v_signal = 0
    /* signal to send */
    var v_state = 0

    override protected def getFieldOrder: util.List[String] = util.Arrays.asList("v_active", "v_signal", "v_state")
  }

  /**
   * Info about VT configuration.
   */
  class vt_mode() extends Structure(Structure.ALIGN_GNUC) {
    var mode = 0
    /* vt mode */
    var waitv = 0
    /* if set, hang on writes if not active */
    var relsig = 0
    /* signal to raise on release req */
    var acqsig = 0
    /* signal to raise on acquisition */
    var frsig = 0

    override protected def getFieldOrder: util.List[String] = util.Arrays.asList("mode", "waitv", "relsig", "acqsig", "frsig")
  }

}

/**
 * Initialize new TTY.
 *
 * @param dname Path to TTY device.
 * @param flags Opening mode, e.g. read, write or both.
 * @throws LastErrorException when the operation fails.
 */
final class NativeTTY(val dname: String, val flags: Int) extends AutoCloseable {
  val nativeFile = new NativeFile(dname, flags)

  /**
   * Get current TTY mode. TTY mode is mostly about VT switching.
   *
   * @return TTY mode.
   * @throws LastErrorException when the operation fails.
   */
  @throws[LastErrorException]
  def getVTmode: NativeTTY.vt_mode = {
    val mode = new NativeTTY.vt_mode()
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
  def setVTmode(mode: NativeTTY.vt_mode): Int = {
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
  def getVTstate: NativeTTY.vt_stat = {
    val stat = new NativeTTY.vt_stat()
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