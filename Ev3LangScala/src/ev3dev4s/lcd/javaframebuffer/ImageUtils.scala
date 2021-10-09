package ev3dev4s.lcd.javaframebuffer

import java.awt.Transparency
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ComponentColorModel
import java.awt.image.DataBuffer
import java.awt.image.DataBufferByte
import java.awt.image.IndexColorModel
import java.awt.image.MultiPixelPackedSampleModel
import java.awt.image.PixelInterleavedSampleModel
import java.awt.image.Raster

/**
 * Common image utilities for framebuffer manipulation
 */
object ImageUtils {
  /**
   * Create new ev3dev-compatible XRGB image.
   *
   * @param w Image width
   * @param h Image height
   * @return Configured BufferedImage
   */
  def createXRGBImage(w: Int, h: Int): BufferedImage = createXRGBImage(w, h, w * 4)

  /**
   * Create new XRGB image.
   *
   * @param w      Image width
   * @param h      Image height
   * @param stride Image scanline stride, i.e. how long the row is in bytes.
   * @return Configured BufferedImage
   */
  def createXRGBImage(w: Int, h: Int, stride: Int): BufferedImage = createXRGBImage(w, h, stride, new Array[Byte](stride * h))

  /**
   * Create new XRGB image.
   *
   * @param width  Image width
   * @param height Image height
   * @param stride Image scanline stride, i.e. how long the row is in bytes.
   * @param buffer Backing buffer.
   * @return Configured BufferedImage
   */
  def createXRGBImage(width: Int, height: Int, stride: Int, buffer: Array[Byte]): BufferedImage =
    createXRGBImage(width, height, stride, getDefaultComponentOffsets, buffer)

  /**
   * Create new XRGB image.
   *
   * @param width   Image width
   * @param height  Image height
   * @param stride  Image scanline stride, i.e. how long the row is in bytes.
   * @param offsets Array of size 4 describing the offsets of color bands: { R, G, B, A }
   * @param buffer  Backing buffer.
   * @return Configured BufferedImage
   */
  def createXRGBImage(width: Int, height: Int, stride: Int, offsets: Array[Int], buffer: Array[Byte]): BufferedImage =
    if buffer.length < (stride * height) then throw new IllegalArgumentException("Buffer is smaller than height*stride")
    if stride < width * 4 then throw new IllegalArgumentException("Stride is smaller than width * 4")

    //todo if any of these take a while just do them once
    val db = new DataBufferByte(buffer, buffer.length)
    // initialize buffer <-> samples bridge
    val sm = new PixelInterleavedSampleModel(DataBuffer.TYPE_BYTE, width, height, 4, stride, offsets)
    // initialize color interpreter
    val spc = ColorSpace.getInstance(ColorSpace.CS_sRGB)
    val cm = new ComponentColorModel(spc, true, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE)
    // create raster
    val wr = Raster.createWritableRaster(sm, db, null)
    // glue everything together
    new BufferedImage(cm, wr, false, null)

  /**
   * Get default XRGB component offsets.
   *
   * @return Offsets: { R, G, B, A }
   */
  def getDefaultComponentOffsets: Array[Int] = Array[Int](2, 1, 0, 3)

  /**
   * Create new BW image.
   *
   * @param width     Image width.
   * @param height    Image height.
   * @param zeroBlack Whether black color is represented by the 0 bit value.
   * @return Configured BufferedImage.
   */
  def createBWImage(width: Int, height: Int, zeroBlack: Boolean): BufferedImage =
    val stride = (width + 7) / 8
    createBWImage(width, height, stride, zeroBlack, new Array[Byte](stride * height))

  /**
   * Create new BW image backed by existing data.
   *
   * @param width     Image width.
   * @param height    Image height.
   * @param zeroBlack Whether black color is represented by the 0 bit value.
   * @param backed    Backing byte buffer.
   * @return Configured BufferedImage.
   */
  def createBWImage(width: Int, height: Int, zeroBlack: Boolean, backed: Array[Byte]): BufferedImage =
    val stride = (width + 7) / 8
    createBWImage(width, height, stride, zeroBlack, backed)

  /**
   * Create new BW image.
   *
   * @param width     Image width.
   * @param height    Image height.
   * @param stride    Image scanline stride, i.e. how long the row is in bytes.
   * @param zeroBlack Whether black color is represented by the 0 bit value.
   * @return Configured BufferedImage.
   */
  def createBWImage(width: Int, height: Int, stride: Int, zeroBlack: Boolean): BufferedImage = createBWImage(width, height, stride, zeroBlack, new Array[Byte](stride * height))

  /**
   * Create new BW image backed by existing data.
   *
   * @param width     Image width.
   * @param height    Image height.
   * @param stride    Image scanline stride, i.e. how long the row is in bytes.
   * @param zeroBlack Whether black color is represented by the 0 bit value.
   * @param backed    Backing byte buffer.
   * @return Configured BufferedImage.
   */
  def createBWImage(width: Int, height: Int, stride: Int, zeroBlack: Boolean, backed: Array[Byte]): BufferedImage =
    if backed.length < (stride * height) then throw new IllegalArgumentException("Buffer is smaller than height*stride")
    if stride < width / 8 then throw new IllegalArgumentException("Stride is smaller than width/8")

    // initialize backing store
    val db = new DataBufferByte(backed, backed.length)
    // initialize buffer <-> sample mapping
    val packing = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE, width, height, 1, stride, 0)
    // initialize raster
    val wr = Raster.createWritableRaster(packing, db, null)
    val mapPixels = if zeroBlack then Array[Byte](0x00.toByte, 0xFF.toByte)
                    else Array[Byte](0xFF.toByte, 0x00.toByte)
    val cm = new IndexColorModel(1, mapPixels.length, mapPixels, mapPixels, mapPixels)
    new BufferedImage(cm, wr, false, null)

  /**
   * Convert image to the underlying byte buffer.
   *
   * @param image Configured BufferedImage.
   * @return Byte array.
   */
  def getImageBytes(image: BufferedImage): Array[Byte] =
    val rst = image.getRaster
    val buf = rst.getDataBuffer
    buf.asInstanceOf[DataBufferByte].getData
}