package bleep.scripts

import bleep.model.ProjectName
import bleep.packaging.{JarType, ManifestCreator, createJar}
import bleep.{BleepScript, Commands, Started, model}
import bloop.config.PlatformFiles
import org.apache.tools.ant.taskdefs.Manifest.Attribute
import org.apache.tools.ant.taskdefs.{Jar => AntJar, Manifest => AntManifest}
import org.apache.tools.ant.{Project => AntProject}
import org.apache.tools.ant.types.{FileSet, ZipFileSet}
import org.apache.tools.ant.types.selectors.{FileSelector, FilenameSelector}

import java.io.{BufferedOutputStream, File, FileOutputStream}
import java.nio.file.{Files, Path}
import scala.util.Using

object Jar extends BleepScript("Jar") {
  override def run(started: Started, commands: Commands, args: List[String]): Unit = {

    val projectName = ProjectName(args.head)
    val crossProjectName = model.CrossProjectName(projectName,None)
    commands.compile(List(crossProjectName))
    //todo handle variants instead of hardcoding "normal"
    //    println(${started.activeProjectsFromPath})  //todo why does this fail silently, bad exit code?

    val classesPath: Path = started.buildPaths.buildsDir.resolve("normal").resolve(".bloop").resolve(projectName.value).resolve("classes")
    val resourcesPath: Path = started.buildPaths.buildsDir.resolve("normal").resolve(".bloop").resolve(projectName.value).resolve("resources") //todo is this the right path for resources?  Do I even need to do it?

    val destFile = jarPath(started, projectName).toFile
    jarDirectory(started, projectName).toFile.mkdirs()
    jarPath(started, projectName).toFile.delete()

    val antProject = new AntProject()

    val antJarTask = new AntJar()
    antJarTask.init()
    antJarTask.setProject(antProject)

    antJarTask.setDestFile(destFile)

    val classFiles = new FileSet()
    classFiles.setDir(classesPath.toFile)
    antJarTask.addFileset(classFiles)

    if(resourcesPath.toFile.exists()) {
      val resourceFiles = new FileSet()
      resourceFiles.setDir(resourcesPath.toFile)
      antJarTask.addFileset(resourceFiles)
    }

    //todo find the dependency libraries and add them
    val classpath: Seq[PlatformFiles.Path] = started.bloopProject(crossProjectName).classpath
    val libraries = new FileSet()
    libraries.setProject(antProject)
    libraries.setDir(classpath.head.toFile.getParentFile)
    val libraryFileSelector = new FilenameSelector()
    libraryFileSelector.setName(classpath.head.toFile.getName)

    libraries.add(libraryFileSelector)
    antJarTask.addZipGroupFileset(libraries)

    val mainClassName = "ev3dev4s.JarRunner$" //todo get this from bleep yaml
    val antManifest = new AntManifest()
    antManifest.addConfiguredAttribute(new Attribute("Main-Class",mainClassName))
    antJarTask.addConfiguredManifest(antManifest)

    antJarTask.execute()
  }

  def runJustBleep(started: Started, commands: Commands, args: List[String]): Unit = {

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

