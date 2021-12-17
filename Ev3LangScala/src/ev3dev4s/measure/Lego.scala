package ev3dev4s.measure

class Degrees(val value:Int) extends AnyVal

class Percent(val value:Int) extends AnyVal

class MilliSeconds(val value:Int) extends AnyVal

class DegreesPerSecond(val value:Int) extends AnyVal

object Lego: 

  extension(i:Int) {
    def degrees = new Degrees(i)
    def percent = new Percent(i)
    def milliseconds = new MilliSeconds(i)
    def degreesPerSecond = new DegreesPerSecond(i)
  }

