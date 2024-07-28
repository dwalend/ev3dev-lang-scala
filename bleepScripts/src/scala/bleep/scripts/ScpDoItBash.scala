package bleep.scripts

import bleep.model.{CrossProjectName, ProjectName}
import bleep.{BleepScript, Commands, Started}

import java.nio.file.Path

object ScpDoItBash extends BleepScript("ScpJar") {
  override def run(started: Started, commands: Commands, args: List[String]): Unit = {
    val doItPath:Path = started.projectPaths(CrossProjectName(ProjectName(args.head),None)).dir.resolve("DoIt.bash")

    Scp.scpFile(doItPath,"DoIt.bash")
  }
}

