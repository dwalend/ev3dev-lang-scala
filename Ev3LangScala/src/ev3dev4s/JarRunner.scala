package ev3dev4s

import ev3dev4s.actuators.Sound
import ev3dev4s.measure.Conversions._

import java.net.URLClassLoader
import java.nio.file.Path

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
    val className = args(1)
    Sound.playTone(110, 200.milliseconds)

    try {
      while (keepGoing) {
        Log.log(s"Start run() of $className from $jarFile")
        val classLoader = new URLClassLoader(Array(jarFile.toUri.toURL))
        classLoader.loadClass(className + "$").getField("MODULE$").get(Array.empty[Object]).asInstanceOf[Runnable].run()
        Log.log(s"Finished run() of $className from $jarFile")
      }
    }
    catch {
      case x: Throwable =>
        x.printStackTrace()
    }
    finally {
      Log.log(s"End JarRunner ")
      Sound.playTone(55, 200.milliseconds)
    }
  }
}