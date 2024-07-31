package bleep.scripts

import bleep.yaml
import cats.syntax.either._
import io.circe._
import io.circe.generic.auto._
import org.apache.tools.ant.taskdefs.optional.ssh.{Scp => AntScp}
import org.apache.tools.ant.{Project => AntProject}

import java.nio.file.{Files, Path}

object Scp {

  def scpFile(fromLocalFile:Path, toRemoteFile:String, robotKey:String):Unit = {

    val robotSpec = robotSpecForKey(robotKey)

    val antScp = new AntScp()
    antScp.init()
    antScp.setProject(new AntProject())
    antScp.setPassword(robotSpec.password)
    antScp.setLocalFile(fromLocalFile.toString)
    antScp.setRemoteTofile(s"${robotSpec.username}@${robotSpec.hostname}:$toRemoteFile")
    antScp.setTrust(true)

    println(s"coping $fromLocalFile")
    antScp.execute()
    println(s"copied $fromLocalFile")

    println("\u0007")
  }

  def robotSpecForKey(robotKey:String): RobotYaml = {

    val yamlString:String = Files.readString(Path.of("ev3dev4s.yaml"))
    val ev3Dev4SYaml: Either[Error, Ev3Dev4sYaml] = yaml.decode[Ev3Dev4sYaml](yamlString)

    val maybeRobotSpec: Either[Error, RobotYaml] = ev3Dev4SYaml.map(_.robots(robotKey))

    maybeRobotSpec.getOrElse(throw new IllegalStateException("blart"))
  }

  //todo special handling of the password??
  case class RobotYaml(username:String, password:String, hostname:String)

  case class Ev3Dev4sYaml(robots:Map[String,RobotYaml])
}

