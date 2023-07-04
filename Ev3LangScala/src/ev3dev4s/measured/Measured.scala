package ev3dev4s.measured

import ev3dev4s.measured.Exponents.{Exponent, Minus, Sum, Dif, Prod, _0, _1, _2, _3, power}

import scala.annotation.targetName

object Measured:
  //noinspection ScalaUnusedSymbol
  class Measurable[
    L <: Exponent,
    T <: Exponent,
    A <: Exponent,
    M <: Exponent,  //todo if it ever matters - remove unused slots
    E <: Exponent,
    S <: Exponent,
  ](val v:Float) extends AnyVal:
    //todo override toString() after bugfix
    //todo is there a way to have "the most specific win" while providing a default - to avoid this inline match that knows all the specified types? Didn't work with extension methods
    transparent inline def pretty:String =
      inline this match {
        case l:Length => s"${v}m"
        case t:Time => s"${v}s"
        case a:Angle => s"${v}d"
        case p:ElectricPotential => s"${v}V"
        case _ => s"$v some unit"
      }

  type DimMap[Op[_ <: Exponent] <: Exponent, X] = X match
    case Measurable[l, t, a, m, q, c] => Measurable[Op[l], Op[t], Op[a], Op[m], Op[q], Op[c]]

  type DimMap2[Op[_ <: Exponent, _ <: Exponent] <: Exponent, X, Y] = X match
    case Measurable[lx, tx, ax, mx, qx, cx] => Y match
      case Measurable[ly, ty, ay, my, qy, cy] =>
        Measurable[Op[lx, ly], Op[tx, ty], Op[ax, ay], Op[mx, my], Op[qx, qy], Op[cx, cy]]

  @targetName("times") type *[X, Y] = DimMap2[Sum, X, Y]
  @targetName("over") type /[X, Y] = DimMap2[Dif, X, Y]
  @targetName("toThe") type ^[X, Y <: Exponent] = DimMap[[Z <: Exponent] =>> Prod[Z, Y], X]

  type Unitless = Measurable[_0, _0, _0, _0, _0, _0]

  type Length          = Measurable[_1, _0, _0, _0, _0, _0]
  type Time            = Measurable[_0, _1, _0, _0, _0, _0]
  type Angle           = Measurable[_0, _0, _1, _0, _0, _0]
  type Mass            = Measurable[_0, _0, _0, _1, _0, _0]
  type ElectricCharge  = Measurable[_0, _0, _0, _0, _1, _0]
  type SubstanceAmount = Measurable[_0, _0, _0, _0, _0, _1]

  type Acceleration = Length / Time ^ _2
  type Area = Length ^ _2
  type Density = Mass / Volume
  type ElectricCurrent = ElectricCharge / Time
  type Energy = Mass * Area / Time ^ _2
  type ElectricPotential = Energy / ElectricCharge
  type Force = Energy / Length
  type Frequency = Time ^ Minus[_1]
  type Power = Energy / Time
  type Pressure = Force / Area
  type Velocity = Length / Time //todo this is Speed
  type Viscosity = Pressure * Time
  type Volume = Length ^ _3

  val meter   : Length            = new Length(1)
  val ampere  : ElectricCurrent   = new ElectricCurrent(1)
  val coulomb : ElectricCharge    = new ElectricCharge(1)
  val hertz   : Frequency         = new Frequency(1)
  val joule   : Energy            = new Energy(1)
  val degree  : Angle             = new Angle(1)
  val kilogram: Mass              = new Mass(1)
  val mole    : SubstanceAmount   = new SubstanceAmount(1)
  val newton  : Force             = new Force(1)
  val pascal  : Pressure          = new Pressure(1)
  val second  : Time              = new Time(1)
  val volt    : ElectricPotential = new ElectricPotential(1)
  val watt    : Power             = new Power(1)

  given Conversion[Float, Unitless] with
    inline def apply(d: Float): Unitless = new Unitless(d)

  given Conversion[Double, Unitless] with
    inline def apply(d: Double): Unitless = new Unitless(d.toFloat)

  given Conversion[Int, Unitless] with
    inline def apply(d: Int): Unitless = new Unitless(d.toFloat)

  given Conversion[Unitless, Float] with
    inline def apply(d: Unitless): Float = d.v

  extension[
    L <: Exponent, T <: Exponent, A <: Exponent, M <: Exponent, Q <: Exponent, C <: Exponent
  ] (x: Measurable[L, T, A, M, Q, C])
    inline def in(units: Measurable[L, T, A, M, Q, C]): Measurable[L, T, A, M, Q, C] =
      new Measurable[L, T, A, M, Q, C](x.v / units.v)

    @targetName("plus") inline def +(y: Measurable[L, T, A, M, Q, C]): Measurable[L, T, A, M, Q, C] =
      new Measurable[L, T, A, M, Q, C](x.v + y.v)

    @targetName("minus") inline def -(y: Measurable[L, T, A, M, Q, C]): Measurable[L, T, A, M, Q, C] =
      new Measurable[L, T, A, M, Q, C](x.v - y.v)

    @targetName("minus") inline def unary_- = //: Measurable[L, T, A, M, Q, C] = //todo maybe a bug - can't define a type for unary_-
      new Measurable[L, T, A, M, Q, C](- x.v)

    @targetName("remainder") inline def %(y: Measurable[L, T, A, M, Q, C]): Measurable[L, T, A, M, Q, C] =
      new Measurable[L, T, A, M, Q, C](x.v % y.v)

    @targetName("times") inline def *[
      Ly <: Exponent, Ty <: Exponent, Ay <: Exponent, My <: Exponent, Qy <: Exponent, Cy <: Exponent
    ](y: Measurable[Ly, Ty, Ay, My, Qy, Cy]): Measurable[L, T, A, M, Q, C] * Measurable[Ly, Ty, Ay, My, Qy, Cy] =
      new(Measurable[L, T, A, M, Q, C] * Measurable[Ly, Ty, Ay, My, Qy, Cy])(x.v * y.v)

    @targetName("over") inline def /[
      Ly <: Exponent, Ty <: Exponent, Ay <: Exponent, My <: Exponent, Qy <: Exponent, Cy <: Exponent
    ](y: Measurable[Ly, Ty, Ay, My, Qy, Cy]): Measurable[L, T, A, M, Q, C] / Measurable[Ly, Ty, Ay, My, Qy, Cy] =
      new(Measurable[L, T, A, M, Q, C] / Measurable[Ly, Ty, Ay, My, Qy, Cy])(x.v / y.v)
//todo    @targetName("toThe") inline def ^[E <: Exponent](y: E): Measurable[L, T, P, M, Q, C] ^ E = power(x.v, y)

    @targetName("lessThan") inline def <(y: Measurable[L, T, A, M, Q, C]): Boolean =
      x.v < y.v
    @targetName("lessThanOrEqual") inline def <=(y: Measurable[L, T, A, M, Q, C]): Boolean =
      x.v <= y.v
    @targetName("safeEquals") inline def ===(y: Measurable[L, T, A, M, Q, C]): Boolean =
      x.v == y.v
    @targetName("greaterThanOrEqual") inline def >=(y: Measurable[L, T, A, M, Q, C]): Boolean =
      x.v >= y.v
    @targetName("greaterThan") inline def >(y: Measurable[L, T, A, M, Q, C]): Boolean =
      x.v > y.v

end Measured

object Angle:
  import Measured.{Angle,degree,Measurable,`*`,given}
  import ev3dev4s.measured.Exponents.*
  import scala.language.implicitConversions

  val rotation: Angle = 360 * degree
  val radian: Angle = rotation / (2 * scala.math.Pi.toFloat)

  extension (x: Angle)
    /**
     * Return a value between - 1/2 full rotation and 1/2 rotation
     */
    inline def unwind:Angle =
      val remainder = x % rotation
      if (remainder < - (rotation/2)) remainder + rotation
      else if (remainder >= (rotation/2)) remainder - rotation
      else remainder

    //todo override toString() after bugfix
    //todo is there a way to have "the most specific win" while providing a default? Didn't work with extension methods
    inline def prettier: String = s"${x.v}d"

end Angle