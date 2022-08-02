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
object MotorPortScanner extends GadgetPortScanner(new File("/sys/class/tacho-motor"),MotorPort.values){
  
  def scanMotors:Map[MotorPort,Motor] = {
    scanGadgetDirs.map{ portAndDir =>
      val driverName = ChannelRereader.readString(portAndDir._2.resolve("driver_name"))
      val motor = driverName match {
        case Ev3LargeMotor.driverName => Ev3LargeMotor(portAndDir._1, Option(MotorFS(portAndDir._2)))
        case Ev3MediumMotor.driverName => Ev3MediumMotor(portAndDir._1, Option(MotorFS(portAndDir._2)))
        case unknown => throw new IllegalArgumentException(s"Unknown driver $driverName")
      }
      portAndDir._1 -> motor
    }
  }

  /**
   * Always stop the motors
   */
  Runtime.getRuntime.addShutdownHook(new Thread({ () =>
    scanMotors.values.foreach(_.brake())
  },"stopMotorsAtShutdown"))
}

sealed case class MotorPort(val name:Char) extends Port

object MotorPort {
  val A = MotorPort('A')
  val B = MotorPort('B')
  val C = MotorPort('C')
  val D = MotorPort('D')

  val values = Array(A,B,C,D)
}

