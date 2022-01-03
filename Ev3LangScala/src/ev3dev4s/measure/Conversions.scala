package ev3dev4s.measure

object Conversions: 

  extension(i:Int) {
    def degrees = new Degrees(i)
    def percent = new Percent(i)
    def milliseconds = new MilliSeconds(i)
    def degreesPerSecond = new DegreesPerSecond(i)
    def unitless = new Unitless(i)
    def microvolts = new Microvolts(i)
    def dutyCyclePercent = new DutyCycle(i)
  }