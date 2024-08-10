package bleep.scripts

import bleep.model.ProjectName
import bleep.packaging.{JarType, ManifestCreator, createJar}
import bleep.{BleepScript, Commands, Started, model}
import bloop.config.Config

import java.nio.file.{Files, Path}

object Jar extends BleepScript("Jar") {
  def run(started: Started, commands: Commands, args: List[String]): Unit = {

    val projectName = ProjectName(args.head)
    val crossProjectName = model.CrossProjectName(projectName,None)
    commands.compile(List(crossProjectName))

    val classesPath: Path = started.projectPaths(crossProjectName).classes
    val resourcesPath: Path = started.projectPaths(crossProjectName).targetDir.resolve("resources")

    val bloopProject: Config.Project = started.bloopFiles(crossProjectName).forceGet.project
    val mainClassName: Option[String] = bloopProject.platform.flatMap(_.mainClass)

    val jarBytes: Array[Byte] = createJar(
      jarType = JarType.Jar,
      manifestCreator = ManifestCreator.default,
      fromFolders = Seq(classesPath,resourcesPath),
      mainClass = mainClassName
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

