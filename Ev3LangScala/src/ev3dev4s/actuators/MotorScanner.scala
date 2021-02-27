package ev3dev4s.actuators

import ev3dev4s.sysfs.ChannelRereader

import java.nio.file.{Files, Path}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object MotorScanner {

  def scanMotorsDir:Seq[Motor] = {
    import scala.jdk.StreamConverters._

    val motorsDir: Path = Path.of("/sys/class/tacho-motor")

    Files.list(motorsDir).toScala(Seq).map{ motorDir:Path =>
      //read the address to learn which port
      val port = MotorPort.namesToPorts(ChannelRereader.readString(motorDir.resolve("address")).last)
      //read the driver to figure out large vs medium
      val driverName = ChannelRereader.readString(motorDir.resolve("driver_name"))
      driverName match {
        case Ev3LargeMotor.driverName => Ev3LargeMotor(port,motorDir)
        case _ => throw new IllegalArgumentException(s"Unknown driver $driverName")
      }
    }
  }
}

trait Motor

sealed case class Ev3LargeMotor(port:MotorPort,motorDir:Path) extends Motor

object Ev3LargeMotor {
  val driverName = "lego-ev3-l-motor"
}

//todo use a Scala3 enum
object MotorPort {
  val A: MotorPort = MotorPort('A')
  val B: MotorPort = MotorPort('B')
  val C: MotorPort = MotorPort('C')
  val D: MotorPort = MotorPort('D')

  val namesToPorts: Map[Char, MotorPort] = Set(A,B,C,D).map{ port => port.name -> port}.toMap
}

sealed case class MotorPort(name:Char)

