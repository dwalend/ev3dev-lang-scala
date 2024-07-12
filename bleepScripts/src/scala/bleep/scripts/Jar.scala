package bleep.scripts

import bleep.model.ProjectName
import bleep.packaging.{JarType, ManifestCreator, createJar}
import bleep.{BleepScript, Commands, Started, model}

import java.io.{BufferedOutputStream, File, FileOutputStream}
import java.nio.file.{Files, Path}
import scala.util.Using

//todo start here. Pick up the ant Jar task instead - to get an executable jar file

object Jar extends BleepScript("Jar") {
  override def run(started: Started, commands: Commands, args: List[String]): Unit = {

    val projectName = ProjectName(args.head)
    commands.compile(List(model.CrossProjectName(projectName,None)))
    //todo handle variants instead of hardcoding "normal"
//    println(${started.activeProjectsFromPath})  //todo why does this fail silently, bad exit code?

    val classesPath: Path = started.buildPaths.buildsDir.resolve("normal").resolve(".bloop").resolve(projectName.value).resolve("classes")
    val resourcesPath: Path = started.buildPaths.buildsDir.resolve("normal").resolve(".bloop").resolve(projectName.value).resolve("resources") //todo is this the right path for resources?  Do I even need to do it?
    val jarBytes: Array[Byte] = createJar(
      jarType = JarType.Jar,
      manifestCreator = ManifestCreator.default,
      fromFolders = Seq(classesPath,resourcesPath),
      mainClass = Option("ev3dev4s.JarRunner") //todo how to get the main class from the bleep.yaml ?
    )

    jarDirectory(started, projectName).toFile.mkdirs()

    jarPath(started, projectName).toFile.delete()
    Files.write(jarPath(started,projectName),jarBytes)

    println(s"${jarBytes.length} bytes written to ${jarPath(started,projectName)}")

  }

  def jarPath(started: Started,projectName: ProjectName):Path = {
    jarDirectory(started, projectName).resolve(s"${projectName.value}.jar")
  }

  def jarDirectory(started: Started,projectName: ProjectName):Path = {
    started.buildPaths.buildsDir.resolve("normal").resolve(".bloop").resolve(projectName.value).resolve("jars")
  }
}

