package bleep.scripts

import org.apache.tools.ant.taskdefs.optional.ssh.{Scp => AntScp}
import org.apache.tools.ant.{Project => AntProject}

import java.nio.file.Path

object Scp {

  def scpFile(fromLocalFile:Path, toRemoteFile:String):Unit = {

    val ev3Username: String = Option(System.getProperties.get("ev3Username")).getOrElse("robot").toString
    val ev3Password: String = Option(System.getProperties.get("ev3Password")).getOrElse("maker").toString
    val ev3Hostname: String = Option(System.getProperties.get("ev3Hostname")).getOrElse("ev3dev").toString

    val remoteTarget = s"$ev3Username@${ev3Hostname}:$toRemoteFile"

    val antScp = new AntScp()
    antScp.init()
    antScp.setProject(new AntProject())
    antScp.setPassword(ev3Password)
    antScp.setLocalFile(fromLocalFile.toString)
    antScp.setRemoteTofile(remoteTarget)
    antScp.setTrust(true)

    println(s"coping $fromLocalFile to $remoteTarget")
    antScp.execute()

    println(s"copied $fromLocalFile to $remoteTarget")
    println("\u0007")
  }
}

