package ev3dev4s.lcd

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
 * Lejos LCD reimplementation using Java2D API
 */
object Lcd extends GraphicsLCD {

  Log.log("Start creating LCD")
  // drawable
  val fb: JavaFramebuffer = SystemDisplay.initializeRealFramebuffer
  val image: BufferedImage = fb.createCompatibleBuffer
  val g2d: Graphics2D = this.image.createGraphics

  // autorefresh
  //todo nothing seems to use autorefresh
  val timer = new Timer("LCD flusher", true)
  private var timer_run = false
  private var timer_msec = 0
  this.clear()

  // stroke
  private var stroke = 0

  def getFramebuffer: JavaFramebuffer = fb

  /**
   * Write LCD with current context
   */
  def flush(): Unit = fb.flushScreen(image)

  override def translate(x: Int, y: Int): Unit = g2d.translate(x, y)

  override def getFont: Font = g2d.getFont

  override def setFont(font: Font): Unit = g2d.setFont(font)

  override def getTranslateX: Int = g2d.getTransform.getTranslateX.toInt

  override def getTranslateY: Int = g2d.getTransform.getTranslateY.toInt

  /**
   * Set RGB value
   *
   * @param rgb rgb
   */
  override def setColor(rgb: Int): Unit = g2d.setColor(new Color(rgb))

  override def setColor(r: Int, g: Int, b: Int): Unit = g2d.setColor(new Color(r, g, b))

  override def setPixel(x: Int, y: Int, color: Int): Unit = {
    val in = new Point2D.Float(x.toFloat, y.toFloat) //todo use Point2D.Integer ??
    val dst = new Point2D.Float
    g2d.getTransform.transform(in, dst)
    val fill = if (color == 0) Color.WHITE
    else Color.BLACK
    image.setRGB(dst.x.toInt, dst.y.toInt, fill.getRGB)
  }

  override def getPixel(x: Int, y: Int): Int = {
    val in = new Point2D.Float(x.toFloat, y.toFloat) //todo use Point2D.Integer ??
    val dst = new Point2D.Float
    g2d.getTransform.transform(in, dst)
    val rgb = image.getRGB(dst.x.toInt, dst.y.toInt)
    if ((rgb & 0x00FFFFFF) == 0x00FFFFFF) 0
    else 1
  }

  override def drawString(str: String, x: Int, y: Int, anchor: Int, inverted: Boolean): Unit = {
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

  override def drawString(str: String, x: Int, y: Int, anchor: Int): Unit = {
    val metrics = g2d.getFontMetrics
    val w = metrics.stringWidth(str)
    val h = metrics.getHeight
    val x1 = adjustX(x, w, anchor)
    val y1 = adjustY(y, h, anchor)
    g2d.drawString(str, x1, y1)
  }

  override def drawSubstring(str: String, offset: Int, len: Int, x: Int, y: Int, anchor: Int): Unit = {
    val sub = str.substring(offset, offset + len)
    drawString(sub, x, y, anchor)
  }

  override def drawChar(character: Char, x: Int, y: Int, anchor: Int): Unit = {
    val str = new String(Array[Char](character))
    drawString(str, x, y, anchor)
  }

  override def drawChars(data: Array[Char], offset: Int, length: Int, x: Int, y: Int, anchor: Int): Unit = {
    val str = new String(data)
    drawString(str, x, y, anchor)
  }

  override def getStrokeStyle: Int = this.stroke

  override def setStrokeStyle(i: Int): Unit = {
    this.stroke = i
    val stroke:BasicStroke = if (i == GraphicsLCD.DOTTED) {
      val dash = Array[Float](3.0f, 3.0f)
      val dash_phase = 0.0f
      new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f, dash, dash_phase)
    }
    else if (i == GraphicsLCD.SOLID)
      new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0.0f)
    else
      throw new IllegalArgumentException("Invalid stroke")
    g2d.setStroke(stroke)
  }

  @deprecated override def drawRegionRop(src: Image, sx: Int, sy: Int, w: Int, h: Int, x: Int, y: Int, anchor: Int, rop: Int): Unit = drawRegionRop(src, sx, sy, w, h, GraphicsLCD.TRANS_NONE, x, y, anchor, rop)

  @deprecated override def drawRegionRop(src: Image, sx: Int, sy: Int, wIn: Int, hIn: Int, transform: Int, xIn: Int, yIn: Int, anchor: Int, rop: Int): Unit = {
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
      case GraphicsLCD.TRANS_MIRROR =>
        tf.scale(-1.0, 1.0)
      case GraphicsLCD.TRANS_MIRROR_ROT90 =>
        tf.scale(-1.0, 1.0)
        tf.quadrantRotate(1)
        h = w
        w = h0
      case GraphicsLCD.TRANS_MIRROR_ROT180 =>
        tf.scale(-1.0, 1.0)
        tf.quadrantRotate(2)
      case GraphicsLCD.TRANS_MIRROR_ROT270 =>
        tf.scale(-1.0, 1.0)
        tf.quadrantRotate(3)
        h = w
        w = h0
      case GraphicsLCD.TRANS_NONE =>
      case GraphicsLCD.TRANS_ROT90 =>
        tf.quadrantRotate(1)
        h = w
        w = h0
      case GraphicsLCD.TRANS_ROT180 =>
        tf.quadrantRotate(2)
      case GraphicsLCD.TRANS_ROT270 =>
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

  @deprecated override def drawRegion(src: Image, sx: Int, sy: Int, w: Int, h: Int, transform: Int, x: Int, y: Int, anchor: Int): Unit = drawRegionRop(src, sx, sy, w, h, transform, x, y, anchor, CommonLCD.ROP_COPY)

  override def drawImage(image: Image, i: Int, i1: Int, i2: Int): Unit = g2d.drawImage(image, i, i1, null)

  override def drawLine(x1: Int, y1: Int, x2: Int, y2: Int): Unit = g2d.drawLine(x1, y1, x2, y2)

  override def fillRect(x: Int, y: Int, width: Int, height: Int): Unit = g2d.fillRect(x, y, width, height)

  override def copyArea(sx: Int, sy: Int, w: Int, h: Int, x: Int, y: Int, anchor: Int): Unit = {
    g2d.copyArea(sx, sy, w, h, adjustX(x, w, anchor), adjustY(y, h, anchor))
  }

  /**
   * Adjust the x co-ordinate to use the translation and anchor values.
   */
  private def adjustX(xIn: Int, w: Int, anchor: Int): Int = { //todo make functional
    var x = xIn
    anchor & (GraphicsLCD.LEFT | GraphicsLCD.RIGHT | GraphicsLCD.HCENTER) match {
      case GraphicsLCD.LEFT =>
      case GraphicsLCD.RIGHT =>
        x -= w
      case GraphicsLCD.HCENTER =>
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
    anchor & (GraphicsLCD.TOP | GraphicsLCD.BOTTOM | GraphicsLCD.VCENTER) match {
      case GraphicsLCD.TOP =>
      case GraphicsLCD.BOTTOM =>
        y -= h
      case GraphicsLCD.VCENTER =>
        y -= h / 2
      case _ =>
        throw new RuntimeException("Bad Option")
    }
    y
  }

  override def drawRoundRect(x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int): Unit = g2d.drawRoundRect(x, y, width, height, arcWidth, arcHeight)

  override def drawRect(x: Int, y: Int, width: Int, height: Int): Unit = g2d.drawRect(x, y, width, height)

  override def drawArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int): Unit = g2d.drawArc(x, y, width, height, startAngle, arcAngle)

  override def fillArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int): Unit = g2d.fillArc(x, y, width, height, startAngle, arcAngle)

  override def drawOval(x: Int, y: Int, width: Int, height: Int): Unit = g2d.drawOval(x, y, width, height)

  override def refresh(): Unit = flush()

  override def clear(): Unit = {
    val tf = g2d.getTransform.clone.asInstanceOf[AffineTransform]
    g2d.getTransform.setToIdentity()
    g2d.setColor(Color.WHITE)
    g2d.fillRect(0, 0, fb.getWidth, fb.getHeight)
    flush()
    g2d.setTransform(tf)
  }

  override def getWidth: Int = fb.getWidth

  override def getHeight: Int = fb.getHeight

  override def getDisplay: Array[Byte] = ImageUtils.getImageBytes(image)

  override def getHWDisplay: Array[Byte] = getDisplay

  override def setContrast(i: Int): Unit = {
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
   */
  override def bitBlt(src: Array[Byte], sw: Int, sh: Int, sx: Int, sy: Int, dx: Int, dy: Int, w: Int, h: Int, rop: Int): Unit = {
    val srcI = lejos2rgb(src, sw, sh)
    val dstI = any2rgb(image)
    bitBlt(srcI, sx, sy, dstI, dx, dy, w, h, rop)
    g2d.drawImage(dstI, 0, 0, null)
  }

  override def bitBlt(src: Array[Byte], sw: Int, sh: Int, sx: Int, sy: Int, dst: Array[Byte], dw: Int, dh: Int, dx: Int, dy: Int, w: Int, h: Int, rop: Int): Unit = {
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

  override def setAutoRefresh(b: Boolean): Unit = if (this.timer_run != b) {
    this.timer_run = b
    timerUpdate()
  }

  override def setAutoRefreshPeriod(i: Int): Unit = {
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
    override def run(): Unit = refresh()
  }
}