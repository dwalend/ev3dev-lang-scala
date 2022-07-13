package ev3dev4s

import ev3dev4s.actuators.Sound
import ev3dev4s.measure.Conversions.*

import java.net.URLClassLoader
import java.nio.file.Path

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object JarRunner:

  @volatile var keepGoing:Boolean = true

  /**
   * @param args First arg is a path to a .jar file. Second arg is the fully-qualified class name of a Runnable Object
   */
  def main(args: Array[String]): Unit =
    val jarFile:Path = Path.of(args(0))
    val className = args(1)
    Sound.playTone(110,200.milliseconds)

    try
      while keepGoing do
        println(s"Reloading and rerunning $className from $jarFile")
        val classLoader = new URLClassLoader(Array(jarFile.toUri.toURL))
        //todo here's the spot to pass in the name of the .jar file to watch - instead of the object instance
        classLoader.loadClass(className + "$").getField("MODULE$").get(Array.empty[Object]).asInstanceOf[Runnable].run()
        println(s"Finished run() of $className from $jarFile")
    catch
      case x: Throwable => x.printStackTrace()
    println(s"End JarRunner ")
    Sound.playTone(110,200.milliseconds)