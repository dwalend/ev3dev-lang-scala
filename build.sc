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
  val scalacOptions = Seq("-deprecation")
  val scalaVersion = "2.13.8"
  val javaVersion = "11.0.10"

  def scpJar(artifactName:String,jarPath: Path): CommandResult = {

    val fileSize: Long = Files.size(jarPath.toNIO)

    val ssh = new SSHExec()
    ssh.init()
    ssh.setKeyfile("~/.ssh/dwalend_ev3_id_rsa")
    ssh.setUsername("robot")
    ssh.setHost("ev3dev.local")
    ssh.setTrust(true)
    ssh.setCommand(s"echo $fileSize > expectedJarFileSize.txt")
    ssh.execute()

    val scp = new Scp()
    scp.init()
    scp.setProject(new Project())
    scp.setKeyfile("~/.ssh/dwalend_ev3_id_rsa")
    scp.setLocalFile(jarPath.toString())
    scp.setRemoteTofile(s"robot@ev3dev.local:$artifactName.jar")
    scp.setTrust(true)
    scp.execute()

    //todo progress or error messages?

    val result = CommandResult(0, Seq.empty)

    result
  }

  def scpAssembly(artifactName:String,assemblyPath: Path): CommandResult = {

    val scp = new Scp()
    scp.init()
    scp.setProject(new Project())
    scp.setKeyfile("~/.ssh/dwalend_ev3_id_rsa")
    scp.setLocalFile(assemblyPath.toString())
    scp.setRemoteTofile(s"robot@ev3dev.local:$artifactName.jar")
    scp.setTrust(true)
    scp.execute()

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

object CargoConnect extends ScalaModule {
  override def artifactName: T[String] = "CargoConnect"
  override def mainClass: T[Option[String]] = Some("net.walend.cargoconnect.CargoConnect")

  def scalaVersion = Shared.scalaVersion
  def javaVersion = Shared.javaVersion

  override def scalacOptions = Shared.scalacOptions

  override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Ev3LangScala)

  def scpJar(): Command[CommandResult] = T.command {
    Shared.scpJar(artifactName(), jar().path)
  }

  def scpAssembly(): Command[CommandResult] = T.command {
    Shared.scpAssembly(artifactName(), assembly().path)
  }
}