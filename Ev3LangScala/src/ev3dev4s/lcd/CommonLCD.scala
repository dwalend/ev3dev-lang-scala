/*package ev3dev4s.lcd

object CommonLCD {
  /**
   * Common raster operations for use with bitBlt
   */
  val ROP_CLEAR = 0x00000000
  val ROP_AND = 0xff000000
  val ROP_ANDREVERSE = 0xff00ff00
  val ROP_COPY = 0x0000ff00
  val ROP_ANDINVERTED = 0xffff0000
  val ROP_NOOP = 0x00ff0000
  val ROP_XOR = 0x00ffff00
  val ROP_OR = 0xffffff00
  val ROP_NOR = 0xffffffff
  val ROP_EQUIV = 0x00ffffff
  val ROP_INVERT = 0x00ff00ff
  val ROP_ORREVERSE = 0xffff00ff
  val ROP_COPYINVERTED = 0x0000ffff
  val ROP_ORINVERTED = 0xff00ffff
  val ROP_NAND = 0xff0000ff
  val ROP_SET = 0x000000ff
}

trait CommonLCD {

  def refresh():Unit


  def clear():Unit

  /**
   * Return the width of the associated drawing surface.
   * <br><b>Note</b>: This is a non standard method.
   *
   * @return width of the surface
   */
  def getWidth:Int

  /**
   * Return the height of the associated drawing surface.
   * <br><b>Note</b>: This is a non standard method.
   *
   * @return height of the surface.
   */
  def getHeight:Int


  def getDisplay:Array[Byte]


  def getHWDisplay:Array[Byte]

  /**
   * Set the LCD contrast.
   *
   * @param contrast 0 blank 0x60 full on
   */
  def setContrast(contrast: Int):Unit


  def bitBlt(src: Array[Byte], sw: Int, sh: Int, sx: Int, sy: Int, dx: Int, dy: Int, w: Int, h: Int, rop: Int):Unit


  def bitBlt(src: Array[Byte], sw: Int, sh: Int, sx: Int, sy: Int, dst: Array[Byte], dw: Int, dh: Int, dx: Int, dy: Int, w: Int, h: Int, rop: Int):Unit

  /**
   * Turn on/off the automatic refresh of the LCD display. At system startup
   * auto refresh is on.
   *
   * @param on true to enable, false to disable
   */
  def setAutoRefresh(on: Boolean):Unit

  /**
   * Set the period used to perform automatic refreshing of the display.
   * A period of 0 disables the refresh.
   *
   * @param period time in ms
   * @return the previous refresh period.
   */
  def setAutoRefreshPeriod(period: Int):Unit
}*/