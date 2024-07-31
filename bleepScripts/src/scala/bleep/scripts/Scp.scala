package bleep.scripts

import org.apache.tools.ant.taskdefs.optional.ssh.{Scp => AntScp}
import org.apache.tools.ant.{Project => AntProject}
import cats.syntax.either._

import java.nio.file.Path

object Scp {

  def scpFile(fromLocalFile:Path, toRemoteFile:String):Unit = {

    Ev3Dev4sYaml.robotSpecForKey.map { robotSpec =>
      val antScp = new AntScp()
      antScp.init()
      antScp.setProject(new AntProject())
      antScp.setPassword(robotSpec.password)
      antScp.setLocalFile(fromLocalFile.toString)
      antScp.setRemoteTofile(s"${robotSpec.username}@${robotSpec.hostname}:$toRemoteFile")
      antScp.setTrust(true)

      println(s"coping $fromLocalFile to ${robotSpec.hostname}:$toRemoteFile")
      antScp.execute()

      println(s"copied $fromLocalFile to ${robotSpec.hostname}:$toRemoteFile")
      println("\u0007")
    }.fold( throw _,_ => ())
  }
}

