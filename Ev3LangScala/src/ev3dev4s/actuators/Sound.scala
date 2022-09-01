package ev3dev4s.actuators


import ev3dev4s.sysfs.Shell

import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import java.io.File
import ev3dev4s.measure.{Hertz, MilliSeconds, Percent}
import ev3dev4s.measure.Conversions._

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

  def playTone(frequency: Hertz, duration: MilliSeconds, volume: Percent):Unit = {
    this.setVolume(volume)
    this.playTone(frequency, duration)
  }
  /**
   * Plays a tone, given its frequency and duration.
   *
   * @param frequency The frequency of the tone in Hertz (Hz).
   * @param duration  The duration of the tone4, in milliseconds.
   */
  def playTone(frequency: Hertz, duration: MilliSeconds):Unit = {
    val cmdTone = s"/usr/bin/beep -f ${frequency.round} -l ${duration.round}"
    Shell.execute(cmdTone)
  }
  /**
   * Play a wav file. Must be mono, from 8kHz to 48kHz, and 8-bit or 16-bit.
   *
   * @param file   the 8-bit or 16-bit PWM (WAV) sample file
   * @param volume the volume percentage 0 - 100
   */
    //todo draw from .jar resources
  def playSample(file: File, volume: Percent = this.volume):Unit = {
    this.setVolume(volume)
    this.playSampleUntilDone(file)
  }

  /**
   * Play a wav file. Must be mono, from 8kHz to 48kHz, and 8-bit or 16-bit.
   *
   * @param file the 8-bit or 16-bit PWM (WAV) sample file
   */
  //todo draw from .jar resources
  def playSampleUntilDone(file: File):Unit = {
    val audioIn: AudioInputStream = AudioSystem.getAudioInputStream(file.toURI.toURL)
    val clip = AudioSystem.getClip
    clip.open(audioIn)
    clip.start()
    Thread.sleep(clip.getMicrosecondLength)
    clip.close()
    audioIn.close()
  }
  /**
   * Set the master volume level
   *
   * @param volume 0-100
   */
  def setVolume(volume: Percent):Unit = {
    this.volume = volume
    val cmdVolume = s"/usr/bin/amixer set PCM,0 $volume%"
    Shell.execute(cmdVolume)
  }

  /**
   * Get the current master volume level
   */ 
  def getVolume:Percent = volume


  /**
   *
   * @param say
   * this is what you want it to say
   * @param voice
   * en works, mb-en1 is very quiet
   * to change the voice, add +f/+m and a number 1-4
   * other fun modifiers for the voice (no spaces): +croak, +whisper
   * @param pitch
   * pitch to say it at, sale from 0-99
   * we don't know where they are as notes. will figure out
   * @param amplitude
   * volume, scale from 0-200
   * @param speeeeed
   * words per minute. lower limit 80, no upper limit (stop at about 500)
   * from
   * http://espeak.sourceforge.net/commands.html
   */
  def speak(
             say: String,
             voice: String = "en",
             pitch: Int = 50,
             amplitude: Int = 100,
             speeeeed: Int = 150
           ): Unit = {
    val speakCommand = s"""/usr/bin/espeak -v$voice -p$pitch -a$amplitude -s$speeeeed "$say" --stdout | aplay"""
    Shell.execute(Array("/bin/sh", "-c", speakCommand))
  }
}
