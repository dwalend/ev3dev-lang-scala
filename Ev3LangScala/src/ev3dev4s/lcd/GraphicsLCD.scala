package ev3dev4s.lcd

import java.awt.Image
import java.awt.Font

object GraphicsLCD {
  val TRANS_MIRROR = 2
  val TRANS_MIRROR_ROT180 = 1
  val TRANS_MIRROR_ROT270 = 4
  val TRANS_MIRROR_ROT90 = 7
  val TRANS_NONE = 0
  val TRANS_ROT180 = 3
  val TRANS_ROT270 = 6
  val TRANS_ROT90 = 5
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
  /* Public color definitions NOT Standard*/ val BLACK = 0
  val WHITE = 0xffffff
}

trait GraphicsLCD extends CommonLCD {
  /**
   * Method to set a pixel on the screen.
   *
   * @param x     the x coordinate
   * @param y     the y coordinate
   * @param color the pixel color (0 = white, 1 = black)
   */
  def setPixel(x: Int, y: Int, color: Int):Unit

  /**
   * Method to get a pixel from the screen.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   * @return the pixel color (0 = white, 1 = black)
   */
  def getPixel(x: Int, y: Int):Int

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
  def drawString(str: String, x: Int, y: Int, anchor: Int, inverted: Boolean):Unit

  /**
   * Draws the specified String using the current font and color. x and y
   * give the location of the anchor point.
   *
   * @param str    the String to be drawn
   * @param x      the x coordinate of the anchor point
   * @param y      the y coordinate of the anchor point
   * @param anchor the anchor point for positioning the text
   */
  def drawString(str: String, x: Int, y: Int, anchor: Int):Unit

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
  def drawSubstring(str: String, offset: Int, len: Int, x: Int, y: Int, anchor: Int):Unit

  /**
   * Draw a single character to the graphics surface using the current color.
   *
   * @param character the character to draw
   * @param x         the x coordinate of the anchor point
   * @param y         the x coordinate of the anchor point
   * @param anchor    the anchor point used to position the text.
   */
  def drawChar(character: Char, x: Int, y: Int, anchor: Int):Unit

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
  def drawChars(data: Array[Char], offset: Int, length: Int, x: Int, y: Int, anchor: Int):Unit

  /**
   * Return the current stroke style.
   *
   * @return current style.
   */
  def getStrokeStyle:Int

  /**
   * Set the stroke style to be used for drawing operations.
   *
   * @param style new style.
   */
  def setStrokeStyle(style: Int):Unit

  /**
   * Draw the specified image to the graphics surface, using the supplied rop.
   * <br><b>Note</b>: This is a non standard method.
   * Added because without it, it is very
   * hard to invert/manipulate an image, or screen region
   *
   * @param src    image to draw (may be null for ops that do not require input.
   * @param sx     x offset in the source
   * @param sy     y offset in the source
   * @param w      width of area to draw
   * @param h      height of area to draw.
   * @param x      destination
   * @param y      destination
   * @param anchor location of the anchor point
   * @param rop    drawing operation.
   * @see Image
   */
  def drawRegionRop(src: Image, sx: Int, sy: Int, w: Int, h: Int, x: Int, y: Int, anchor: Int, rop: Int):Unit

  /**
   * Draw the specified region of the source image to the graphics surface
   * after applying the requested transformation, use the supplied rop.
   * <br>NOTE: When calculating the anchor point this method assumes that
   * a transformed version of the source width/height should be used.
   *
   * @param src       The source image
   * @param sx        x coordinate of the region
   * @param sy        y coordinate of the region
   * @param w         width of the region
   * @param h         height of the region
   * @param transform the required transform
   * @param x         x coordinate of the anchor point
   * @param y         y coordinate of the anchor point
   * @param anchor    type of anchor
   * @param rop       raster operation used to draw the output.
   */
  def drawRegionRop(src: Image, sx: Int, sy: Int, w: Int, h: Int, transform: Int, x: Int, y: Int, anchor: Int, rop: Int):Unit

  /**
   * Draw the specified region of the supplied image to the graphics surface.
   * NOTE: Transforms are not currently supported.
   *
   * @param src       image to draw (may be null for ops that do not require input.
   * @param sx        x offset to the region
   * @param sy        y offset to the region
   * @param w         width of the region
   * @param h         height of the region
   * @param transform transform
   * @param x         destination
   * @param y         destination
   * @param anchor    location of the anchor point
   * @see Image
   */
  def drawRegion(src: Image, sx: Int, sy: Int, w: Int, h: Int, transform: Int, x: Int, y: Int, anchor: Int):Unit

  /**
   * Draw the specified image to the graphics surface, using the supplied rop.
   *
   * @param src    image to draw (may be null for ops that do not require input.
   * @param x      destination
   * @param y      destination
   * @param anchor location of the anchor point
   * @see Image
   */
  def drawImage(src: Image, x: Int, y: Int, anchor: Int):Unit

  /**
   * Draw a line between the specified points, using the current color and style.
   *
   * @param x0 x start point
   * @param y0 y start point
   * @param x1 x end point
   * @param y1 y end point
   */
  def drawLine(x0: Int, y0: Int, x1: Int, y1: Int):Unit

  /**
   * Draw an arc, using the current color and style.
   */
  def drawArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int):Unit

  /**
   * Draw a filled arc, using the current color.
   */
  def fillArc(x: Int, y: Int, width: Int, height: Int, startAngle: Int, arcAngle: Int):Unit

  /**
   * Draw a rounded rectangle.
   */
  def drawRoundRect(x: Int, y: Int, width: Int, height: Int, arcWidth: Int, arcHeight: Int):Unit

  /**
   * Draw a rectangle using the current color and style.
   */
  def drawRect(x: Int, y: Int, width: Int, height: Int):Unit

  /**
   * Draw a filled rectangle using the current color.
   */
  def fillRect(x: Int, y: Int, w: Int, h: Int):Unit

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
  def copyArea(sx: Int, sy: Int, w: Int, h: Int, x: Int, y: Int, anchor: Int):Unit

  /**
   * Return the currently selected font object.
   *
   * @return Current font.
   */
  def getFont:Font

  /**
   * Set the current font
   *
   * @param f the font
   */
  def setFont(f: Font):Unit

  /**
   * Translates the origin of the graphics context to the point
   * (x, y) in the current coordinate system. Calls are cumulative.
   *
   * @param x the new translation origin x value
   * @param y new translation origin y value
   * @see #getTranslateX()
   * @see #getTranslateY()
   */
  def translate(x: Int, y: Int):Unit

  /**
   * Gets the X coordinate of the translated origin of this graphics context.
   *
   * @return X of current origin
   */
  def getTranslateX:Int

  /**
   * Gets the Y coordinate of the translated origin of this graphics context.
   *
   * @return Y of current origin
   */
  def getTranslateY:Int

  /**
   * Set the current drawing color. The value is in the format 0x00RRGGBB.
   * NOTE. Currently only black and white is supported. any non black color
   * is treated as white!
   *
   * @param rgb new color.
   */
  def setColor(rgb: Int):Unit

  /**
   * Sets the current color to the specified RGB values.
   *
   * @param red   the red component
   * @param green the green component
   * @param blue  the blue
   * @throws IllegalArgumentException if any of the color components
   *                                  are outside of range <code>0-255</code>
   */
  def setColor(red: Int, green: Int, blue: Int):Unit

  /**
   * Draws the outline of an oval. The result is a circle or ellipse that fits within the rectangle specified by the x, y, width, and height arguments.
   *
   * The oval covers an area that is width + 1 pixels wide and height + 1 pixels tall.
   *
   * @param x      the x coordinate of the upper left corner of the oval to be drawn.
   * @param y      the y coordinate of the upper left corner of the oval to be drawn.
   * @param width  the width of the oval to be drawn.
   * @param height the height of the oval to be drawn.
   */
  def drawOval(x: Int, y: Int, width: Int, height: Int):Unit
}