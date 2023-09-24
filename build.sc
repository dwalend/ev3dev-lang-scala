import mill._
import scalalib._

import mill.define.Command
import os.{CommandResult, Path}

import $ivy.`com.github.mwiede:jsch:0.2.9`
import $ivy.`org.apache.ant:ant-jsch:1.10.13`
import org.apache.tools.ant.taskdefs.optional.ssh.{Scp,SSHExec}
import org.apache.tools.ant.Project

object Shared {
  val scalacOptions: Seq[String] = Seq("-deprecation")
  val scalaVersion = "2.13.12"//"3.3.0"
  val javaVersion = "11.0.10"

  val ev3UserName = "robot"
  def ev3Password:String = System.getProperty("ev3Password") //todo do something clever to allow key files
  def ev3Hostname = s"${Option(System.getProperty("robotHostname")).getOrElse("ev3dev.local")}"

  def scpFile(fromLocalFile:Path, toRemoteFile:String):Unit = {
    val scp = new Scp()
    scp.init()
    scp.setProject(new Project())
    scp.setPassword(ev3Password)
    scp.setLocalFile(fromLocalFile.toString())
    scp.setRemoteTofile(s"$ev3UserName@$ev3Hostname:$toRemoteFile")
    scp.setTrust(true)
    scp.execute()
    println("\u0007")
  }

  /**
   * Copy a jar file to the ev3 via scp - and write the expected size in another file (in hopes of detecting that the
   * jar file is complete.)
   */
  def scpJar(artifactName:String,jarPath: Path): CommandResult = {
    scpFile(jarPath,s"$artifactName.jar")

    //todo progress or error messages?
    val result = CommandResult(Seq("scpJar",artifactName,jarPath.toString()),0, Seq.empty)

    result
  }

  /**
   * Copy an assembly file - a jar file that includes all needed dependencies - to the Ev3
   */
  def scpAssembly(artifactName:String,assemblyPath: Path): CommandResult = {

    scpFile(assemblyPath,s"$artifactName.jar")

    //todo progress or error messages?
    val result = CommandResult(Seq("scpJar",artifactName,assemblyPath.toString()),0, Seq.empty)

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

    val result = CommandResult(Seq("scpBash",fromBashFile.toString(),toBashFile),0, Seq.empty)

    result

  }

}

object Ev3LangScala extends ScalaModule {
  override def artifactName: T[String] = "Ev3LangScala"

  def scalaVersion = Shared.scalaVersion
  def javaVersion = Shared.javaVersion

  override def scalacOptions = Shared.scalacOptions

  def AppToRobot():Command[CommandResult] = T.command {
    Shared.scpAssembly(artifactName(),assembly().path)
  }

  /**
   * Update the millw script.
   */
  def millw(): Command[PathRef] = T.command {
    val target = mill.util.Util.download("https://raw.githubusercontent.com/lefou/millw/main/millw")
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

  def ToRobot():Command[CommandResult] = T.command {
    Shared.scpJar(artifactName(),jar().path)
  }

  def AppToRobot(): Command[CommandResult] = T.command {
    Shared.scpAssembly(artifactName(), assembly().path)
  }
}

object MasterPiece extends ScalaModule {
  override def artifactName: T[String] = "MasterPiece"
  override def mainClass: T[Option[String]] = Some("masterpiece.Menu")

  def scalaVersion = Shared.scalaVersion
  def javaVersion = Shared.javaVersion

  override def scalacOptions = Shared.scalacOptions.appended("-Yno-imports")

  override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Ev3LangScala)

  def ToRobot(): Command[CommandResult] = T.command {
    Shared.scpJar(artifactName(), jar().path)
  }

  def DoItToRobot() = T.command {
    val bashFile = millSourcePath / "DoIt.bash"
    Shared.scpBash(bashFile,"DoIt.bash")
  }

  def AppToRobot(): Command[CommandResult] = T.command {
    Shared.scpAssembly(artifactName(), assembly().path)
  }
}