package ev3dev4s.lcd

import com.sun.jna.LastErrorException
import ev3dev4s.Log

import java.awt.Image
import java.awt.Font
import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.Color
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D
import java.awt.image.AffineTransformOp
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.util.Timer
import java.util.TimerTask

/**
 * Lejos LCD reimplementation using Java2D API in Scala
 */
object Lcd {

  Log.log("Start creating LCD")
  // drawable
  private val framebuffer: JavaFramebuffer = {
    val display: DisplayInterface = {
      Log.log("initializing new real display")
      try new OwnedDisplay()
      catch {
        case e: LastErrorException =>
          val errno = e.getErrorCode
          if (errno == NativeConstants.ENOTTY || errno == NativeConstants.ENXIO) {
            Log.log("real display init failed, but it was caused by not having a real TTY, using fake console")
            // not inside Brickman
            new StolenDisplay()
          }
          else throw e
      }
    }
    display.openFramebuffer() //todo change to just open the thing
  }

  Log.log(s"framebuffer is $framebuffer")
  val image: BufferedImage = framebuffer.createCompatibleBuffer()
  val g2d: Graphics2D = this.image.createGraphics

  this.clear()

  // stroke
  private var stroke = 0

  /**
   * Write LCD with current context
   */
  def flush(): Unit = framebuffer.flushScreen(image)

  /**
   * Translates the origin of the graphics context to the point
   * (x, y) in the current coordinate system. Calls are cumulative.
   *
   * @param x the new translation origin x value
   * @param y new translation origin y value
   * @see #getTranslateX()
   * @see #getTranslateY()
   */
  def translate(x: Int, y: Int): Unit = g2d.translate(x, y)

  def getFont: Font = g2d.getFont

  def setFont(font: Font): Unit = g2d.setFont(font)

  def getTranslateX: Int = g2d.getTransform.getTranslateX.toInt

  def getTranslateY: Int = g2d.getTransform.getTranslateY.toInt

  /* Public color definitions NOT Standard*/
  val BLACK = 0
  val WHITE = 0xffffff

  /**
   * Set the current drawing color. The value is in the format 0x00RRGGBB.
   * NOTE. Currently only black and white is supported. any non black color
   * is treated as white!
   *
   * @param rgb new color.
   */
  def setColor(rgb: Int): Unit = g2d.setColor(new Color(rgb))

/**
 * Sets the current color to the specified RGB values.
 *
 * @param red   the red component
 * @param green the green component
 * @param blue  the blue
 * @throws IllegalArgumentException if any of the color components
 *                                  are outside of range 
 */
  def setColor(red: Int, green: Int, blue: Int): Unit = g2d.setColor(new Color(red, green, blue))

  /**
   * @param x     the x coordinate
   * @param y     the y coordinate
   * @param color the pixel color (0 = white, 1 = black)
   */
  def setPixel(x: Int, y: Int, color: Int): Unit = {
    val in = new Point2D.Float(x.toFloat, y.toFloat) //todo use Point2D.Integer ??
    val dst = new Point2D.Float
    g2d.getTransform.transform(in, dst)
    val fill = if (color == 0) Color.WHITE
    else Color.BLACK
    image.setRGB(dst.x.toInt, dst.y.toInt, fill.getRGB)
  }

  /**
   * @param x the x coordinate
   * @param y the y coordinate
   * @return the pixel color (0 = white, 1 = black)
   */  def getPixel(x: Int, y: Int): Int = {
    val in = new Point2D.Float(x.toFloat, y.toFloat) //todo use Point2D.Integer ??
    val dst = new Point2D.Float
    g2d.getTransform.transform(in, dst)
    val rgb = image.getRGB(dst.x.toInt, dst.y.toInt)
    if ((rgb & 0x00FFFFFF) == 0x00FFFFFF) 0
    else 1
  }

  /**
   * Draws the specified String using the current font and color. x and y
   * give the location of the anchor point. Additional method to allow for
   * the easy use of inverted text. In this case the area below the string
   * is drawn in the current color, before drawing the text in the "inverted"
   * color.
   * <br><b>Note</b>: This is a non standard method.
   *
   * @param str      the String to be drawn
   * @param x        the x coordinate of the anchor point
   * @param y        the y coordinate of the anchor point
   * @param anchor   the anchor point for positioning the text
   * @param inverted true to invert the text display.
   */
  def drawString(str: String, x: Int, y: Int, anchor: Int, inverted: Boolean): Unit = {
    val oldFg = g2d.getColor
    val oldBg = g2d.getBackground
    g2d.setColor(if (inverted) Color.WHITE
    else Color.BLACK)
    g2d.setBackground(if (inverted) Color.BLACK
    else Color.WHITE)
    drawString(str, x, y, anchor)
    g2d.setColor(oldFg)
    g2d.setBackground(oldBg)
  }

  /**
   * Draws the specified String using the current font and color. x and y
   * give the location of the anchor point.
   *
   * @param str    the String to be drawn
   * @param x      the x coordinate of the anchor point
   * @param y      the y coordinate of the anchor point
   * @param anchor the anchor point for positioning the text
   */
  def drawString(str: String, x: Int, y: Int, anchor: Int): Unit = {
    val metrics = g2d.getFontMetrics
    val w = metrics.stringWidth(str)
    val h = metrics.getHeight
    val x1 = adjustX(x, w, anchor)
    val y1 = adjustY(y, h, anchor)
    g2d.drawString(str, x1, y1)
  }

  /**
   * Draw a substring to the graphics surface using the current color.
   *
   * @param str    the base string
   * @param offset the start of the sub string
   * @param len    the length of the sub string
   * @param x      the x coordinate of the anchor point
   * @param y      the x coordinate of the anchor point
   * @param anchor the anchor point used to position the text.
   */
  def drawSubstring(str: String, offset: Int, len: Int, x: Int, y: Int, anchor: Int): Unit = {
    val sub = str.substring(offset, offset + len)
    drawString(sub, x, y, anchor)
  }

  /**
   * Draw a single character to the graphics surface using the current color.
   *
   * @param character the character to draw
   * @param x         the x coordinate of the anchor point
   * @param y         the x coordinate of the anchor point
   * @param anchor    the anchor point used to position the text.
   */
  def drawChar(character: Char, x: Int, y: Int, anchor: Int): Unit = {
    val str = new String(Array[Char](character))
    drawString(str, x, y, anchor)
  }

  /**
   * Draw a series of characters to the graphics surface using the current color.
   *
   * @param data   the characters
   * @param offset the start of the characters to be drawn
   * @param length the length of the character string to draw
   * @param x      the x coordinate of the anchor point
   * @param y      the x coordinate of the anchor point
   * @param anchor the anchor point used to position the text.
   */
  def drawChars(data: Array[Char], offset: Int, length: Int, x: Int, y: Int, anchor: Int): Unit = {
    val str = new String(data)
    drawString(str, x, y, anchor)
  }

  /**
   * Constant for the <code>SOLID</code> stroke style.
   *
   * <P>Value <code>0</code> is assigned to <code>SOLID</code>.</P>
   */
  val SOLID = 0
  /**
   * Constant for the <code>DOTTED</code> stroke style.
   *
   * <P>Value <code>1</code> is assigned to <code>DOTTED</code>.</P>
   */
  val DOTTED = 1

  /**
   * @return current style.
   */
  def getStrokeStyle: Int = this.stroke

  /**
   * @param i new style.
   */  
  def setStrokeStyle(i: Int): Unit = {
    this.stroke = i
    val stroke:BasicStroke = if (i == DOTTED) {
      val dash = Array[Float](3.0f, 3.0f)
      val dash_phase = 0.0f
      new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, dash, dash_phase)
    }
    else if (i == SOLID)
      new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f)
    else
      throw new IllegalArgumentException("Invalid stroke")
    g2d.setStroke(stroke)
  }

  val TRANS_MIRROR = 2
  val TRANS_MIRROR_ROT180 = 1
  val TRANS_MIRROR_ROT270 = 4
  val TRANS_MIRROR_ROT90 = 7
  val TRANS_NONE = 0
  val TRANS_ROT180 = 3
  val TRANS_ROT270 = 6
  val TRANS_ROT90 = 5
  
  /**
   * Draw the specified region of the source image to the graphics surface
   * after applying the requested transformation, use the supplied rop.
   * <br>NOTE: When calculating the anchor point this method assumes that
   * a transformed version of the source width/height should be used.
   *
   * @param src       The source image
   * @param sx        x coordinate of the region
   * @param sy        y coordinate of the region
   * @param wIn         width of the region
   * @param hIn         height of the region
   * @param transform the required transform
   * @param xIn         x coordinate of the anchor point
   * @param yIn         y coordinate of the anchor point
   * @param anchor    type of anchor
   * @param rop       raster operation used to draw the output.
   */
  private def drawRegionRop(src: Image, sx: Int, sy: Int, wIn: Int, hIn: Int, transform: Int, xIn: Int, yIn: Int, anchor: Int, rop: Int): Unit = {
    var w = wIn
    var h = hIn
    val x = adjustX(xIn, w, anchor)
    val y = adjustY(yIn, h, anchor)
    val srcI = any2rgb(src)
    val midx = srcI.getWidth / 2.0
    val midy = srcI.getHeight / 2.0
    val tf = new AffineTransform
    tf.translate(midx, midy)
    val h0 = h
    transform match {
      case TRANS_MIRROR =>
        tf.scale(-1.0, 1.0)
      case TRANS_MIRROR_ROT90 =>
        tf.scale(-1.0, 1.0)
        tf.quadrantRotate(1)
        h = w
        w = h0
      case TRANS_MIRROR_ROT180 =>
        tf.scale(-1.0, 1.0)
        tf.quadrantRotate(2)
      case TRANS_MIRROR_ROT270 =>
        tf.scale(-1.0, 1.0)
        tf.quadrantRotate(3)
        h = w
        w = h0
      case TRANS_NONE =>
      case TRANS_ROT90 =>
        tf.quadrantRotate(1)
        h = w
        w = h0
      case TRANS_ROT180 =>
        tf.quadrantRotate(2)
      case TRANS_ROT270 =>
        tf.quadrantRotate(3)
        h = w
        w = h0
      case _ =>
        throw new RuntimeException("Bad Option")
    }
    tf.translate(-midx, -midy)
    val op = new AffineTransformOp(tf, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
    val transformed = ImageUtils.createXRGBImage(w, h)
    op.filter(srcI, transformed)
    val dstI = any2rgb(image)
    bitBlt(srcI, sx, sy, dstI, x, y, w, h, rop)
    g2d.drawImage(dstI, 0, 0, null)
  }
  
  def drawImage(image: Image, i: Int, i1: Int, i2: Int): Unit = g2d.drawImage(image, i, i1, null)

  def drawLine(x1: Int, y1: Int, x2: Int, y2: Int): Unit = g2d.drawLine(x1, y1, x2, y2)

  def fillRect(x: Int, y: Int, width: Int, height: Int): Unit = g2d.fillRect(x, y, width, height)

  /**
   * Copy one rectangular area of the drawing surface to another.
   *
   * @param sx     Source x
   * @param sy     Source y
   * @param w      Source width
   * @param h      Source height
   * @param x      Destination x
   * @param y      Destination y
   * @param anchor location of the anchor point of the destination.
   */
  def copyArea(sx: Int, sy: Int, w: Int, h: Int, x: Int, y: Int, anchor: Int): Unit = {
    g2d.copyArea(sx, sy, w, h, adjustX(x, w, anchor), adjustY(y, h, anchor))
  }

  /**
   * Centering text and images horizontally
   * around the anchor point
   *
   * <P>Value <code>1</code> is assigned to <code>HCENTER</code>.</P>
   */
  val HCENTER = 1
  /**
   * Centering images vertically
   * around the anchor point.
   *
   * <P>Value <code>2</code> is assigned to <code>VCENTER</code>.</P>
   */
  val VCENTER = 2
  /**
   * Position the anchor point of text and images
   * to the left of the text or image.
   *
   * <P>Value <code>4</code> is assigned to <code>LEFT</code>.</P>
   */
  val LEFT = 4
  /**
   * Position the anchor point of text and images
   * to the right of the text or image.
   *
   * <P>Value <code>8</code> is assigned to <code>RIGHT</code>.</P>
   */
  val RIGHT = 8
  /**
   * Position the anchor point of text and images
   * above the text or image.
   *
   * <P>Value <code>16</code> is assigned to <code>TOP</code>.</P>
   */
  val TOP = 16
  /**
   * Position the anchor point of text and images
   * below the text or image.
   *
   * <P>Value <code>32</code> is assigned to <code>BOTTOM</code>.</P>
   */
  val BOTTOM = 32
  /**
   * Position the anchor point at the baseline of text.
   *
   * <P>Value <code>64</code> is assigned to <code>BASELINE</code>.</P>
   */
  val BASELINE = 64

  /**
   * Adjust the x co-ordinate to use the translation and anchor values.
   */
  private def adjustX(xIn: Int, w: Int, anchor: Int): Int = { //todo make functional
    var x = xIn
    anchor & (LEFT | RIGHT | HCENTER) match {
      case LEFT =>
      case RIGHT =>
        x -= w
      case HCENTER =>
        x -= w / 2
      case _ =>
        throw new RuntimeException("Bad Option")
    }
    x
  }

  /**
   * Adjust the y co-ordinate to use the translation and anchor values.
   */
  private def adjustY(yIn: Int, h: Int, anchor: Int):Int = { //todo make functional
    var y = yIn
    anchor & (TOP | BOTTOM | VCENTER) match {
      case TOP =>
      case BOTTOM =>
        y -= h
      case VCENTER =>
        y -= h / 2
      case _ =>
        throw new RuntimeException("Bad Option")
    }
    y
  }

  def drawRoundRect(x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int): Unit = g2d.drawRoundRect(x, y, width, height, arcWidth, arcHeight)

  def drawRect(x: Int, y: Int, width: Int, height: Int): Unit = g2d.drawRect(x, y, width, height)

  def drawArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int): Unit = g2d.drawArc(x, y, width, height, startAngle, arcAngle)

  def fillArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int): Unit = g2d.fillArc(x, y, width, height, startAngle, arcAngle)

  def drawOval(x: Int, y: Int, width: Int, height: Int): Unit = g2d.drawOval(x, y, width, height)

  /**
   * Refresh the display. If auto refresh is off, this method will wait until
   * the display refresh has completed. If auto refresh is on it will return
   * immediately.
   */
  def refresh(): Unit = flush()

  /**
   * Clear the display.
   */
  def clear(): Unit = {
    val tf = g2d.getTransform.clone.asInstanceOf[AffineTransform]
    g2d.getTransform.setToIdentity()
    g2d.setColor(Color.WHITE)
    g2d.fillRect(0, 0, framebuffer.getWidth, framebuffer.getHeight)
    flush()
    g2d.setTransform(tf)
  }

  def getWidth: Int = framebuffer.getWidth

  def getHeight: Int = framebuffer.getHeight

  /**
   * Provide access to the LCD display frame buffer.
   *
   * @return byte array that is the frame buffer.
   */
  def getDisplay: Array[Byte] = ImageUtils.getImageBytes(image)

  /**
   * Get access to hardware LCD display.
   *
   * @return byte array that is the frame buffer
   */
  def getHWDisplay: Array[Byte] = getDisplay

  def setContrast(i: Int): Unit = {
    // not implemented even on leJOS
  }

  /**
   * Convert from leJOS image format to Java image
   */
  private def lejos2rgb(src: Array[Byte], width: Int, height: Int) = {
    @SuppressWarnings(Array("SuspiciousNameCombination")) val in = ImageUtils.createBWImage(height, width, true, src)
    val out = ImageUtils.createXRGBImage(width, height)
    java_lejos_flip(in, out)
  }

  private def any2rgb(img: Image) = {
    val copy = ImageUtils.createXRGBImage(img.getWidth(null), img.getHeight(null))
    val gfx = copy.getGraphics.asInstanceOf[Graphics2D]
    gfx.drawImage(img, 0, 0, null)
    gfx.dispose()
    copy
  }

  /**
   * Convert from Java image to leJOS image format
   */
  private def any2lejos(img: BufferedImage): Array[Byte] = {
    val out = ImageUtils.createBWImage(img.getHeight, img.getWidth, true)
    val right = java_lejos_flip(img, out)
    right.getRaster.getDataBuffer.asInstanceOf[DataBufferByte].getData
  }

  private def java_lejos_flip(in: BufferedImage, out: BufferedImage): BufferedImage = {
    val tf = new AffineTransform
    tf.quadrantRotate(1)
    tf.scale(-1.0, +1.0)
    val op = new AffineTransformOp(tf, AffineTransformOp.TYPE_NEAREST_NEIGHBOR)
    op.filter(in, out)
  }

  /**
   * Slow emulation of leJOS bitBlt()
   *
   * Standard two input BitBlt function with the LCD display as the
   * destination. Supports standard raster ops and
   * overlapping images. Images are held in native leJOS/Lego format.
   *
   * @param src byte array containing the source image
   * @param sw  Width of the source image
   * @param sh  Height of the source image
   * @param sx  X position to start the copy from
   * @param sy  Y Position to start the copy from
   * @param dx  X destination
   * @param dy  Y destination
   * @param w   width of the area to copy
   * @param h   height of the area to copy
   * @param rop raster operation.
   */
  def bitBlt(src: Array[Byte], sw: Int, sh: Int, sx: Int, sy: Int, dx: Int, dy: Int, w: Int, h: Int, rop: Int): Unit = {
    val srcI = lejos2rgb(src, sw, sh)
    val dstI = any2rgb(image)
    bitBlt(srcI, sx, sy, dstI, dx, dy, w, h, rop)
    g2d.drawImage(dstI, 0, 0, null)
  }

  /**
   * Standard two input BitBlt function. Supports standard raster ops and
   * overlapping images. Images are held in native leJOS/Lego format.
   *
   * @param src byte array containing the source image
   * @param sw  Width of the source image
   * @param sh  Height of the source image
   * @param sx  X position to start the copy from
   * @param sy  Y Position to start the copy from
   * @param dst byte array containing the destination image
   * @param dw  Width of the destination image
   * @param dh  Height of the destination image
   * @param dx  X destination
   * @param dy  Y destination
   * @param w   width of the area to copy
   * @param h   height of the area to copy
   * @param rop raster operation.
   */
  def bitBlt(src: Array[Byte], sw: Int, sh: Int, sx: Int, sy: Int, dst: Array[Byte], dw: Int, dh: Int, dx: Int, dy: Int, w: Int, h: Int, rop: Int): Unit = {
    val srcI = lejos2rgb(src, sw, sh)
    val dstI = lejos2rgb(dst, dw, dh)
    bitBlt(srcI, sx, sy, dstI, dx, dy, w, h, rop)
    val gfx = dstI.createGraphics
    gfx.drawImage(srcI, dy, dx, dy + h, dx + w, sy, sx, sy + h, sx + w, Color.WHITE, null)
    gfx.dispose()
    val data = any2lejos(dstI)
    System.arraycopy(data, 0, dst, 0, Math.min(data.length, dst.length))
  }

  private def bitBlt(src: BufferedImage, sx: Int, sy: Int, dst: BufferedImage, dx: Int, dy: Int, w: Int, h: Int, rop: Int): Unit = {
    val srcR = src.getRaster
    val dstR = dst.getRaster
    val msk_dst = (0xFF & (rop >> 24)).toByte
    val xor_dst = (0xFF & (rop >> 16)).toByte
    val msk_src = (0xFF & (rop >> 8)).toByte
    val xor_src = (0xFF & rop).toByte
    val dstskip = msk_dst == 0 && xor_dst == 0
    val dstpix = new Array[Int](4)
    val srcpix = new Array[Int](4)
    for (vx <- 0 until w) {
      for (vy <- 0 until h) {
        val srcx = sx + vx
        val srcy = sy + vy
        val dstx = dx + vx
        val dsty = dy + vy
        srcR.getPixel(srcx, srcy, srcpix)
        if (dstskip) { // only rgb, no a
          for (s <- 0 until 3) {
            dstpix(s) = (srcpix(s) & msk_src) ^ xor_src
          }
        }
        else {
          dstR.getPixel(dstx, dsty, dstpix)
          for (s <- 0 until 3) {
            dstpix(s) = ((dstpix(s) & msk_dst) ^ xor_dst) ^ ((srcpix(s) & msk_src) ^ xor_src)
          }
        }
        dstR.setPixel(dstx, dsty, dstpix)
      }
    }
  }

  // autorefresh
  //todo nothing seems to use autorefresh
  lazy val timer = new Timer("LCD flusher", true)
  private var timer_run = false
  private var timer_msec = 0

  def setAutoRefresh(b: Boolean): Unit = if (this.timer_run != b) {
    this.timer_run = b
    timerUpdate()
  }

  def setAutoRefreshPeriod(i: Int): Unit = {
    val old = this.timer_msec
    if (old != i) {
      this.timer_msec = i
      timerUpdate()
    }
  }

  private def timerUpdate(): Unit = {
    timer.cancel()
    if (timer_run && timer_msec > 0) timer.scheduleAtFixedRate(new Flusher, 0, timer_msec)
  }

  private class Flusher extends TimerTask {
    def run(): Unit = refresh()
  }
}

/*
Color sensors created
1618764674394 Command: beep
1618764677266 Start creating LCD
1618764677281 initializing new real display
1618764677326 Opening TTY
1618764687734 Getting activeVT
1618764688302 Getting old keyboard mode
1618764688374 Opening FB 0
1618764688445 Start NativeFramebuffer /dev/fb0
1618764688500 NativeFramebuffer /dev/fb0 constructed
1618764688730 map vt5 -> fb 0
1618764688753 add deinitializer
1618764688921 Switching console to text mode
1618764689114 Switching to text mode succeeded
1618764689132 Initialing framebuffer in system console
1618764689140 Switching console to graphics mode
1618764689144 Switching off keyboard
1618764689151 Switching to graphics
1618764689155 Switching setting VT mode
1618764689182 Done Switching console to graphics mode try block
1618764689189 Start NativeFramebuffer /dev/fb0
1618764689194 NativeFramebuffer /dev/fb0 constructed
1618764689201 Start initializeFramebuffer
1618764689271 JavaFramebuffer start
1618764689554 JavaFramebuffer fixinfo
1618764690439 JavaFramebuffer varinfo
1618764690455 stride is 712
1618764690464 JavaFramebuffer videomem
1618764690472 stride is 712
1618764690476 JavaFramebuffer backup
1618764690485 stride is 712
1618764710768 Opened JavaFramebuffer, mode 178x128x32bpp
1618764710776 initializeFramebuffer made JavaFramebuffer
1618764710783 initializeFramebuffer setFlushEnabled
1618764710791 Clearing framebuffer
1618764710801 stride is 712
1618764710815 stride is 712
1618764710825 initializeFramebuffer cleared
1618764710830 Storing framebuffer snapshot
1618764710836 stride is 712
1618764710844 initializeFramebuffer data stored
1618764710856 framebuffer is ev3dev4s.lcd.JavaFramebuffer@1edcf1
1618764710863 stride is 712
1618764711001 stride is 712
1618764711021 stride is 712
lcd created

 */