package ev3dev4s.sensors

import ev3dev4s.measure.Conversions._
import ev3dev4s.measure.Percent

import java.nio.file.Path

/**
 * @author David Walend
 * @since v0.0.0
 */
case class Ev3ColorSensor(override val port:SensorPort,initialSensorDir:Option[Path])
  extends MultiModeSensor(port,initialSensorDir.map(MultiModeSensorFS.Value012SensorFS)) {

  override def findGadgetFS(): Option[MultiModeSensorFS.Value012SensorFS] =
    SensorPortScanner.findGadgetDir(port, Ev3ColorSensor.driverName)
      .map(MultiModeSensorFS.Value012SensorFS)


  private lazy val onlyReflectMode: ReflectMode = ReflectMode()

  def reflectMode(): ReflectMode =
    setMaybeWriteMode(onlyReflectMode)

  sealed case class ReflectMode() extends Mode {
    val name = "COL-REFLECT"

    /**
     * Reflected light
     *
     * @return Reflected light intensity (0 to 100)
     */
    def readReflect(): Percent = this.synchronized {
      checkPort(_.readValue0Int().percent)
    }
  }

  private lazy val onlyAmbientMode: AmbientMode = AmbientMode()

  def ambientMode(): AmbientMode =
    setMaybeWriteMode(onlyAmbientMode)

  sealed case class AmbientMode() extends Mode {
    val name = "COL-AMBIENT"

    /**
     * Ambient light
     *
     * @return Ambient light intensity (0 to 100)
     */
    def readAmbient(): Percent = this.synchronized {
      checkPort(_.readValue0Int().percent)
    }
  }

  private lazy val onlyColorMode = ColorMode()

  def colorMode(): ColorMode =
    setMaybeWriteMode(onlyColorMode)

  case class ColorMode() extends Mode {
    val name = "COL-COLOR"

    /**
     * Ambient light
     *
     * @return color detected
     */
    def readColor(): Ev3ColorSensor.Color = this.synchronized {
      Ev3ColorSensor.Color.values(checkPort(_.readValue0Int()))
    }
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

object Ev3ColorSensor{
  val driverName = "lego-ev3-color"

  /**
   * @see https://docs.ev3dev.org/projects/lego-linux-drivers/en/ev3dev-stretch/sensor_data.html#lego-ev3-color-mode2-value0
   */
  sealed case class Color(name:String)

  object Color {
    val NoColor: Color = Color("No")
    val Black: Color = Color("Black")
    val Blue: Color = Color("Blue")
    val Green: Color = Color("Green")
    val Yellow: Color = Color("Yellow")
    val Red: Color = Color("Red")
    val White: Color = Color("White")
    val Brown: Color = Color("Brown")

    val values: Array[Color] = Array(NoColor,Black,Blue,Green,Yellow,Red,White,Brown)
  }
}


