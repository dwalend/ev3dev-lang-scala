package net.walend.lessons

import ev3dev4s.Log
import ev3dev4s.actuators.Sound
import ev3dev4s.measure.Conversions._
import ev3dev4s.sysfs.ChannelRereader
import net.walend.cargoconnect.Robot

import java.nio.file.attribute.FileTime
import java.nio.file.{FileSystemException, Files, Path, Paths}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
case class Controller(actions:Array[TtyMenuAction],setSensorRows:() => Unit) extends Runnable {

  override def run(): Unit = {
    try {
      Robot.check()
      val timeThread = new Thread(UpdateScreenAndCheckJar)
      timeThread.setDaemon(true)
      timeThread.start()

      Sound.playTone(220, 200.milliseconds)
      Sound.playTone(440, 200.milliseconds)
      ttyMenu.loop()
    }
    finally {
      UpdateScreenAndCheckJar.keepGoing = false
      Sound.playTone(440, 200.milliseconds)
      Sound.playTone(220, 200.milliseconds)
    }
  }

  val ttyMenu: TtyMenu = TtyMenu(actions :+ Reload, setLcd)

  def setLcd(ttyMenu: TtyMenu): Unit = {
    ttyMenu.setActionRow(3)
    setSensorRows()
  }

  var startTime: Long = System.currentTimeMillis()

  def elapsedTime: Long = (System.currentTimeMillis() - startTime) / 1000

  object UpdateScreenAndCheckJar extends Runnable {
    @volatile var keepGoing = true

    val expectedJarFile: Path = Paths.get(this.getClass.getProtectionDomain.getCodeSource.getLocation.getPath)
    val currentJarLastModifiedTime: FileTime = Files.getLastModifiedTime(expectedJarFile)
    val jarFileSizeFile: Path = expectedJarFile.getParent.resolve("expectedJarFileSize.txt") //todo use current working directory

    @volatile var expectedJarLastModifiedTime = currentJarLastModifiedTime

    override def run(): Unit = {
      while (keepGoing) {
        if (!ttyMenu.doingAction) {
          ttyMenu.drawScreen()
          checkJarUploaded()
        }

        Thread.sleep(500)
      }
    }

    private def checkJarUploaded(): Unit = {
      try {
        val jarLastModifiedTime = Files.getLastModifiedTime(expectedJarFile)

        if (expectedJarLastModifiedTime != jarLastModifiedTime) {
          val jarFileSize = Files.size(expectedJarFile)
          val expectedJarFileSize = ChannelRereader.readAsciiInt(jarFileSizeFile)
          if (jarFileSize == expectedJarFileSize) {
            expectedJarLastModifiedTime = jarLastModifiedTime
            Log.log(s"New .jar file is complete")
            Sound.playTone(175, 200.milliseconds)
          }
        }
      } catch {
        case fxs: FileSystemException =>
          fxs.printStackTrace()
          Log.log(s"${
            fxs.getClass.getName
          } ${
            fxs.getMessage
          } caught. Will keep going.")
      }
    }

  }

  object Reload extends TtyMenuAction {
    override def act(ttyMenu: TtyMenu): Unit = ttyMenu.stopLoop()
  }
}

