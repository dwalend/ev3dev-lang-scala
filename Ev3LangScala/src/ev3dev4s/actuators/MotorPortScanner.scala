package ev3dev4s.actuators

import ev3dev4s.sysfs.{ChannelRereader, GadgetPortScanner, Port}

import java.io.File
import java.nio.file.Path
import scala.collection.immutable.ArraySeq


/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object MotorPortScanner extends GadgetPortScanner(new File("/sys/class/tacho-motor"),MotorPort.values):
  
  def scanMotors:Map[MotorPort,Motor] =
    scanGadgetDirs.map{(port,motorDir) =>
      val driverName = ChannelRereader.readString(motorDir.resolve("driver_name"))
      val motor = driverName match
        case Ev3LargeMotor.driverName => Ev3LargeMotor(port,Option(MotorFS(motorDir)))
        case Ev3MediumMotor.driverName => Ev3MediumMotor(port,Option(MotorFS(motorDir)))
        case unknown => throw new IllegalArgumentException(s"Unknown driver $driverName")
      port -> motor
    }

  /**
   * Always stop the motors
   */
  Runtime.getRuntime.addShutdownHook(new Thread({ () =>
    scanMotors.values.foreach(_.brake())
  },"stopMotorsAtShutdown"))

enum MotorPort(val name:Char) extends Port:
  case A extends MotorPort('A')
  case B extends MotorPort('B')
  case C extends MotorPort('C')
  case D extends MotorPort('D')

