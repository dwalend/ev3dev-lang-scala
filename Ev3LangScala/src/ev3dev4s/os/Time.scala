package ev3dev4s.os

import ev3dev4s.measured.dimension.Dimensions.second
import ev3dev4s.measured.dimension.{Time, milli}


/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Time {

  def now(): Long = System.currentTimeMillis()

  def pause(time: Time): Unit = {
    val deadline: Float = now() + time.in(milli(second))
    System.gc()
    val sleepTime: Float = deadline - now()
    if (sleepTime > 0) Thread.sleep(sleepTime.round)
  }

  def pause(): Unit =
    Thread.`yield`()
}