package ev3dev4s.lcd

import com.sun.jna.LastErrorException
import ev3dev4s.Log
import java.awt.image.BufferedImage
import java.util
import ev3dev4s.lcd.NativeConstants.FB_TYPE_PACKED_PIXELS
import ev3dev4s.lcd.NativeConstants.FB_VISUAL_TRUECOLOR

/**
 * Linux XRGB 32bpp framebuffer
 *
 * @param fb      The framebuffer device (e.g. /dev/fb0)
 * @param display Display manager (e.g. /dev/tty)
 */
class RGBFramebuffer(val fb: NativeFramebuffer, override val display: DisplayInterface)
  extends LinuxFramebuffer(fb, display) {

  //todo this is really odd
  /*
  try {
    close()
  } catch {
    case e: LastErrorException => throw new RuntimeException("Cannot close framebuffer", e)
  }
    */
  // taking ownership
  setDeviceClose(true)

  override def createCompatibleBuffer(width: Int, height: Int): BufferedImage = {
    val stride = 4 * width
    createCompatibleBuffer(width, height, stride, new Array[Byte](stride * height))
  }

  override def createCompatibleBuffer(width: Int, height: Int, stride: Int, buffer: Array[Byte]): BufferedImage =
    ImageUtils.createXRGBImage(width, height, stride, getComponentOffsets, buffer)

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