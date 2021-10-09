package ev3dev4s.lcd.javaframebuffer

import com.sun.jna.LastErrorException
import ev3dev4s.Log

import java.io.IOException

/**
 * <p>Display manager interface.</p>
 *
 * <p>This class provides interface for switching between text and
 * graphics display modes. It also provides interface for opening
 * graphics framebuffer. </p>
 *
 * @author Jakub Vaněk
 */
abstract class DisplayInterface extends AutoCloseable {
  protected var fbInstance:JavaFramebuffer = null //todo this can be a val - simplify the lifecycle

  /**
   * <p>Switch the display to a graphics mode.</p>
   *
   * @throws RuntimeException when the switch fails
   */
  def switchToGraphicsMode():Unit

  /**
   * <p>Switch the display to a text mode.</p>
   *
   * @throws RuntimeException when the switch fails
   */
  def switchToTextMode():Unit

  /**
   * <p>Get the framebuffer for the system display.</p>
   *
   * <p>The framebuffer is initialized only once, later calls
   * return references to the same instance.</p>
   *
   * @return Java framebuffer compatible with the system display.
   * @throws RuntimeException when switch to graphics mode or the framebuffer initialization fails.
   */
  def openFramebuffer(): JavaFramebuffer

  /**
   * <p>Remove all references to this framebuffer.</p>
   *
   * @param fb Framebuffer to remove.
   */
    //todo never used
  def releaseFramebuffer(fb: JavaFramebuffer):Unit = {
    if fb != null && (fb eq fbInstance) then fbInstance = null
    else throw new IllegalArgumentException("Framebuffer must be non-null and identical to the builtin framebuffer")
  }

  /**
   * Close the internal framebuffer.
   */
    //todo never used
  protected def closeFramebuffer(): Unit = {
    if fbInstance != null then try fbInstance.close()
    catch {
      case e@(_: IOException | _: LastErrorException) =>
        System.err.println("Error occurred during framebuffer shutdown: " + e.getMessage)
        e.printStackTrace()
    } finally fbInstance = null
  }

  /**
   * Initialize new internal instance of JavaFramebuffer.
   *
   * @param backend Device behind JavaFramebuffer.
   * @param enable  Whether to enable framebuffer flushing from the beginning.
   */
  protected def initializeFramebuffer(backend: NativeFramebuffer, enable: Boolean): Unit = {
    Log.log("Start initializeFramebuffer")
    fbInstance = new JavaFramebuffer(backend, this)
    Log.log("initializeFramebuffer made JavaFramebuffer")
    fbInstance.setFlushEnabled(enable)
    Log.log("initializeFramebuffer setFlushEnabled")
    fbInstance.clear() //this initial clear takes a long time
    Log.log("initializeFramebuffer cleared")
    fbInstance.storeData()
    Log.log("initializeFramebuffer data stored")
  }
}