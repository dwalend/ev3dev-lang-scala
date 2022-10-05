import mill._
import scalalib._

import mill.define.Command
import os.{CommandResult, Path}

import java.nio.file.Files
import $ivy.`com.github.mwiede:jsch:0.1.61`
import $ivy.`org.apache.ant:ant-jsch:1.10.12`
import org.apache.tools.ant.taskdefs.optional.ssh.{Scp,SSHExec}
import org.apache.tools.ant.Project

object Shared {
  val scalacOptions: Seq[String] = Seq("-deprecation")
  val scalaVersion = "2.13.8"
  val javaVersion = "11.0.10"

  val ev3UserName = "robot"
  val ev3Password = System.getProperty("ev3Password") //todo do something clever to allow key files
  val ev3Hostname = "ev3dev.local"

  def scpFile(fromLocalFile:Path, toRemoteFile:String):Unit = {
    val scp = new Scp()
    scp.init()
    scp.setProject(new Project())
    scp.setPassword(ev3Password)
    scp.setLocalFile(fromLocalFile.toString())
    scp.setRemoteTofile(s"$ev3UserName@$ev3Hostname:$toRemoteFile")
    scp.setTrust(true)
    scp.execute()
  }

  /**
   * Copy a jar file to the ev3 via scp - and write the expected size in another file (in hopes of detecting that the
   * jar file is complete.)
   */
  def scpJar(artifactName:String,jarPath: Path): CommandResult = {
/*
    val fileSize: Long = Files.size(jarPath.toNIO)

    val ssh = new SSHExec()
    ssh.init()
    ssh.setPassword(ev3Password)
    ssh.setUsername(ev3UserName)
    ssh.setHost(ev3Hostname)
    ssh.setTrust(true)
    ssh.setCommand(s"echo $fileSize > expectedJarFileSize.txt")
    ssh.execute()
*/
    scpFile(jarPath,s"$artifactName.jar")

    //todo progress or error messages?

    val result = CommandResult(0, Seq.empty)

    result
  }

  /**
   * Copy an assembly file - a jar file that includes all needed dependencies - to the Ev3
   */
  def scpAssembly(artifactName:String,assemblyPath: Path): CommandResult = {

    scpFile(assemblyPath,s"$artifactName.jar")

    //todo progress or error messages?

    val result = CommandResult(0, Seq.empty)

    result
  }

  def scpBash(fromBashFile:Path,toBashFile:String):CommandResult = {
    scpFile(fromBashFile, toBashFile)

    val ssh = new SSHExec()
    ssh.init()
    ssh.setPassword(ev3Password)
    ssh.setUsername(ev3UserName)
    ssh.setHost(ev3Hostname)
    ssh.setTrust(true)
    ssh.setCommand(s"chmod +x $toBashFile")
    ssh.execute()

    //todo progress or error messages?

    val result = CommandResult(0, Seq.empty)

    result

  }

}

object Ev3LangScala extends ScalaModule {
  override def artifactName: T[String] = "Ev3LangScala"

  def scalaVersion = Shared.scalaVersion
  def javaVersion = Shared.javaVersion

  override def scalacOptions = Shared.scalacOptions

  def scpAssembly():Command[CommandResult] = T.command {
    Shared.scpAssembly(artifactName(),assembly().path)
  }

  /**
   * Update the millw script.
   */
  def millw(): Command[PathRef] = T.command {
    val target = mill.modules.Util.download("https://raw.githubusercontent.com/lefou/millw/main/millw")
    val millw = millSourcePath / "millw"
    os.copy.over(target.path, millw)
    os.perms.set(millw, os.perms(millw) + java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE)
    target
  }
}

object Ev3LangScalaExample extends ScalaModule {
  override def mainClass: T[Option[String]] = Some("ev3dev4s.sensors.examples.Ev3KeyPadExample")

  override def artifactName: T[String] = "Ev3LangScalaExample"

  def scalaVersion = Shared.scalaVersion
  def javaVersion = Shared.javaVersion

  override def scalacOptions = Shared.scalacOptions

  override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Ev3LangScala)

  def scpJar():Command[CommandResult] = T.command {
    Shared.scpJar(artifactName(),jar().path)
  }

  def scpAssembly(): Command[CommandResult] = T.command {
    Shared.scpAssembly(artifactName(), assembly().path)
  }
}

object SuperPowered extends ScalaModule {
  override def artifactName: T[String] = "SuperPowered"
  override def mainClass: T[Option[String]] = Some("superpowered.Menu")

  def scalaVersion = Shared.scalaVersion
  def javaVersion = Shared.javaVersion

  override def scalacOptions = Shared.scalacOptions.appended("-Yno-imports")

  override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Ev3LangScala)

  def scpJar(): Command[CommandResult] = T.command {
    Shared.scpJar(artifactName(), jar().path)
  }

  def scpBash() = T.command {
    val bashFile = millSourcePath / "SuperPowered.bash"
    Shared.scpBash(bashFile,"SuperPowered.bash")
  }

  def scpAssembly(): Command[CommandResult] = T.command {
    Shared.scpAssembly(artifactName(), assembly().path)
  }
}