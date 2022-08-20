package ev3dev4s.lego

import ev3dev4s.actuators.{Sound => Ev3Sound}
import ev3dev4s.measure.{Hertz, MilliSeconds}

import java.io.File

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Sound {

  /**
   * @param file .wav file to play
   */
  def startSound(file:File): Unit = {
    Ev3Sound.playSample(file)
  }

  def playSound(file:File): Unit = {
    Ev3Sound.playSampleUntilDone(file)
  }

  def playBeep(frequency: Hertz, duration: MilliSeconds): Unit = {
    Ev3Sound.playTone(frequency, duration)
  }

  def speak(say:String):Unit = {
    Ev3Sound.speak(say)
  }
}
