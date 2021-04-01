package ev3dev4s.sensors

import ev3dev4s.sysfs.ChannelRereader

import java.nio.file.Path

/**
 * @author David Walend
 * @since v0.0.0
 */
case class Ev3ColorSensor(port:SensorPort,sensorDir:Path) extends MultiModeSensor(sensorDir) {

  def reflectMode():ReflectMode = {
    getOrElseChangeMode(ReflectMode.apply)
  }

  case class ReflectMode() extends Mode {
    val name = "COL-REFLECT"

    private lazy val reflectReader = ChannelRereader(sensorDir.resolve("value0"))
    override private[sensors] def init():Unit = reflectReader.path

    /**
     * Reflected light
     * @return Reflected light intensity (0 to 100)
     */
    def readReflect():Int = this.synchronized{
      reflectReader.readAsciiInt()
    }

    override def close(): Unit = this.synchronized{
      reflectReader.close()
    }
  }

  /* todo
COL-REFLECT	Reflected light - sets LED color to red	pct (percent)	0	1	value0: Reflected light intensity (0 to 100)
COL-AMBIENT	Ambient light - sets LED color to blue (dimly lit)	pct (percent)	0	1	value0: Ambient light intensity (0 to 100)
COL-COLOR	Color - sets LED color to white (all LEDs rapidly cycling)	col (color)	0	1	value0: Detected color (0 to 7) [26]
REF-RAW	Raw Reflected - sets LED color to red	none	0	2
value0: ??? (0 to 1020???)

value1: ??? (0 to 1020???)

RGB-RAW	Raw Color Components - sets LED color to white (all LEDs rapidly cycling)	none	0	3
value0: Red (0 to 1020???)

value1: Green (0 to 1020???)

value2: Blue (0 to 1020???)

COL-CAL [27]	Calibration ??? - sets LED color to red, flashing every 4 seconds, then goes continuous	none	0	4
value0: ???

value1: ???

value2: ???

value3: ???
   */
}

object Ev3ColorSensor {
  val driverName = "lego-ev3-color"
}


