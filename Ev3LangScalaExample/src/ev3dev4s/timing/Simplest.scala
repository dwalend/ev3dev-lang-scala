package ev3dev4s.timing

/**
 * Run via
 *
 * brickrun -r -- java -cp Ev3LangScala.jar ev3dev4s.JarRunner Ev3LangScalaExample.jar ev3dev4s.timing.Simplest
 *
 * @author David Walend
 * @since v0.0.0
 */
object Simplest extends Runnable {
  override def run(): Unit = {
    println("Hi!")
    System.exit(0)
  }

  def main(args: Array[String]): Unit =
    run()
}
