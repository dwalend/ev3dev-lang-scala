package ev3dev4s.actuators

import ev3dev4s.sysfs.ChannelRereader

import java.io.File
import java.nio.file.Path
import scala.collection.immutable.ArraySeq

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object MotorPortScanner {

  def scanMotorsDir:Map[MotorPort,Motor] = {
    val motorMap = {
      //noinspection SpellCheckingInspection
      val motorsDir: File = new File("/sys/class/tacho-motor")
      ArraySeq.unsafeWrapArray(motorsDir.listFiles()).map{ (motorDir: File) =>
        //read the address to learn which port
        val addressPath = Path.of(motorDir.getAbsolutePath,"address")
        val port = MotorPort.namesToPorts(ChannelRereader.readString(addressPath).last)

        //read the driver to figure out large vs medium
        val driverPath = Path.of(motorDir.getAbsolutePath,"driver_name")
        val driverName = ChannelRereader.readString(driverPath)

        driverName match {
          case Ev3LargeMotor.driverName => Ev3LargeMotor(port,Path.of(motorDir.getAbsolutePath))
          case _ => throw new IllegalArgumentException(s"Unknown driver $driverName")
        }
      }
    }.map{motor => motor.port -> motor}.toMap

    val stopMotors:Runnable = () => {
      println(s"stopMotors at shutdown started")
      motorMap.values.foreach(_.brake())
      motorMap.values.foreach(_.close())
      println(s"stopMotors at shutdown complete")
    }
    Runtime.getRuntime.addShutdownHook(new Thread(stopMotors,"stopMotorsAtShutdown"))

    motorMap
  }
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


