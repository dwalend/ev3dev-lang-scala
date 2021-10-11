package ev3dev4s.actuators

import ev3dev4s.sysfs.ChannelRereader

import java.io.File
import java.nio.file.Path
import scala.collection.immutable.ArraySeq

import ev3dev4s.sysfs.Port


/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object MotorPortScanner:

  private def scanMotorDirs:Map[MotorPort,Path] =
    //noinspection SpellCheckingInspection
    val motorsDir: File = new File("/sys/class/tacho-motor")
    ArraySeq.unsafeWrapArray(motorsDir.listFiles()).map { (motorDir: File) =>
      //read the address to learn which port
      val addressPath = Path.of(motorDir.getAbsolutePath,"address")
      val port = namesToPorts(ChannelRereader.readString(addressPath).last)
      (port -> motorDir.toPath)
    }.toMap

  def scanMotors:Map[MotorPort,Motor] =
    scanMotorDirs.map{(port,motorDir) =>
      val driverName = ChannelRereader.readString(motorDir.resolve("driver_name"))
      val motor = driverName match
        case Ev3LargeMotor.driverName => Ev3LargeMotor(port,Option(MotorFS(motorDir)))
        case Ev3MediumMotor.driverName => Ev3MediumMotor(port,Option(MotorFS(motorDir)))
        case unknown => throw new IllegalArgumentException(s"Unknown driver $driverName")
      port -> motor
    }

  private[actuators] def findMotorDevice(port: MotorPort):Option[MotorFS] =
    scanMotorDirs.get(port).map(MotorFS(_))

  private val namesToPorts: Map[Char, MotorPort] = MotorPort.values.map{ port => port.name -> port}.toMap

  Runtime.getRuntime.addShutdownHook(new Thread({ () =>
    scanMotors.values.foreach(_.brake())
  },"stopMotorsAtShutdown"))

enum MotorPort(val name:Char) extends Port:
  case A extends MotorPort('A')
  case B extends MotorPort('B')
  case C extends MotorPort('C')
  case D extends MotorPort('D')

