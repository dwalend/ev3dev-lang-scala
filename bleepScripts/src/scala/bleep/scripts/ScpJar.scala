package bleep.scripts

import bleep.model.ProjectName
import bleep.{BleepScript, Commands, Started}

import java.nio.file.Path

import org.apache.tools.ant.taskdefs.optional.ssh.{Scp,SSHExec}
import org.apache.tools.ant.{Project => AntProject}

object ScpJar extends BleepScript("ScpJar") {
  override def run(started: Started, commands: Commands, args: List[String]): Unit = {
    //todo make the Jar file with something like commands.compile(List(model.CrossProjectName(ProjectName(args.head),None)))

    val projectName = ProjectName(args.head)
    val jarPath = Jar.jarPath(started,projectName)

    println(s"coping $jarPath")
    scpJar(jarPath,projectName)
    println(s"copied $jarPath")
  }

  /**
   * Copy a jar file to the ev3 via scp - and write the expected size in another file (in hopes of detecting that the
   * jar file is complete.)
   */
  def scpJar(jarPath: Path,projectName: ProjectName) = {
    scpFile(jarPath,s"${projectName.value}.jar")
  }

  def scpFile(fromLocalFile:Path, toRemoteFile:String):Unit = {
    val ev3UserName = "robot"
    val ev3Password = "nope"
    val ev3Hostname = "sally.local"

    val scp = new Scp()
    scp.init()
    scp.setProject(new AntProject())
    scp.setPassword(ev3Password)
    scp.setLocalFile(fromLocalFile.toString())
    scp.setRemoteTofile(s"$ev3UserName@$ev3Hostname:$toRemoteFile")
    scp.setTrust(true)
    scp.execute()
    println("\u0007")
  }
}

