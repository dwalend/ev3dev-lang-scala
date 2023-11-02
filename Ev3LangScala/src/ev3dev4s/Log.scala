package ev3dev4s

import java.io.PrintStream

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Log {
  private val logStream: PrintStream = Option(System.getProperty("logFile")).map(new PrintStream(_)).getOrElse(System.out)

  def log(text: String): Unit = logStream.println(s"${System.currentTimeMillis()} $text")

  def log(text: String, x: Throwable): Unit = {
    logStream.println(s"${System.currentTimeMillis()} $text")
    x.printStackTrace(logStream)
  }

}
