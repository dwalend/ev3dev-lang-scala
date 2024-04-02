package ev3dev4s

import ev3dev4s.actuators.{Ev3Led, MotorPortScanner, Sound}
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.sensors.Ev3KeyPad
import ev3dev4s.measured.dimension.milli
import ev3dev4s.measured.dimension.Dimensions.{hertz,second, `*`, given}

import java.net.URLClassLoader
import java.nio.file.Path
import scala.annotation.tailrec

/**
 * Loads a .jar file, then run()s a Runnable.
 *
 * @author David Walend
 * @since v0.0.0
 */
object JarRunner {

  @volatile var keepGoing: Boolean = true

  /**
   * Loads the .jar file named in the first arg, runs the Runnable named in the second arg, until something sets keepGoing to false.
   *
   * @param args the first arg is a path to a .jar file. The second arg is the fully-qualified class name of a Runnable Object
   */
  def main(args: Array[String]): Unit = {

    val jarFile: Path = Path.of(args(0))
    val className: String = args(1)
    Sound.playTone(55 * hertz, 200 * milli(second))
    Ev3Led.writeBothOff()
    try {
      Sound.playTone(110 * hertz, 200 * milli(second))
      runIt(jarFile, className)
    }
    finally {
      Log.log(s"End JarRunner ")
      Sound.playTone(55 * hertz, 200 * milli(second))
    }
  }

  @tailrec
  def runIt(jarFile:Path,className:String):Unit = {
    try {
      Log.log(s"Start run() of $className from $jarFile")
      val classLoader = new URLClassLoader(Array(jarFile.toUri.toURL))
      classLoader.loadClass(className + "$").getField("MODULE$").get(Array.empty[Object]).asInstanceOf[Runnable].run()
    }
    catch {
      case x: Throwable =>
        Log.log("Caught in top-level",x)
        MotorPortScanner.stopAllMotors()
        Ev3Led.writeBothRed()
        Lcd.clear()
        x.getMessage.grouped(11).take(3).zipWithIndex.foreach { chunk =>
          Lcd.set(chunk._2, chunk._1)
        }
        Lcd.set(3,"Push Button")
        Lcd.flush()
        Sound.playTone(55 * hertz, 200 * milli(second))
        Ev3KeyPad.blockUntilAnyKey()
        Ev3Led.writeBothOff()
    }
    finally {
      Sound.playTone(110 * hertz, 200 * milli(second))
      Log.log(s"Finished run() of $className from $jarFile")
    }
    runIt(jarFile, className)
  }
}