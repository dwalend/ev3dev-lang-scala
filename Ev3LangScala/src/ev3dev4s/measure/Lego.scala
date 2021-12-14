package ev3dev4s.measure

class Degrees(val value:Int) extends AnyVal

class Percent(val value:Int) extends AnyVal


object Lego: 

  extension(i:Int) {
    def degrees = new Degrees(i)
    def percent = new Percent(i)
  }

