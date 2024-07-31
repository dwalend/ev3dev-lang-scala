package bleep.scripts

import io.circe.Error
import bleep.yaml
import io.circe.generic.auto._

import java.nio.file.{Files, Path}

/**
 * A class to read project-specific yaml into.
 *
 * @author David Walend
 * @since v0.0.0
 */
//todo remove target when you can get something in via -D
case class Ev3Dev4sYaml(target:String,robots:Map[String,RobotYaml])

object Ev3Dev4sYaml {

  lazy val loadYaml: Either[Error, Ev3Dev4sYaml] = {
    val yamlString:String = Files.readString(Path.of("ev3dev4s-bleep.yaml"))
    yaml.decode[Ev3Dev4sYaml](yamlString)
  }

  def robotSpecForKey: Either[Error, RobotYaml] = {
    println(System.getProperties.get("robotName"))

    //todo replace with -D
//    val robotKey: String = Option(System.getProperties.get("robotName")).getOrElse("firefly").toString
    val robotKey: String = loadYaml.map(_.target).getOrElse(throw new IllegalStateException("No target in .yaml"))

    loadYaml.map(_.robots(robotKey))
  }
}

//todo special handling of the password??
case class RobotYaml(username:String, password:String, hostname:String)

