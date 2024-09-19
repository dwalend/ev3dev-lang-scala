package ev3dev4s.scala2measure

object Conversions {

  implicit class IntConversions(i: Int) {
    def degrees = new Degrees(i.toFloat)

    def percent = new Percent(i.toFloat)

    def milliseconds = new MilliSeconds(i.toFloat)

    def ms = new MilliSeconds(i.toFloat)

    def degreesPerSecond = new DegreesPerSecond(i.toFloat)
    def dps = degreesPerSecond

    def unitless = new Unitless(i.toFloat)

    def microvolts = new Microvolts(i.toFloat)

    def dutyCyclePercent = new DutyCycle(i.toFloat)

    def ledIntensity = new LedIntensity(i.toFloat)

    def millimeters = new MilliMeters(i.toFloat)
    def mm = new MilliMeters(i.toFloat)
    def cm = new MilliMeters(i.toFloat * 10)
    def studs = new MilliMeters(i.toFloat * 8)

    def Hz = new Hertz(i.toFloat)
  }

  implicit class FloatConversions(f: Float) {
    def degrees = new Degrees(f)

    def percent = new Percent(f)

    def milliseconds = new MilliSeconds(f)

    def ms = new MilliSeconds(f)

    def degreesPerSecond = new DegreesPerSecond(f)
    def dps = degreesPerSecond

    def unitless = new Unitless(f)

    def microvolts = new Microvolts(f)

    def dutyCyclePercent = new DutyCycle(f)

    def ledIntensity = new LedIntensity(f)

    def millimeters = new MilliMeters(f)
    def mm = new MilliMeters(f)
    def cm = new MilliMeters(f * 10)
    def studs = new MilliMeters(f * 8)

    def Hz = new Hertz(f)
  }

  implicit class LongConversions(l: Long) {
    def degrees = new Degrees(l.toFloat)

    def percent = new Percent(l.toFloat)

    def milliseconds = new MilliSeconds(l.toFloat)
    def ms = new MilliSeconds(l.toFloat)

    def degreesPerSecond = new DegreesPerSecond(l.toFloat)

    def unitless = new Unitless(l.toFloat)

    def microvolts = new Microvolts(l.toFloat)

    def dutyCyclePercent = new DutyCycle(l.toFloat)

    def ledIntensity = new LedIntensity(l.toFloat)

    def millimeters = new MilliMeters(l.toFloat)
    def mm = new MilliMeters(l.toFloat)
    def cm = new MilliMeters(l.toFloat * 10)
    def studs = new MilliMeters(l.toFloat * 8)

    def Hz = new Hertz(l.toFloat)
  }
}