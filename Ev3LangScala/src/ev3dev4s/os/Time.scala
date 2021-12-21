package ev3dev4s.os

import ev3dev4s.measure.Conversions.*
import ev3dev4s.measure.MilliSeconds

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Time:

  def now():Long = System.currentTimeMillis()

  def pause(milliseconds:MilliSeconds = 1000.milliseconds):Unit =
    val deadline = now() + milliseconds.value
    System.gc()
    val sleepTime = deadline - now()
    if(sleepTime > 0) Thread.sleep(sleepTime)