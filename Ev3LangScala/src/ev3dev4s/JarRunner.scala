package ev3dev4s

import ev3dev4s.actuators.{Ev3Led, Sound}
import ev3dev4s.measure.Conversions._
import ev3dev4s.sensors.Ev3KeyPad

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
    Sound.playTone(55.Hz, 200.milliseconds)
    try {
      runIt(jarFile, className)
    }
    finally {
      Log.log(s"End JarRunner ")
      Sound.playTone(55.Hz, 200.milliseconds)
    }
  }

  @tailrec
  def runIt(jarFile:Path,className:String):Unit = {
    Sound.playTone(110.Hz, 200.milliseconds)
    try {
      Log.log(s"Start run() of $className from $jarFile")
      val classLoader = new URLClassLoader(Array(jarFile.toUri.toURL))
      classLoader.loadClass(className + "$").getField("MODULE$").get(Array.empty[Object]).asInstanceOf[Runnable].run()
    }
    catch {
      case x: Throwable =>
        Log.log("Caught in top-level",x)
        Ev3Led.writeBothRed()
        Sound.playTone(55.Hz, 200.milliseconds)
        Ev3KeyPad.blockUntilAnyKey()
        Ev3Led.writeBothOff()
    }
    finally {
      Sound.playTone(110.Hz, 200.milliseconds)
      Log.log(s"Finished run() of $className from $jarFile")
    }
    runIt(jarFile, className)
  }
}