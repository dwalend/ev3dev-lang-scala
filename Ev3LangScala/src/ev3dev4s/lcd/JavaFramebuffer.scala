package ev3dev4s.lcd

import java.awt.image.BufferedImage
import java.io.Closeable

/**
 * <p>Java2D-based framebuffer interface</p>
 */
trait JavaFramebuffer extends Closeable {
  /**
   * Query framebuffer width.
   *
   * @return Screen width in pixels.
   */
    def getWidth:Int

  /**
   * Query framebuffer height.
   *
   * @return Screen height in pixels.
   */
  def getHeight:Int

  /**
   * Query framebuffer scanline stride, e.g. real row length in bytes.
   *
   * @return Screen scanline stride in bytes
   */
  def getStride :Int

  /**
   * Create full-screen buffer.
   *
   * @return BufferedImage with correct settings.
   */
  def createCompatibleBuffer:BufferedImage

  /**
   * Create pixel-compatible buffer with a specified size.
   *
   * @param width  Requested image width.
   * @param height Requested image height.
   * @return BufferedImage with correct settings.
   */
  def createCompatibleBuffer(width: Int, height: Int):BufferedImage

  /**
   * Create pixel-compatible buffer with a specified size and stride.
   *
   * @param width  Requested image width.
   * @param height Requested image height.
   * @param stride Requested scanline stride.
   * @return BufferedImage with correct settings.
   */
  def createCompatibleBuffer(width: Int, height: Int, stride: Int):BufferedImage

  /**
   * Create pixel-compatible buffer with a specified size and stride.
   * This buffer will be backed by existing byte array.
   *
   * @param width  Requested image width.
   * @param height Requested image height.
   * @param stride Requested scanline stride.
   * @return BufferedImage with correct settings.
   */
  def createCompatibleBuffer(width: Int, height: Int, stride: Int, backed: Array[Byte]):BufferedImage

  /**
   * Write full-screen buffer into the framebuffer.
   *
   * @param compatible What to draw onto the screen.
   */
  def flushScreen(compatible: BufferedImage):Unit

  /**
   * Controls whether {@link JavaFramebuffer# flushScreen ( BufferedImage )} has effect or not.
   *
   * @param rly Whether flushing should be enabled or not.
   */
  def setFlushEnabled(rly: Boolean):Unit

  /**
   * Store current hardware framebuffer state.
   */
  def storeData():Unit

  /**
   * Restore original hardware framebuffer state.
   */
  def restoreData():Unit

  /**
   * Clear the hardware framebuffer.
   */
  def clear():Unit

  /**
   * Get the associated display manager.
   *
   * @return reference to display manager
   */
  def getDisplay: DisplayInterface
}