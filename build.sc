import coursier.Repository
import mill._
import scalalib._
import coursier.maven.MavenRepository
import mill.api.Loose
import mill.api.Result
import mill.define.{Command, Target}
import os.{CommandResult, Path}

object Ev3LangScala extends ScalaModule {
  override def artifactName: T[String] = "Ev3LangScala"

  def scalaVersion = "2.13.5"
  def javaVersion = "11.0.10"

  override def scalacOptions = Seq("-deprecation")

}

object Ev3LangScalaExample extends ScalaModule {
  override def mainClass: T[Option[String]] = Some("ev3dev4s.sensors.examples.Ev3KeyPadExample")

  override def artifactName: T[String] = "Ev3LangScalaExample"

  def scalaVersion = "2.13.5"
  def javaVersion = "11.0.10"

  override def scalacOptions = Seq("-deprecation")

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

}
