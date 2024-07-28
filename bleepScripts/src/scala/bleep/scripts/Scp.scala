package bleep.scripts

import bleep.model.ProjectName
import bleep.{BleepScript, Commands, Started}
import org.apache.tools.ant.taskdefs.optional.ssh.{Scp => AntScp}
import org.apache.tools.ant.{Project => AntProject}

import java.nio.file.Path

object Scp {

  def scpFile(fromLocalFile:Path, toRemoteFile:String):Unit = {
    val ev3UserName = "robot"    //todo from yaml - ask how
    val ev3Password = "nope"     //todo from command line or bleep secure way - ask how
    val ev3Hostname = "firefly.local" //todo from yaml -ask how

    val antScp = new AntScp()
    antScp.init()
    antScp.setProject(new AntProject())
    antScp.setPassword(ev3Password)
    antScp.setLocalFile(fromLocalFile.toString)
    antScp.setRemoteTofile(s"$ev3UserName@$ev3Hostname:$toRemoteFile")
    antScp.setTrust(true)
    antScp.execute()
    println("\u0007")
  }
}

