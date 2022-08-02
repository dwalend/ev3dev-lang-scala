package ev3dev4s.sysfs

import java.io.IOException
import java.nio.file.{AccessDeniedException,NoSuchFileException}

import ev3dev4s.Log

/**
 * Motors and Sensors that get plugged into Ports
 *
 * @author David Walend
 * @since v0.0.0
 */
abstract class Gadget[GFS <: GadgetFS,GPort <: Port](val port:GPort,initialGadgetFS: Option[GFS]) extends AutoCloseable{

  private var gadgetFS:Option[GFS] = initialGadgetFS

  def findGadgetFS():Option[GFS]

  protected def unsetGadgetFS():Unit = synchronized{
    gadgetFS = None
  }

  def checkPort[A](action:GFS => A):A = synchronized {
    def handleUnpluggedGadget(t: Throwable): Nothing = {
      try {
        gadgetFS.foreach(_.close)
      }
      catch {
        case iox: IOException => //don't care - just recover from the unplug
      }
      gadgetFS = None //set to None so that next time this will try to find something in the port
      throw UnpluggedException(port, t)
    }

    try {
      gadgetFS.orElse { //see if the gadget is plugged back in
        gadgetFS = findGadgetFS()
        gadgetFS
      }.fold[A] { //if still not plugged in
        throw UnpluggedException(port)
      } { //otherwise do the action
        action(_)
      }
    }
    catch {
      case GadgetUnplugged(x) => handleUnpluggedGadget(x)
      case x: Throwable =>
        Log.log(s"caught $x with '${x.getMessage()}'",x)
        throw x
    }
  }

  override def close(): Unit = synchronized {
    gadgetFS.foreach(_.close)
    gadgetFS = None
  }
}

trait GadgetFS extends AutoCloseable

trait Port {
  def name:Char
}

case class UnpluggedException(port: Port,cause:Throwable) extends Exception(s"$port gadget unplugged",cause)

object UnpluggedException {
  def apply(port: Port): UnpluggedException = UnpluggedException(port, null)

  def safeString(readSensorToString: (() => String)): String =
    try {
      readSensorToString()
    }
    catch {
      case _: UnpluggedException => "UnP"
    }
}

object GadgetUnplugged{
  /**
   * Returns true if the provided `Throwable` is to be considered non-fatal, or false if it is to be considered fatal
   */
  def apply(t: Throwable): Boolean = t match {
    // VirtualMachineError includes OutOfMemoryError and other fatal errors
    case _: NoSuchFileException | _: AccessDeniedException  => true
    case iox: IOException if iox.getMessage() == "No such device" => true
    case iox: IOException if iox.getMessage() == "No such device or address" => true
    case iox: IOException if iox.getMessage() == "Device or resource busy" => true
    case _ => false
  }

  /**
   * Returns Some(t) if NonFatal(t) == true, otherwise None
   */
  def unapply(t: Throwable): Option[Throwable] = if (apply(t)) Some(t)
                                                  else None
}