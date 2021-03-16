package ev3dev4s.lcd.examples

import ev3dev4s.Log
import ev3dev4s.lcd.{GraphicsLCD, Lcd}

/**
 * @author David Walend
 * @since v0.0.0
 */
object HelloLcd extends Runnable {

  def main(args: Array[String]): Unit = run()

  override def run(): Unit = {
    Log.log("start")
    val lcd: GraphicsLCD = Lcd.getInstance()

    //clear the LCD
    lcd.setColor(GraphicsLCD.WHITE)
    lcd.fillRect(0, 0, lcd.getWidth, lcd.getHeight)

    lcd.setColor(GraphicsLCD.BLACK)
    lcd.drawString("Hello World!",0,30,GraphicsLCD.LEFT+GraphicsLCD.BOTTOM)
    lcd.refresh()
    Log.log("showing Hello World")

    Thread.sleep(10000)
  }
}

/*
Just after code copied over: 59901 ms from start to hello world

1615859790110 start
1615859790859 Instancing LCD for Stretch
1615859805480 initializing new real display
1615859805535 Initialing system console
1615859805547 Opening TTY
1615859806296 Opening FB 0
1615859810524 map vt5 -> fb 0
1615859812105 Switching console to text mode
1615859812223 Initialing framebuffer in system console
1615859812234 Switching console to graphics mode
1615859812312 Loading framebuffer
1615859818016 Opened LinuxFB, mode 178x128x32bpp
1615859818030 Closing LinuxFB
1615859819255 Framebuffer BitFramebufferProvider is not compatible
1615859819918 Opened LinuxFB, mode 178x128x32bpp
1615859819981 Framebuffer RGBFramebufferProvider is compatible
1615859819992 Clearing framebuffer
1615859833815 Drawing frame on framebuffer
1615859833845 Storing framebuffer snapshot
1615859834061 Drawing frame on framebuffer
1615859849982 Drawing frame on framebuffer
1615859850011 showing Hello World
1615859860104 Closing system console
1615859860190 Closing LinuxFB
 */
