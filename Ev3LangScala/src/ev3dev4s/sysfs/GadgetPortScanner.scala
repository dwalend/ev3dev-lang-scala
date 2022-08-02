package ev3dev4s.sysfs

import java.io.File
import java.nio.file.{Path,NoSuchFileException}
import scala.collection.immutable.ArraySeq

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
abstract class GadgetPortScanner[P <: Port](gadgetDir:File,ports:Array[P]) {

  val namesToPorts: Map[Char, P] = ports.map { p => p.name -> p }.toMap

  def scanGadgetDirs: Map[P, Path] = {
    ArraySeq.unsafeWrapArray(gadgetDir.listFiles()).map { (dir: File) =>
      //read the address to learn which port
      val addressPath = Path.of(dir.getAbsolutePath, "address")
      val port = namesToPorts(ChannelRereader.readString(addressPath).last)
      (port -> dir.toPath)
    }.toMap
  }

  def findGadgetDir(port: P, expectedDriverName: String): Option[Path] =
    try {
      scanGadgetDirs.get(port)
        .map { dir =>
          val foundDriverName = ChannelRereader.readString(dir.resolve("driver_name"))
          if (foundDriverName == expectedDriverName) dir
          else throw WrongGadgetInPortException(port, expectedDriverName, foundDriverName)
        }
    }
    catch {
      case nsfx: NoSuchFileException => None
    }
}

case class WrongGadgetInPortException(port:Port,expectedDriverName:String,foundDriverName:String)
  extends Exception(s"Expected $expectedDriverName in $port but found $foundDriverName")