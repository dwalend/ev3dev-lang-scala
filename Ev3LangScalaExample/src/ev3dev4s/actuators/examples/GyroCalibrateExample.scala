package ev3dev4s.actuators.examples
//todo move to sensors

import ev3dev4s.{Ev3System, Log}
import ev3dev4s.sysfs.{ChannelRereader, ChannelRewriter, UnpluggedException, GadgetUnplugged}
import ev3dev4s.sensors.{Ev3Gyroscope, Ev3KeyPad, SensorPort, SensorPortScanner}
import ev3dev4s.os.Time

import java.io.{File,IOException}
import java.nio.file.{Path,NoSuchFileException,AccessDeniedException}
import scala.collection.immutable.ArraySeq

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object GyroCalibrateExample extends Runnable:
  def main(args: Array[String]): Unit =
    run()

  override def run(): Unit =
    val gyro:Ev3Gyroscope = SensorPortScanner.scanSensors.values.collectFirst{case x:Ev3Gyroscope => x}.get

    gyro.despin()
    
    Ev3KeyPad.blockUntilAnyKey()

