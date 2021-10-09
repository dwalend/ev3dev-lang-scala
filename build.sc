import mill._
import scalalib._
import coursier.maven.MavenRepository
import coursier.Repository
import mill.api.Loose
import mill.api.Result
import mill.define.{Command, Target}
import os.{CommandResult, Path}

object Shared {
  val scalacOptions = Seq("-deprecation", "-source:3.0", "-indent", "-rewrite")
}

object Ev3LangScala extends ScalaModule {
  override def artifactName: T[String] = "Ev3LangScala"

  def scalaVersion = "3.0.2"//"2.13.5"//"3.0.0-RC2"//
  def javaVersion = "11.0.10"

  override def scalacOptions = Shared.scalacOptions

  //not needed after LCDs are in this library
  /*
  override def repositories: Seq[Repository] = super.repositories ++ Seq(
    MavenRepository("https://jitpack.io")
  )
  */
  //not needed after LCDs are in this library
  override def ivyDeps: Target[Loose.Agg[Dep]] = Agg(
    ivy"net.java.dev.jna:jna:4.5.2"
//    ivy"com.github.ev3dev-lang-java:ev3dev-lang-java:2.6.2-SNAPSHOT",    //2.7.0-SNAPSHOT ??
//    ivy"com.github.ev3dev-lang-java:lejos-commons:0.7.3",   //won't need these two dependencies when you can pull the main library in from ivy
//    ivy"net.java.dev.jna:jna:4.5.2",
//    ivy"org.slf4j:slf4j-simple:1.7.25"
  )

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

}
