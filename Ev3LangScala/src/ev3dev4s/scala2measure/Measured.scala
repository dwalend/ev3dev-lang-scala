package ev3dev4s.scala2measure

// todo when either something like coulomb is available for scala3 or you have
// to shift back to Scala 2, use someone else's library for unit operations

/**
 * A universal trait of things that are measured with units.
 *
 * This provides general-purpose operations for those things.
 */
trait Measured[M <: Measured[M]] extends Any {
  def v: Float // todo fine to change to Ratio if Float doesn't work out

  def create(i: Float): M

  //noinspection
  def toString(): String // todo figure out def toString(): String

  def unary_-(): M = create(-this.v) // todo add type annotation when allowed

  def +(m: M): M = create(this.v + m.v)

  def -(m: M): M = create(this.v - m.v)

  def <(m: M): Boolean = this.v < m.v

  def >(m: M): Boolean = this.v > m.v

  def <=(m: M): Boolean = this.v <= m.v

  def >=(m: M): Boolean = this.v >= m.v

  def !=(m: M): Boolean = this.v != m.v

  def *(m: Measured[_]): Float = this.v * m.v

  def *(f: Float): M = create(this.v * f)

  def /(m: Measured[_]): Float = this.v / m.v

  def %(m: Measured[_]): M = create(this.v % m.v)

  def abs: M = create(this.v.abs)

  def sign: Unitless = new Unitless(this.v.sign)

  def round: Int = v.round

  //todo figure out how to do a Range
}

object Measured {
  def min[M <: Measured[M]](m1: M, m2: M): M = if (m1.v < m2.v) m1
  else m2

  def max[M <: Measured[M]](m1: M, m2: M): M = if (m1.v > m2.v) m1
  else m2
}


class Degrees(val v: Float) extends AnyVal with Measured[Degrees] {
  def create(i: Float) = new Degrees(i)

  override def toString(): String = s"${v.round}d"

  /**
   * @return a v between -180 and +179
   */
  def unwind: Degrees = {
    val remainder = v % 360
    if (remainder < -180) create(remainder + 360)
    else if (remainder >= 180) create(remainder - 360)
    else create(remainder)
  }
}

class Percent(val v: Float) extends AnyVal with Measured[Percent] {
  def create(i: Float) = new Percent(i)

  override def toString(): String = s"$v%"
}

class MilliSeconds(val v: Float) extends AnyVal with Measured[MilliSeconds] {
  def create(i: Float) = new MilliSeconds(i)

  override def toString(): String = s"${v}ms"
}

class DegreesPerSecond(val v: Float) extends AnyVal with Measured[DegreesPerSecond] {
  def create(i: Float) = new DegreesPerSecond(i)

  override def toString(): String = s"${v}dps"
}

class Unitless(val v: Float) extends AnyVal with Measured[Unitless] {
  def create(i: Float) = new Unitless(i)

  override def toString(): String = s"$v"
}

class Microvolts(val v: Float) extends AnyVal with Measured[Microvolts] {
  def create(i: Float) = new Microvolts(i)

  override def toString(): String = s"${v}uV"
}

class DutyCycle(val v: Float) extends AnyVal with Measured[DutyCycle] {
  def create(i: Float) = new DutyCycle(i)

  override def toString(): String = s"$v%"
}

class LedIntensity(val v: Float) extends AnyVal with Measured[LedIntensity] {
  def create(i: Float) = new LedIntensity(i)

  override def toString(): String = s"${v}i"
}

class MilliMeters(val v: Float) extends AnyVal with Measured[MilliMeters] {
  def create(i: Float) = new MilliMeters(i)

  override def toString(): String = s"${v}mm"
}

class Hertz(val v: Float) extends AnyVal with Measured[Hertz] {
  override def create(i: Float): Hertz = new Hertz(i)

  override def toString(): String = s"${v}Hz"
}