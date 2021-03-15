package ev3dev4s

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Log {
  def log(text: String): Unit = {
    Log.log(s"${System.currentTimeMillis()} $text")
  }
}
