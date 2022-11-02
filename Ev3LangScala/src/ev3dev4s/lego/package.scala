package ev3dev4s

import ev3dev4s.sysfs.{Port, UnpluggedException}

import scala.util.control.NonFatal

/**
 * A subset of what Ev3 Classroom offers, in Scala instead of Scratch or LabView
 *
 * @author David Walend
 * @since v0.0.0
 */
package object lego {

  private[lego] def handleUnplugged[P <:Port,A](port: P, block: P => A, scan:() => Unit): A = {
    def scanAndTryAgain(t:Throwable):A = {
      scan()
      try {
        block(port)
      } catch {
        case NonFatal(_) => throw t
      }
    }

    try {
      block(port)
    } catch {
      case upx: UnpluggedException => scanAndTryAgain(upx)
      case nsx: NoSuchElementException => scanAndTryAgain(nsx)
    }
  }
}
