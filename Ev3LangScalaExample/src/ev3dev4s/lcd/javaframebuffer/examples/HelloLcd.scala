package ev3dev4s.lcd.javaframebuffer.examples

import ev3dev4s.Log
import ev3dev4s.lcd.javaframebuffer.Lcd

/**
 * @author David Walend
 * @since v0.0.0
 */
object HelloLcd extends Runnable {

  def main(args: Array[String]): Unit = run()

  override def run(): Unit = {
    Log.log("start")

    //clear the LCD
    Lcd.setColor(Lcd.WHITE)
    Lcd.fillRect(0, 0, Lcd.getWidth, Lcd.getHeight)

    Lcd.setColor(Lcd.BLACK)
    Lcd.drawString("Hello World!",0,30,Lcd.LEFT+Lcd.BOTTOM)
    Lcd.refresh()
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

/* Deleted the FrameBufferProvider business and the BitFrameBuffer: 56850 ms = 3 seconds saved
1615861231545 start
1615861232407 Instancing LCD for Stretch
1615861246432 initializing new real display
1615861246492 Initialing system console
1615861246506 Opening TTY
1615861247251 Opening FB 0
1615861251443 map vt5 -> fb 0
1615861252929 Switching console to text mode
1615861253111 Initialing framebuffer in system console
1615861253121 Switching console to graphics mode
1615861258173 Opened LinuxFB, mode 178x128x32bpp
1615861258190 Clearing framebuffer
1615861272370 Drawing frame on framebuffer
1615861272401 Storing framebuffer snapshot
1615861272600 Drawing frame on framebuffer
1615861288343 Drawing frame on framebuffer
1615861288395 showing Hello World
1615861298474 Closing system console
1615861298563 Closing LinuxFB
 */
