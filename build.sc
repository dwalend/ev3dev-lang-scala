import mill._
import scalalib._
import coursier.maven.MavenRepository
import coursier.Repository
import mill.api.Loose
import mill.api.Result
import mill.define.{Command, Target}
import os.{CommandResult, Path}

import $ivy.`org.apache.ant:ant:1.10.12`, org.apache.tools.ant.taskdefs.Checksum

object Shared {
  val scalacOptions = Seq("-deprecation", "-source:3.0")
}

object Ev3LangScala extends ScalaModule {
  override def artifactName: T[String] = "Ev3LangScala"

  def scalaVersion = "3.0.2"//"2.13.5"//"3.0.0-RC2"//
  def javaVersion = "11.0.10"

  override def scalacOptions = Shared.scalacOptions

  def scpAssembly():Command[CommandResult] = T.command {

    val scpProc = os.proc(
      'scp,
      "-i", "~/.ssh/dwalend_ev3_id_rsa",
      assembly().path,
      s"robot@ev3dev.local:${artifactName()}.jar"
    )

    val result: CommandResult = scpProc.call()
    println(result.out)
    println(result.err)

    result
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

  def scalaVersion = "3.0.2"//"2.13.5"//"3.0.0-RC2"//
  def javaVersion = "11.0.10"

  override def scalacOptions = Shared.scalacOptions

  override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Ev3LangScala)

  def scpJar():Command[CommandResult] = T.command {

      //scp -i ~/.ssh/dwalend_ev3_id_rsa out/Replay/jar/dest/out.jar robot@ev3dev.local:Replay.jar
      val scpProc = os.proc(
        'scp,
        "-i", "~/.ssh/dwalend_ev3_id_rsa",
        jar().path,
        s"robot@ev3dev.local:${artifactName()}.jar"
      )

      val result: CommandResult = scpProc.call()
      println(result.out)
      println(result.err)

      result
  }

  def scpAssembly():Command[CommandResult] = T.command {

    val scpProc = os.proc(
      'scp,
      "-i", "~/.ssh/dwalend_ev3_id_rsa",
      assembly().path,
      s"robot@ev3dev.local:${artifactName()}.jar"
    )

    val result: CommandResult = scpProc.call()
    println(result.out)
    println(result.err)

    result
  }
  /*
  def checkSumJar: Target[Unit] = T {

    val checkSumTask = new Checksum()
    checkSumTask.setFile(jar().path.toIO)
    checkSumTask.setTodir() //todo some new file in out ?

    checkSumTask.execute()
    //scp -i ~/.ssh/dwalend_ev3_id_rsa out/Replay/jar/dest/out.jar robot@ev3dev.local:Replay.jar
  }
  */
}

object Ev3LangScalaExperimental extends ScalaModule {
  override def artifactName: T[String] = "Ev3LangScalaExperimental"

  def scalaVersion = "3.0.2"//"2.13.5"//"3.0.0-RC2"//
  def javaVersion = "11.0.10"

  override def scalacOptions = Shared.scalacOptions

  override def ivyDeps: Target[Loose.Agg[Dep]] = Agg(
    ivy"net.java.dev.jna:jna:4.5.2" //todo sna?
  )

  override def moduleDeps: Seq[JavaModule] = super.moduleDeps ++ Seq(Ev3LangScala)

  def scpJar():Command[CommandResult] = T.command {

    //scp -i ~/.ssh/dwalend_ev3_id_rsa out/Replay/jar/dest/out.jar robot@ev3dev.local:Replay.jar
    val scpProc = os.proc(
      'scp,
      "-i", "~/.ssh/dwalend_ev3_id_rsa",
      jar().path,
      s"robot@ev3dev.local:${artifactName()}.jar"
    )

    val result: CommandResult = scpProc.call()
    println(result.out)
    println(result.err)

    result
  }
}


