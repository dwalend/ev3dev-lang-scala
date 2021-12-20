package ev3dev4s.measure

class Degrees(val value:Int) extends AnyVal with Measured[Degrees]:
  def create(i:Int) = new Degrees(i)

class Percent(val value:Int) extends AnyVal with Measured[Percent]:
  def create(i:Int) = new Percent(i)

class MilliSeconds(val value:Int) extends AnyVal with Measured[MilliSeconds]:
  def create(i:Int) = new MilliSeconds(i)

class DegreesPerSecond(val value:Int) extends AnyVal with Measured[DegreesPerSecond]:
  def create(i:Int) = new DegreesPerSecond(i)

class Unitless(val value:Int) extends AnyVal with Measured[Unitless]:
  def create(i:Int) = new Unitless(i)

object Lego: 

  extension(i:Int) {
    def degrees = new Degrees(i)
    def percent = new Percent(i)
    def milliseconds = new MilliSeconds(i)
    def degreesPerSecond = new DegreesPerSecond(i)
    def unitless = new Unitless(i)
  }

//todo when either something like coulomb is available for scala3 or you have to shift back to Scala 2, use someone else's library for unit operations 
trait Measured[M <: Measured[M]] extends Any:
  def value:Int

  def create(i:Int):M

  def +(m:M):M = create(this.value + m.value)

  def - (m:M):M = create(this.value - m.value)

  def < (m:M):Boolean = this.value < m.value

  def > (m:M):Boolean = this.value > m.value

  def * (m:Measured[_]):Int = this.value * m.value

  def / (m:Measured[_]):Int = this.value / m.value

  def abs:M = create(this.value.abs)

  def sign:Unitless = new Unitless(this.value.sign)
