package ev3dev4s.sensors

import ev3dev4s.sysfs.{ChannelRereader, ChannelRewriter, Gadget, GadgetFS}
import ev3dev4s.Log

import java.nio.file.Path
import scala.reflect.ClassTag

/**
 * @author David Walend
 * @since v0.0.0
 */
abstract class Sensor[SFS <: SensorFS](port:SensorPort,initialSensorFS: Option[SFS]) extends Gadget(port,initialSensorFS)

trait SensorFS extends GadgetFS

abstract class MultiModeSensor[SFS <: MultiModeSensorFS](port:SensorPort,initialSensorFS: Option[SFS])
  extends Sensor(port,initialSensorFS) {

  private var mode: Option[Mode] = None

  def currentMode: Option[Mode] = this.synchronized {
    mode
  }

  private[sensors] def writeMode: Mode => Unit = this.synchronized {
    checkPort(_.writeMode)
  }

  private[sensors] def setMaybeWriteMode[M <: Mode : ClassTag](toMode: M): M = this.synchronized {
    mode.collect { case m: M => m }.getOrElse {
      writeMode(toMode)
      mode = Option(toMode)
      toMode
    }
  }
}

trait Mode {
  def name: String
}

trait MultiModeSensorFS extends SensorFS {
  private[sensors] def writeMode(mode: Mode): Unit
}

object MultiModeSensorFS {

  case class Value0SensorFS(sensorDir: Path) extends MultiModeSensorFS {
    private lazy val modeWriter = ChannelRewriter(sensorDir.resolve("mode"))
    private lazy val value0Reader = ChannelRereader(sensorDir.resolve("value0"))

    private[sensors] def writeMode(mode: Mode): Unit = {
      val file = sensorDir.resolve("mode").toFile
      modeWriter.writeString(mode.name)
    }

    def readValue0Int(): Int = value0Reader.readAsciiInt()

    override def close(): Unit = {
      value0Reader.close()
      modeWriter.close()
    }
  }


  case class Value012SensorFS(sensorDir: Path) extends MultiModeSensorFS {
    private val modeWriter = ChannelRewriter(sensorDir.resolve("mode"))
    private val value0Reader = ChannelRereader(sensorDir.resolve("value0"))
    private val value1Reader = ChannelRereader(sensorDir.resolve("value1"))
    private val value2Reader = ChannelRereader(sensorDir.resolve("value2"))

    private[sensors] def writeMode(mode: Mode): Unit = modeWriter.writeString(mode.name)

    def readValue0Int(): Int = value0Reader.readAsciiInt()

    override def close(): Unit = {
      value2Reader.close()
      value1Reader.close()
      value0Reader.close()
      modeWriter.close()
    }
  }
}