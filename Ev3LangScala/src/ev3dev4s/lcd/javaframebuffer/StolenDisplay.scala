package ev3dev4s.lcd.javaframebuffer

import ev3dev4s.Log
import ev3dev4s.sysfs.Shell

/**
 * Class to allow running programs over SSH
 */
class StolenDisplay() extends DisplayInterface {
  private val DISABLE_BRICKMAN_COMMAND = "sudo systemctl stop brickman"
  private val ENABLE_BRICKMAN_COMMAND = "sudo systemctl start brickman"

  /**
   * Disable Brickman.
   */
  Log.log("Disabling Brickman service")
  Shell.execute(DISABLE_BRICKMAN_COMMAND)
  Runtime.getRuntime.addShutdownHook(new Thread(new Runnable{
    def run():Unit = restoreBrickman()
  }, "restore brickman"))

  /**
   * noop, graphics goes to the display
   */
  override def switchToGraphicsMode(): Unit = Log.log("Switch to graphics mode")

  /**
   * noop, text goes to SSH host
   */
  override def switchToTextMode(): Unit = Log.log("Switch to text mode")

  /**
   * noop, we do not have any resources
   */
  override def close(): Unit = {
    Log.log("Display close")
    // free objects
    closeFramebuffer()
  }

  override def openFramebuffer(): JavaFramebuffer = {
    if (fbInstance == null) {
      Log.log("Initialing framebuffer in fake console")
      initializeFramebuffer(new NativeFramebuffer("/dev/fb0"), true)
    }
    fbInstance
  }

  /**
   * Enable Brickman.
   */
  private def restoreBrickman(): String = {
    Log.log("Enabling Brickman service")
    Shell.execute(ENABLE_BRICKMAN_COMMAND)
  }
}
