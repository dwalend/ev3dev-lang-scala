package ev3dev4s.timing

import ev3dev4s.Ev3System

/**
 * Run via
 *
 * brickrun -r -- java -cp Ev3LangScala.jar ev3dev4s.JarRunner Ev3LangScalaExample.jar ev3dev4s.timing.Simplest
 *
 * @author David Walend
 * @since v0.0.0
 */
object LED extends Runnable:
  override def run(): Unit =
    Ev3System.leftLed.writeYellow()

    System.exit(0)

  def main(args: Array[String]): Unit =
    run()
