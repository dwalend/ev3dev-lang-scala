package ev3dev4s.lcd.tty.examples

import ev3dev4s.actuators.Sound
import ev3dev4s.{Ev3System, Log}
import ev3dev4s.lcd.tty.Lcd
import ev3dev4s.scala2measure.Conversions.IntConversions
import ev3dev4s.sensors.Ev3Gyroscope
import ev3dev4s.sysfs.{ChannelRereader, UnpluggedException}

import java.nio.file.attribute.FileTime
import java.nio.file.{FileSystemException, Files, Path, Paths}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object TtySensorsAndMenu extends Runnable {
  def main(args: Array[String]): Unit =
    run()

  override def run(): Unit = {
    val timeThread = new Thread(PeriodicUpdate)
    timeThread.setDaemon(true)
    timeThread.start()

    ttyMenu.run()

    PeriodicUpdate.keepGoing = false
  }

  val ttyMenu: TtyMenu = {
    val actions: Array[TtyMenuAction] = Array(
      LedAction("Green", { () =>
        Ev3System.leftLed.writeGreen()
        Ev3System.rightLed.writeGreen()
      }),
      LedAction("Yellow", { () =>
        Ev3System.leftLed.writeYellow()
        Ev3System.rightLed.writeYellow()
      }),
      LedAction("Red", { () =>
        Ev3System.leftLed.writeRed()
        Ev3System.rightLed.writeRed()
      }),
      DespinGyro,
      TtyMenu.Reload
    )
    TtyMenu(actions, setLcd)
  }

  def setLcd(ttyMenu: TtyMenu): Unit = {
    ttyMenu.setActionRow(2)
    setSensorRows()
  }

  var startTime: Long = System.currentTimeMillis()

  def elapsedTime: Long = (System.currentTimeMillis() - startTime) / 1000

  val gyroscope: Ev3Gyroscope = Ev3System.portsToSensors.values.collectFirst { case g: Ev3Gyroscope => g }.get

  def setSensorRows(): Unit = {
    Lcd.set(0, s"${elapsedTime}s", Lcd.RIGHT)
    val heading: String = UnpluggedException.safeString(() => s"${gyroscope.headingMode().readHeading().round}d")
    Lcd.set(0, heading, Lcd.LEFT)
  }

  object PeriodicUpdate extends Runnable {
    @volatile var keepGoing = true

    val expectedJarFile: Path = Paths.get(this.getClass.getProtectionDomain.getCodeSource.getLocation.getPath)
    val currentJarLastModifiedTime: FileTime = Files.getLastModifiedTime(expectedJarFile)
    val jarFileSizeFile: Path = expectedJarFile.getParent.resolve("expectedJarFileSize.txt") //todo use current working directory

    @volatile var expectedJarLastModifiedTime = currentJarLastModifiedTime

    override def run(): Unit =
      while (keepGoing) {
        if (!ttyMenu.doingAction) {
          ttyMenu.drawScreen()
          checkJarUploaded()
        }
        Thread.sleep(500)
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
            Sound.playTone(175.Hz, 200.milliseconds)
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
}

object DespinGyro extends TtyMenuAction {

  val gyroscope: Ev3Gyroscope = Ev3System.portsToSensors.values.collectFirst { case g: Ev3Gyroscope => g }.get

  override def run(menu: TtyMenu): Unit =
    gyroscope.despin()
}