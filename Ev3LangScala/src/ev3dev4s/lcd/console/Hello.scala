package ev3dev4s.lcd.console

import ev3dev4s.sysfs.Shell

import java.io.{FileOutputStream, PrintStream}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Hello extends Runnable {
  def main(args: Array[String]): Unit = {
    run()
  }

  override def run(): Unit = {
    Shell.execute("setfont Uni3-TerminusBold32x16")
    
    val printStream = new PrintStream(new FileOutputStream("/dev/tty"))
    printStream.print("Blärt!")
    printStream.flush()
    Thread.sleep(10000)
  }
}
