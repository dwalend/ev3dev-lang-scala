package ev3dev4s.measure

//todo when either something like coulomb is available for scala3 or you have to shift back to Scala 2, use someone else's library for unit operations 
/**
 * A universal trait of things that are measured with units.
 * 
 * This provides general-purpose operations for those things.
 */
trait Measured[M <: Measured[M]] extends Any:
  def value:Int //todo fine to change to Float or spire's Ratio if Int doesn't work out

  def create(i:Int):M

 //todo figure out def toString():String

  def +(m:M):M = create(this.value + m.value)

  def - (m:M):M = create(this.value - m.value)

  def < (m:M):Boolean = this.value < m.value

  def > (m:M):Boolean = this.value > m.value

  def <= (m:M):Boolean = this.value <= m.value

  def >= (m:M):Boolean = this.value >= m.value

  def != (m:M):Boolean = this.value != m.value

  def * (m:Measured[_]):Int = this.value * m.value

  def / (m:Measured[_]):Int = this.value / m.value

  def % (m:Measured[_]):M = create(this.value % m.value)

  def abs:M = create(this.value.abs)

  def sign:Unitless = new Unitless(this.value.sign)

  //todo figure out how to do a Range

class Degrees(val value:Int) extends AnyVal with Measured[Degrees]:
  def create(i:Int) = new Degrees(i)

  //todo figure out override def toString() = s"${Integer.toString(value)}d"

  /**
   * @return a value between -180 and +179 
   */ 
  def unwind:Degrees = 
    val remainder = value % 360
    if(remainder < -180) create(remainder + 360)
    else if(remainder >= 180) create(remainder - 360)
    else create(remainder)

class Percent(val value:Int) extends AnyVal with Measured[Percent]:
  def create(i:Int) = new Percent(i)

class MilliSeconds(val value:Int) extends AnyVal with Measured[MilliSeconds]:
  def create(i:Int) = new MilliSeconds(i)

class DegreesPerSecond(val value:Int) extends AnyVal with Measured[DegreesPerSecond]:
  def create(i:Int) = new DegreesPerSecond(i)

class Unitless(val value:Int) extends AnyVal with Measured[Unitless]:
  def create(i:Int) = new Unitless(i)

class Microvolts(val value:Int) extends AnyVal with Measured[Microvolts]:
  def create(i:Int) = new Microvolts(i)

class DutyCycle(val value:Int) extends AnyVal with Measured[DutyCycle]:
  def create(i:Int) = new DutyCycle(i)

class LedInensity(val value:Int) extends AnyVal with Measured[LedInensity]:
  def create(i:Int) = new LedInensity(i)