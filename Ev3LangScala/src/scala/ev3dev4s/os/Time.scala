package ev3dev4s.os

import ev3dev4s.scala2measure.MilliSeconds
import ev3dev4s.scala2measure.Conversions.LongConversions
/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Time {

  lazy val startTime:Long = System.currentTimeMillis()

  def now(): MilliSeconds = (System.currentTimeMillis() - startTime).ms

  def pause(milliseconds: MilliSeconds): Unit = {
    val deadline: MilliSeconds = now() + milliseconds
    System.gc()
    val sleepTime: Float = (deadline - now()).v
    if (sleepTime > 0) Thread.sleep(sleepTime.round)
  }

  def pause(): Unit =
    Thread.`yield`()
}