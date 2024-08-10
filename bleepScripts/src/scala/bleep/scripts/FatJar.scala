package bleep.scripts

import bleep.model.ProjectName
import bleep.{BleepScript, Commands, Started, model}
import bloop.config.{Config, PlatformFiles}
import org.apache.tools.ant.taskdefs.Manifest.Attribute
import org.apache.tools.ant.taskdefs.{Jar => AntJar, Manifest => AntManifest}
import org.apache.tools.ant.types.FileSet
import org.apache.tools.ant.types.selectors.FilenameSelector
import org.apache.tools.ant.{Project => AntProject}

import java.nio.file.Path

object FatJar extends BleepScript("FatJar") {
  override def run(started: Started, commands: Commands, args: List[String]): Unit = {

    val projectName = ProjectName(args.head)
    val crossProjectName = model.CrossProjectName(projectName,None)
    commands.compile(List(crossProjectName))
    //    println(${started.activeProjectsFromPath})  //todo why does this fail silently, bad exit code?

    val classesPath: Path = started.projectPaths(crossProjectName).classes
    val resourcesPath: Path = started.projectPaths(crossProjectName).targetDir.resolve("resources")

    val destFile = Jar.jarPath(started, projectName).toFile
    Jar.jarDirectory(started, projectName).toFile.mkdirs()
    destFile.delete()

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

    //find each dependency library and add it
    //todo is there a way to just include referenced dependencies?
    val classpath: Seq[PlatformFiles.Path] = started.bloopProject(crossProjectName).classpath
    val libraries = new FileSet()
    libraries.setProject(antProject)
    classpath.foreach{ cp =>
      libraries.setDir(cp.toFile.getParentFile)
      val libraryFileSelector = new FilenameSelector()
      libraryFileSelector.setName(cp.toFile.getName)
      libraries.add(libraryFileSelector)
      antJarTask.addZipGroupFileset(libraries)
    }

    val bloopProject: Config.Project = started.bloopFiles(crossProjectName).forceGet.project
    val mainClassName: Option[String] = bloopProject.platform.flatMap(_.mainClass)

    val antManifest = new AntManifest()
    mainClassName.foreach{m => antManifest.addConfiguredAttribute(new Attribute("Main-Class",m))}
    antJarTask.addConfiguredManifest(antManifest)

    antJarTask.execute()

    println(s"Wrote fat jar $destFile with main class $mainClassName")
  }
}

