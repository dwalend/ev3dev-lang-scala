package bleep.scripts

import bleep.model.ProjectName
import bleep.packaging.{JarType, ManifestCreator, createJar}
import bleep.{BleepScript, Commands, Started, model}

import java.io.{BufferedOutputStream, File, FileOutputStream}
import java.nio.file.{Files, Path}
import scala.util.Using

object Jar extends BleepScript("Jar") {
  override def run(started: Started, commands: Commands, args: List[String]): Unit = {

    commands.compile(List(model.CrossProjectName(ProjectName(args.head),None)))
    //todo handle variants instead of hardcoding "normal"
//    println(${started.activeProjectsFromPath})  //todo why does this fail silently, bad exit code?

    val classesPath: Path = started.buildPaths.buildsDir.resolve("normal").resolve(".bloop").resolve(args.head).resolve("classes")
    val resourcesPath: Path = started.buildPaths.buildsDir.resolve("normal").resolve(".bloop").resolve(args.head).resolve("resources") //todo is this the right path for resources?  Do I even need to do it?
    val jarBytes: Array[Byte] = createJar(
      jarType = JarType.Jar,
      manifestCreator = ManifestCreator.default,
      fromFolders = Seq(classesPath,resourcesPath),
      mainClass = Option("ev3dev4s.JarRunner") //todo how to get the main class from the bleep.yaml ?
    )

    val jarDirectory = started.buildPaths.buildsDir.resolve("normal").resolve(".bloop").resolve(args.head).resolve("jars")
    jarDirectory.toFile.mkdirs()
    val jarPath = jarDirectory.resolve(s"${args.head}.jar")
    jarPath.toFile.delete()
    Files.write(jarPath,jarBytes)

    println(s"${jarBytes.length} bytes written to $jarPath")

  }
}

