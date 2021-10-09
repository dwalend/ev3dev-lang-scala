package ev3dev4s.lcd.javaframebuffer

import com.sun.jna.LastErrorException
import ev3dev4s.Log

import java.io.IOException
import NativeConstants.KD_GRAPHICS
import NativeConstants.KD_TEXT
import NativeConstants.K_OFF
import NativeConstants.O_RDWR
import NativeConstants.SIGUSR2
import NativeConstants.VT_AUTO
import NativeConstants.VT_PROCESS
import NativeTTYStructures.vt_mode

/**
 * <p>System console manager.</p>
 *
 * <p>Manages the output mode of the display. It is possible
 * to switch between graphics mode and text mode. Graphics mode reserves
 * the display for drawing operation and hides text output. On the
 * other hand, text mode suspends drawing operations and shows the text
 * on the Linux console.
 * This class also manages VT (= Virtual Terminal = console) switches
 * in the case of a VT switch occuring when graphics mode is set.</p>
 *
 * <p>Implementation of this class is based on the GRX3 linuxfb plugin.</p>
 *
 * @author Jakub Vaněk
 */
class OwnedDisplay() extends DisplayInterface {
  Log.log("Opening TTY")
  private val ttyfd:NativeTTY = new NativeTTY("/dev/tty", O_RDWR)

  Log.log("Getting activeVT")
  private val activeVT = ttyfd.getVTstate.v_active

  Log.log("Getting old keyboard mode")
  private val old_kbmode: Int = ttyfd.getKeyboardMode

  Log.log("Opening FB 0")
  //todo this is making an extra NativeFramebuffer or two , just to get the path to the frame buffer
  private val framebuffer0 = new NativeFramebuffer("/dev/fb0")
  private val fbn = framebuffer0.mapConsoleToFramebuffer(activeVT) //todo make this a static call - make the right frame buffer the first time - cut that to just one call

  Log.log("map vt" + activeVT + " -> fb " + fbn)
  if fbn < 0 then
    Log.log("No framebuffer for current TTY")
    throw new IOException("No framebuffer device for the current VT")
  private val fbPath: String = "/dev/fb" + fbn
  private val fbfd: NativeFramebuffer = if fbn != 0 then
    Log.log("Redirected to FB " + fbn)
    framebuffer0.close()
    new NativeFramebuffer(fbPath)
  else framebuffer0
  fbfd.close()

  Log.log("add deinitializer")
  val deinitializer:Thread = new Thread(() => deinitialize(), "console restore")
  Runtime.getRuntime.addShutdownHook(deinitializer)
  switchToTextMode()

  override def close(): Unit = if ttyfd.isOpen then
    deinitialize()
    Runtime.getRuntime.removeShutdownHook(deinitializer)

  /**
   * <p>Put the display to a state where it is ready for returning.</p>
   *
   * <p>Keyboard mode is restored, text mode is set and VT autoswitch is enabled.
   * Then, console file descriptor is closed.</p>
   */
    //todo put inside close and let the shutdown hook do the work
  private def deinitialize(): Unit =
    Log.log("Closing system console")
    try
      ttyfd.setKeyboardMode(old_kbmode)
      ttyfd.setConsoleMode(KD_TEXT)
      val vtm = new vt_mode
      vtm.mode = VT_AUTO.toByte
      vtm.relsig = 0
      vtm.acqsig = 0
      ttyfd.setVTmode(vtm)
      ttyfd.close()
    catch
      case e: LastErrorException =>
        System.err.println("Error occured during console shutdown: " + e.getMessage)
        e.printStackTrace()
    // free objects
    closeFramebuffer()

  /**
   * <p>Switch the display to a graphics mode.</p>
   *
   * <p>It switches VT to graphics mode with keyboard turned off.
   * Then, it tells kernel to notify Java when VT switch occurs.
   * Also, framebuffer contents are restored and write access is enabled.</p>
   *
   * @throws RuntimeException when the switch fails
   */
  override def switchToGraphicsMode(): Unit =
    Log.log("Switching console to graphics mode")
    try
      Log.log("Switching off keyboard")
      ttyfd.setKeyboardMode(K_OFF)
      Log.log("Switching to graphics")
      ttyfd.setConsoleMode(KD_GRAPHICS)
      Log.log("Switching setting VT mode")
      val vtm = new vt_mode
      vtm.mode = VT_PROCESS.toByte
      vtm.relsig = SIGUSR2.toByte
      vtm.acqsig = SIGUSR2.toByte
      ttyfd.setVTmode(vtm)
      Log.log("Done Switching console to graphics mode try block")
    catch
      case e: LastErrorException =>
        throw new RuntimeException("Switch to graphics mode failed", e)
    if fbInstance != null then
      fbInstance.restoreData()
      Log.log("Switching finished if block")

  /**
   * <p>Switch the display to a text mode.</p>
   *
   * <p>It stores framebuffer data and disables write access. Then,
   * it switches VT to text mode and allows kernel to auto-switch it.</p>
   *
   * @throws RuntimeException when the switch fails
   */
  override def switchToTextMode(): Unit =
    Log.log("Switching console to text mode")
    if fbInstance != null then
      fbInstance.setFlushEnabled(false)
      fbInstance.storeData()
    try
      ttyfd.setConsoleMode(KD_TEXT)
      val vtm = new vt_mode
      vtm.mode = VT_AUTO.toByte
      vtm.relsig = 0
      vtm.acqsig = 0
      ttyfd.setVTmode(vtm)
    catch
      case e: LastErrorException =>
        throw new RuntimeException("Switch to text mode failed", e)
    Log.log("Switching to text mode succeeded")

  /**
   * <p>Get the framebuffer for the system display.</p>
   *
   * <p>The framebuffer is initialized only once, later calls
   * return references to the same instance.</p>
   *
   * @return Java framebuffer compatible with the system display.
   * @throws RuntimeException when switch to graphics mode or the framebuffer initialization fails.
   */
    //todo is there any time you wouldn't just open the frame buffer??
  override def openFramebuffer(): JavaFramebuffer =
    if fbInstance == null then
      Log.log("Initialing framebuffer in system console")
      switchToGraphicsMode()
      //todo another option is to just reuse the native frame buffer from the initialization here
      initializeFramebuffer(new NativeFramebuffer(fbPath), enable = true)
    fbInstance
}