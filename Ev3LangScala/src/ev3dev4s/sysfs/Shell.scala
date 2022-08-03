package ev3dev4s.sysfs

import ev3dev4s.Log

object Shell {
  /**
   * Execute a command passed as a parameter
   *
   * @param command Command to execute in Linux
   * @return Result from the command
   */
  def execute(command: String): String = {
    Log.log("Command: " + command)

    val p: Process = Runtime.getRuntime.exec(command)
    p.waitFor

    import java.io.ByteArrayOutputStream
    val output = new ByteArrayOutputStream
    val buffer = new Array[Byte](1024)
    var length = 0
    while ({
      length = p.getInputStream.read(buffer)
      length != -1 })
    { output.write(buffer, 0, length) }

    p.getInputStream.close()
    output.toString("UTF-8")
  }

}