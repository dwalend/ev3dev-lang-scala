package bleep.scripts

import bleep.model.{ProjectName, ScriptName}
import bleep.{BleepScript, Commands, Started}

import java.nio.file.Path

object ScpJar extends BleepScript("ScpJar") {
  override def run(started: Started, commands: Commands, args: List[String]): Unit = {
    commands.script(ScriptName("jar"),args)

    val projectName = ProjectName(args.head)
    val jarPath = Jar.jarPath(started,projectName)

    scpJar(jarPath,projectName)
  }

  /**
   * Copy a jar file to the ev3 via scp - and write the expected size in another file (in hopes of detecting that the
   * jar file is complete.)
   */
  def scpJar(jarPath: Path,projectName: ProjectName) = {
    Scp.scpFile(jarPath,s"${projectName.value}.jar")
  }
}

