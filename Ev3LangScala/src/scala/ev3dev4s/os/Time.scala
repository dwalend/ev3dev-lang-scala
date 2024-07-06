package ev3dev4s.os

import ev3dev4s.scala2measure.MilliSeconds

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Time {

  def now(): Long = System.currentTimeMillis()

  def pause(milliseconds: MilliSeconds): Unit = {
    val deadline: Float = now() + milliseconds.v
    System.gc()
    val sleepTime: Float = deadline - now()
    if (sleepTime > 0) Thread.sleep(sleepTime.round)
  }

  def pause(): Unit =
    Thread.`yield`()
}