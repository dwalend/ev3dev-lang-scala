package ev3dev4s.measure

//todo when either something like coulomb is available for scala3 or you have to shift back to Scala 2, use someone else's library for unit operations 
/**
 * A universal trait of things that are measured with units.
 * 
 * This provides general-purpose operations for those things.
 */
trait Measured[M <: Measured[M]] extends Any {
  def value: Float //todo fine to change to Ratio if Float doesn't work out

  def create(i: Float): M

  def toString(): String

  //todo figure out def toString():String
  def unary_- = create(-this.value) //todo add type annotation when allowed

  def +(m: M): M = create(this.value + m.value)

  def -(m: M): M = create(this.value - m.value)

  def <(m: M): Boolean = this.value < m.value

  def >(m: M): Boolean = this.value > m.value

  def <=(m: M): Boolean = this.value <= m.value

  def >=(m: M): Boolean = this.value >= m.value

  def !=(m: M): Boolean = this.value != m.value

  def *(m: Measured[_]): Float = this.value * m.value

  def /(m: Measured[_]): Float = this.value / m.value

  def %(m: Measured[_]): M = create(this.value % m.value)

  def abs: M = create(this.value.abs)

  def sign: Unitless = new Unitless(this.value.sign)

  def round: Int = value.round

  //todo figure out how to do a Range
}

object Measured{
  def min[M <: Measured[M]](m1:M,m2:M):M = if(m1.value < m2.value) m1
                                            else m2
  def max[M <: Measured[M]](m1:M,m2:M):M = if(m1.value > m2.value) m1
                                            else m2
}


class Degrees(val value:Float) extends AnyVal with Measured[Degrees] {
  def create(i: Float) = new Degrees(i)

  override def toString(): String = s"${value}d"

  /**
   * @return a value between -180 and +179 
   */
  def unwind: Degrees = {
    val remainder = value % 360
    if (remainder < -180) create(remainder + 360)
    else if (remainder >= 180) create(remainder - 360)
    else create(remainder)
  }
}

class Percent(val value:Float) extends AnyVal with Measured[Percent] {
  def create(i: Float) = new Percent(i)

  override def toString(): String = s"$value%"
}

class MilliSeconds(val value:Float) extends AnyVal with Measured[MilliSeconds] {
  def create(i: Float) = new MilliSeconds(i)

  override def toString(): String = s"${value}ms"
}

class DegreesPerSecond(val value:Float) extends AnyVal with Measured[DegreesPerSecond] {
  def create(i: Float) = new DegreesPerSecond(i)

  override def toString(): String = s"${value}dps"
}

class Unitless(val value:Float) extends AnyVal with Measured[Unitless] {
  def create(i: Float) = new Unitless(i)

  override def toString(): String = s"${value}"
}

class Microvolts(val value:Float) extends AnyVal with Measured[Microvolts]{
  def create(i:Float) = new Microvolts(i)
  override def toString():String = s"${value}uv"
}

class DutyCycle(val value:Float) extends AnyVal with Measured[DutyCycle] {
  def create(i: Float) = new DutyCycle(i)

  override def toString(): String = s"${value}%"
}

class LedIntensity(val value:Float) extends AnyVal with Measured[LedIntensity] {
  def create(i: Float) = new LedIntensity(i)

  override def toString(): String = s"${value}"
}


class MilliMeters(val value:Float) extends AnyVal with Measured[MilliMeters] {
  def create(i: Float) = new MilliMeters(i)

  override def toString(): String = s"${value}mm"
}

