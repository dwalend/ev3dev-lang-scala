package ev3dev4s.os

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Time:

  def now():Long = System.currentTimeMillis()

  def pause(milliseconds:Long = 1000L):Unit =
    val deadline = now() + milliseconds
    System.gc()
    val sleepTime = deadline - now()
    if(sleepTime > 0) Thread.sleep(sleepTime)