package ev3dev4s.sensors

import java.nio.file.Path

/**
 * @author David Walend
 * @since v0.0.0
 */
case class Ev3InfraredSensor(override val port: SensorPort, initialSensorDir: Option[Path])
  extends MultiModeSensor(port, initialSensorDir.map(MultiModeSensorFS.Value012SensorFS)) { //todo all eight channels

  override def findGadgetFS(): Option[MultiModeSensorFS.Value012SensorFS] =
    SensorPortScanner.findGadgetDir(port, Ev3InfraredSensor.driverName)
      .map(MultiModeSensorFS.Value012SensorFS)

  private lazy val onlyRemoteMode: RemoteMode = RemoteMode()

  def remoteMode(): RemoteMode = setMaybeWriteMode(onlyRemoteMode)

  sealed case class RemoteMode() extends Mode {

    import ev3dev4s.sensors.Ev3InfraredSensor.RemoteButton

    val name = "IR-REMOTE"

    /**
     * @return the buttons pushed on the remote - not more than two.
     *         See https://docs.ev3dev.org/projects/lego-linux-drivers/en/ev3dev-stretch/sensor_data.html#lego-ev3-ir-mode2-value0
     */
    def readRemote1(): Set[RemoteButton] = this.synchronized {
      intToButtons(checkPort(_.readValue0Int()))
    }

    import RemoteButton._

    lazy val intToButtons: Map[Int, Set[RemoteButton]] = Map(
      0 -> Set(),
      1 -> Set(LeftUp),
      2 -> Set(LeftDown),
      3 -> Set(RightUp),
      4 -> Set(RightDown),
      5 -> Set(LeftUp, RightUp),
      6 -> Set(LeftUp, RightDown),
      7 -> Set(LeftDown, RightUp),
      8 -> Set(LeftDown, RightDown),
      9 -> Set(Beacon),
      10 -> Set(LeftUp, LeftDown),
      11 -> Set(RightUp, RightDown),
    )
  }

}
/* todo
All the other modes
 */

object Ev3InfraredSensor {
  val driverName = "lego-ev3-ir"

  /**
   * @see https://docs.ev3dev.org/projects/lego-linux-drivers/en/ev3dev-stretch/sensor_data.html#lego-ev3-color-mode2-value0
   */
  sealed case class RemoteButton(name: String)

  object RemoteButton {
    val LeftUp: RemoteButton = RemoteButton("LeftUp")
    val LeftDown: RemoteButton = RemoteButton("LeftDown")
    val RightUp: RemoteButton = RemoteButton("RightUp")
    val RightDown: RemoteButton = RemoteButton("RightDown")
    val Beacon: RemoteButton = RemoteButton("Beacon")
  }
}