package bleep.scripts

import io.circe.Error
import bleep.yaml
import cats.syntax.either._
import io.circe._
import io.circe.generic.auto._

import java.nio.file.{Files, Path}

/**
 * A class to read project-specific yaml into.
 *
 * @author David Walend
 * @since v0.0.0
 */
case class Ev3Dev4sYaml(robots:Map[String,RobotYaml])

object Ev3Dev4sYaml {

  lazy val loadYaml: Either[Error, Ev3Dev4sYaml] = {
    val yamlString:String = Files.readString(Path.of("ev3dev4s-bleep.yaml"))
    yaml.decode[Ev3Dev4sYaml](yamlString)
  }

  def robotSpecForKey: RobotYaml = {
    println(System.getProperties.get("robotName"))

    val robotKey: String = Option(System.getProperties.get("robotName")).getOrElse("firefly").toString

    val maybeRobotSpec: Either[Error, RobotYaml] = loadYaml.map(_.robots(robotKey))
    //todo return either
    maybeRobotSpec.getOrElse(throw new IllegalStateException("blart"))
  }
}

//todo special handling of the password??
case class RobotYaml(username:String, password:String, hostname:String)

