package ev3dev4s.actuators


import ev3dev4s.sysfs.Shell

import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import java.io.File

import ev3dev4s.measure.{MilliSeconds,Percent}
import ev3dev4s.measure.Conversions.*

/**
 * drives sound via shelling out
 */
//todo someday drive the ALSA layer directly
//todo maybe https://community.oracle.com/tech/developers/discussion/1273219/example-code-to-generate-audio-tone
//todo options in https://stackoverflow.com/questions/3780406/how-to-play-a-sound-alert-in-a-java-application
//todo maybe just Toolkit.getDefaultToolkit().beep()

object Sound {
  private var volume = 50.percent

  def beep():Unit =
    Shell.execute("beep")

  def playTone(frequency: Int, duration: MilliSeconds, volume: Percent):Unit =
    this.setVolume(volume)
    this.playTone(frequency, duration)

  /**
   * Plays a tone, given its frequency and duration.
   *
   * @param frequency The frequency of the tone in Hertz (Hz).
   * @param duration  The duration of the tone4, in milliseconds.
   */
  def playTone(frequency: Int, duration: MilliSeconds):Unit =
    val cmdTone = s"beep -f $frequency -l $duration"
    Shell.execute(cmdTone)

  /**
   * Play a wav file. Must be mono, from 8kHz to 48kHz, and 8-bit or 16-bit.
   *
   * @param file   the 8-bit or 16-bit PWM (WAV) sample file
   * @param volume the volume percentage 0 - 100
   */
    //todo draw from .jar resources
  def playSample(file: File, volume: Percent):Unit =
    this.setVolume(volume)
    this.playSample(file)

  /**
   * Play a wav file. Must be mono, from 8kHz to 48kHz, and 8-bit or 16-bit.
   *
   * @param file the 8-bit or 16-bit PWM (WAV) sample file
   */
  //todo draw from .jar resources
  def playSample(file: File):Unit =
    val audioIn: AudioInputStream = AudioSystem.getAudioInputStream(file.toURI.toURL)
    val clip = AudioSystem.getClip
    clip.open(audioIn)
    clip.start()
    Thread.sleep(clip.getMicrosecondLength)
    clip.close()
    audioIn.close()

  /**
   * Set the master volume level
   *
   * @param volume 0-100
   */
  def setVolume(volume: Percent):Unit =
    this.volume = volume
    val cmdVolume = s"amixer set PCM,0 $volume%"
    Shell.execute(cmdVolume)

  /**
   * Get the current master volume level
   */ 
  def getVolume:Percent = volume

  //todo text to speech from https://www.ev3dev.org/docs/tutorials/using-ev3-speaker/
}