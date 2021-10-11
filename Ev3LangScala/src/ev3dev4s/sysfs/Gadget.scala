package ev3dev4s.sysfs

import java.io.IOException
import java.nio.file.AccessDeniedException

/**
 * Motors and Sensors that get plugged into Ports
 *
 * @author David Walend
 * @since v0.0.0
 */
abstract class Gadget[GFS <: GadgetFS,GPort <: Port](val port:GPort,initialGadgetFS: Option[GFS]) extends AutoCloseable:

  private var gadgetFS:Option[GFS] = initialGadgetFS

  def findGadgetFS():Option[GFS]

  def checkPort[A](action:GFS => A):A = synchronized {
    def handleUnpluggedGadget(t: Throwable): Nothing =
      gadgetFS.foreach(_.close)
      gadgetFS = None //set to None so that next time this will try to find something in the port
      throw UnpluggedException(port, t)

    try
      gadgetFS.orElse { //see if the motor is plugged back in
        gadgetFS = findGadgetFS()
        gadgetFS
      }.fold[A] { //if still not plugged in
        throw UnpluggedException(port)
      } { //otherwise do the action
        action(_)
      }
    catch //exceptions that indicate the gadget is unplugged
      case iox: IOException if iox.getMessage() == "No such device" => handleUnpluggedGadget(iox)
      case adx: AccessDeniedException => handleUnpluggedGadget(adx) //happens just after plug-back-in
  }

  override def close(): Unit = synchronized {
    gadgetFS.foreach(_.close)
    gadgetFS = None
  }

trait GadgetFS extends AutoCloseable

trait Port

case class UnpluggedException(port: Port,cause:Throwable) extends Exception(s"Gadget in $port unplugged",cause)

object UnpluggedException:
  def apply(port:Port):UnpluggedException = UnpluggedException(port,null)