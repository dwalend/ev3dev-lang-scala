package ev3dev4s.sensors

import ev3dev4s.sysfs.ChannelRewriter

import java.nio.file.Path
import scala.reflect.ClassTag

/**
 * @author David Walend
 * @since v0.0.0
 */
trait Sensor extends AutoCloseable:
  def port: SensorPort

abstract class MultiModeSensor(sensorDir:Path) extends Sensor:

  private val modeWriter = ChannelRewriter(sensorDir.resolve("mode"))

  private def writeMode(mode: Mode):Mode = this.synchronized {
    modeWriter.writeString(mode.name)
    mode
  }

  private var mode:Option[Mode] = None

  def getCurrentMode:Option[Mode] = this.synchronized {
    mode
  }

  private[sensors] def getOrElseChangeMode[M <: Mode: ClassTag](create:() => M):M = this.synchronized {
    mode.collect{case m:M => m}.getOrElse{
      val toMode = create()
      mode.foreach(_.close())
      writeMode(toMode)
      toMode.init()
      mode = Option(toMode)
      toMode
    }
  }

  trait Mode extends AutoCloseable:
    def name:String

    private[sensors] def init():Unit

  override def close(): Unit = this.synchronized {
    modeWriter.close()
    mode.foreach(_.close())
  }