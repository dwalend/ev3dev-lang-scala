package bleep.scripts

import io.circe.Error
import bleep.yaml
import io.circe.generic.auto._

import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters.EnumerationHasAsScala

/**
 * A class to read project-specific yaml into.
 *
 * @author David Walend
 * @since v0.0.0
 */
//todo remove target when you can get something in via -D
case class Ev3Dev4sYaml(robots:Map[String,RobotYaml])

object Ev3Dev4sYaml {

  lazy val loadYaml: Either[Error, Ev3Dev4sYaml] = {
    val yamlString:String = Files.readString(Path.of("ev3dev4s-bleep.yaml"))
    yaml.decode[Ev3Dev4sYaml](yamlString)
  }

  def robotSpecForKey: Either[Error, RobotYaml] = {

    val robotKey: String = Option(System.getProperties.get("robotName")).getOrElse("ev3dev").toString
    loadYaml.map(_.robots(robotKey))
  }
}

//todo special handling of the password??
case class RobotYaml(username:String, password:String, hostname:String)

