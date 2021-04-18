package ev3dev4s.minlcd

import com.sun.jna.Pointer
import ev3dev4s.Log
import ev3dev4s.actuators.Ev3Led
import ev3dev4s.lcd.NativeConstants.{KD_GRAPHICS, KD_TEXT, K_OFF, MAP_SHARED, O_RDONLY, O_RDWR, PROT_READ, PROT_WRITE, SIGUSR2, VT_PROCESS}
import ev3dev4s.lcd.NativeTTYStructures.vt_mode
import ev3dev4s.lcd.{NativeConstants, NativeFile, NativeFramebuffer, NativeFramebufferStructures, NativeTTY}
import ev3dev4s.minlcd.Lcd.{bufferSize, writeBuffer, writePixels}
import ev3dev4s.sensors.Ev3KeyPad

/**
 * A minimal lcd implementation translating and modifying https://github.com/theZiz/ev3c ( http://ziz.gp2x.de/ev3c_documentation/files/include/ev3c_lcd-h.html
 *
 * @author David Walend
 * @since v0.0.0
 */
object Lcd {

  Log.log("Start Lcd")
  //todo all of this can go into NativeTTY
  val tty = new NativeTTY("/dev/tty", O_RDWR) //todo try O_RDONLY
  Log.log("made tty")
  tty.setKeyboardMode(K_OFF) //turn off the keyboard
  Log.log("set keyboard mode")
  tty.setConsoleMode(KD_GRAPHICS) //set graphics mode
  Log.log("set graphics mode")
  val vtm = new vt_mode
  Log.log("set vt mode")
  vtm.mode = VT_PROCESS.toByte
  vtm.relsig = SIGUSR2.toByte
  vtm.acqsig = SIGUSR2.toByte
  tty.setVTmode(vtm) // wait for mode switch
  Log.log("tty Complete")

  val frameBuffer: NativeFramebuffer = new NativeFramebuffer("/dev/fb0")

  //todo all of this can go into NativeFramebuffer
  val fixedInfo: NativeFramebufferStructures.fb_fix_screeninfo = frameBuffer.getFixedScreenInfo
  val varInfo: NativeFramebufferStructures.fb_var_screeninfo = frameBuffer.getVariableScreenInfo
  val height: Int = varInfo.yres
  val width:Int = varInfo.xres
  val stride: Int = fixedInfo.line_length
  val bufferSize: Int = height * stride
  val videoMem: Pointer = frameBuffer.mmap(bufferSize)
  Log.log("frameBuffer Complete")


  def writeBuffer(bytes:Array[Byte]):Unit = {
    videoMem.write(0, bytes, 0, bufferSize)
    frameBuffer.msync(videoMem, bufferSize, NativeConstants.MS_SYNC)
  }

  def writePixels(pixels: Pixels):Unit = {
//    writeBuffer(pixels.toBytes)
  }

  lazy val whiteBuffer: Array[Byte] = Array.fill[Byte](bufferSize)(0x0)
  def clear():Unit = {
    Log.log("start clear()")
    writeBuffer(whiteBuffer)
    Log.log("clear() Complete")
  }

  def close():Unit = {
    Log.log("start close")
    frameBuffer.munmap(videoMem,bufferSize)
    frameBuffer.close()
    tty.setConsoleMode(KD_TEXT)
    tty.close()
    Log.log("finish close")
  }
}

class Pixels {
  val pixel2D: Array[Boolean] = Array.fill[Boolean](Lcd.height*Lcd.width)(false)

  def set(x:Int,y:Int,on:Boolean): Unit = pixel2D.update(y*Lcd.width + x,on)
/*
  private val zeroToSeven: Seq[Int] = 0 until 8
  def toBytes:Array[Byte] = {

    def toByte(start:Int):Byte = {
      zeroToSeven.foldRight[Byte](0x0){(i,byt) =>
        val shifted:Byte = byt << 1
        val lowest:Byte = if(pixel2D(start+i)) 0x1 else 0x0
        shifted + lowest
      }
    }
    
    Array.tabulate[Byte](Lcd.bufferSize){ i =>
      toByte(i)
    }
  }

 */
}

object LcdTest extends Runnable{
  override def run(): Unit = {
    Ev3Led.writeBothYellow()
    //first goal init and close
    Lcd.clear()

    //second goal - draw a black screen
    //val blackBuffer: Array[Byte] = Array.fill[Byte](bufferSize)(0xf)
    //writeBuffer(blackBuffer)


    //third goal - draw a black square on the LCD
    /*
    val centerH = Lcd.height/2
    val centerW = Lcd.width/2
    val pixels = new Pixels
    for(i <- -10 to 10){
      pixels.set(centerW+i,centerH-10,true)
      pixels.set(centerW+i,centerH+10,true)
      pixels.set(centerW-10,centerH+i,true)
      pixels.set(centerW+10,centerH+i,true)
    }
    writePixels(pixels)
    */
    //big goal - draw a letter
    Log.log("Waiting for escape button")

    val (key,state) = Ev3KeyPad.blockUntilAnyKey()
    if(key == Ev3KeyPad.ESCAPE) System.exit(0)
    if(state == Ev3KeyPad.RELEASED) {
      Lcd.close()
      Ev3Led.writeBothGreen()
    }
  }
}
