package ev3dev4s

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Log:
  def log(text: String): Unit =
    println(s"${System.currentTimeMillis()} $text")
