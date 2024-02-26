package ev3dev4s.measured.dimension

import ev3dev4s.measured.typelevelint
import ev3dev4s.measured.typelevelint.{Divides, IntT, NonZeroIntT, diff, power}

import scala.annotation.targetName
import scala.language.implicitConversions

/**
 * Where the main definitions are, in particular Dim for physical quantities.
 */
object Dimensions:
  /**
   * Type that keeps track of the dimension of a quantity.
   *
   * @tparam Length exponent for the length component of the dim
   * @tparam Time exponent for the time component of the dim
   * @tparam Temperature exponent for the temperature component of the dim
   * @tparam Mass exponent for the mass component of the dim
   * @tparam ElectricCharge exponent for the electric charge component of the dim
   * @tparam SubstanceAmount exponent for the substance amount component of the dim
   * @tparam Cost exponent for the cost component of the dim
   * @tparam Angle exponent for the angle component of the dim
   * @tparam AbstractCharge exponent for the abstract charge component of the dim
   * @tparam AbstractPotential exponent for the abstract potential component of the dim
   */
  opaque type Dim[
    Length <: IntT,            // l
    Time <: IntT,              // t
    Temperature <: IntT,       // p
    Mass <: IntT,              // m
    ElectricCharge <: IntT,    // q
    SubstanceAmount <: IntT,   // n
    Cost <: IntT,              // c
    Angle <: IntT,             // a
    AbstractCharge <: IntT,    // aQ
    AbstractPotential <: IntT, // aP
  ] = Float

  // Standard units (SI units for SI dimensions)
  val ampere   : ElectricCurrent    = 1
  val coulomb  : ElectricCharge     = 1
  val dollar   : Cost               = 1
  val hertz    : Frequency          = 1
  val joule    : Energy             = 1
  val kelvin   : Temperature        = 1
  val kilogram : Mass               = 1
  val metre    : Length             = 1
  val mole     : SubstanceAmount    = 1
  val newton   : Force              = 1
  val ohm      : ElectricResistance = 1
  val pascal   : Pressure           = 1
  val pUnit    : AbstractPotential  = 1
  val rUnit    : AbstractCharge     = 1
  val degree   : Angle              = 1
  val second   : Time               = 1
  val volt     : ElectricPotential  = 1
  val watt     : Power              = 1

  /**
   * Dimensionless quantities can be auto-converted to Floats.
   */
  given Conversion[Float, Uno] with
    inline def apply(d: Float): Uno = d

  /**
   * Int can be auto-converted to Uno.
   */
  given Conversion[Int, Uno] with
    inline def apply(d: Int): Uno = d.toFloat

  /**
   * Float can be auto-converted to Uno.
   */
  given Conversion[Uno, Float] with
    inline def apply(d: Uno): Float = d

  // Trigonometric functions
  val radiansPerDegree = (math.Pi / 180)

  inline def sin(a: Angle): Uno = math.sin(a.toDouble * radiansPerDegree).toFloat
  inline def cos(a: Angle): Uno = math.cos(a.toDouble * radiansPerDegree).toFloat
  inline def tan(a: Angle): Uno = math.tan(a.toDouble * radiansPerDegree).toFloat
  inline def sec(a: Angle): Uno = (1 / math.cos(a.toDouble * radiansPerDegree)).toFloat
  inline def csc(a: Angle): Uno = (1 / math.sin(a.toDouble * radiansPerDegree)).toFloat
  inline def cot(a: Angle): Uno = (1 / math.tan(a.toDouble * radiansPerDegree)).toFloat

  extension (a: Angle)
    /**
     * Normalizes this angle two be between 0 and 2 Pi.
     */
    inline def normalized: Angle = (tau.toFloat * fractionalPart((a: Float) / tau.toFloat))

  // Inverse trigonometric functions
  inline def asin(x: Uno): Angle = (math.asin(x.toDouble)/radiansPerDegree).toFloat
  inline def acos(x: Uno): Angle = (math.acos(x.toDouble)/radiansPerDegree).toFloat
  inline def atan(x: Uno): Angle = (math.atan(x.toDouble)/radiansPerDegree).toFloat
  inline def asec(x: Uno): Angle = (math.acos(1 / x.toDouble)/radiansPerDegree).toFloat
  inline def acsc(x: Uno): Angle = (math.asin(1 / x.toDouble)/radiansPerDegree).toFloat
  inline def acot(x: Uno): Angle = (math.atan(1 / x.toDouble)/radiansPerDegree).toFloat
  
  /**
   * Absolute value
   */
  inline def abs[
    L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, N <: IntT, C <: IntT, A <: IntT, AQ <: IntT, AP <: IntT,
  ](
    x: Dim[L, T, P, M, Q, N, C, A, AQ, AP]
  ): Dim[L, T, P, M, Q, N, C, A, AQ, AP] = math.abs(x)

  /**
   * Floor
   */
  inline def floor[
    L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, N <: IntT, C <: IntT, A <: IntT, AQ <: IntT, AP <: IntT,
  ](
    x: Dim[L, T, P, M, Q, N, C, A, AQ, AP]
  ): Dim[L, T, P, M, Q, N, C, A, AQ, AP] = math.floor(x.toDouble).toFloat

  /**
   * Ceiling
   */
  inline def ceil[
    L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, N <: IntT, C <: IntT, A <: IntT, AQ <: IntT, AP <: IntT,
  ](
    x: Dim[L, T, P, M, Q, N, C, A, AQ, AP]
  ): Dim[L, T, P, M, Q, N, C, A, AQ, AP] = math.ceil(x.toDouble).toFloat

  /**
   * Functions that apply to any quantity, regardless of its dimension.
   */
  extension[
    L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, N <: IntT, C <: IntT, A <: IntT, AQ <: IntT, AP <: IntT,
  ] (x: Dim[L, T, P, M, Q, N, C, A, AQ, AP])

    /**
     * String representation of this quantity, using base dimensions and standard units
     */
    def asString(using L, T, P, M, Q, N, C, A, AQ, AP): String =
      x.toString + " " + dimensionsAsString(
        summon[L], summon[T], summon[P], summon[M], summon[Q], summon[N], summon[C], summon[A], summon[AQ], summon[AP],
      )

    /**
     * String representation of this quantity, using the given unit, as well as base dimensions and standard units
     */
    def asStringWith[
      L2 <: IntT, T2 <: IntT, P2 <: IntT, M2 <: IntT, Q2 <: IntT, N2 <: IntT, C2 <: IntT, A2 <: IntT, AQ2 <: IntT,
      AP2 <: IntT,
    ](unit: Dim[L2, T2, P2, M2, Q2, N2, C2, A2, AQ2, AP2], unitString: String)(using
      l: L, t: T, p: P, m: M, q: Q, n: N, c: C, a: A, aQ: AQ, aP: AP,
      l2: L2, t2: T2, p2: P2, m2: M2, q2: Q2, n2: N2, c2: C2, a2: A2, aQ2: AQ2, aP2: AP2,
    ): String =
      val remainingUnits = dimensionsAsString(
        diff(l, l2), diff(t, t2), diff(p, p2), diff(m, m2), diff(q, q2), diff(n, n2), diff(c, c2), diff(a, a2),
        diff(aQ, aQ2), diff(aP, aP2), 
      )
      (x / unit).toString + " " + Seq(unitString, remainingUnits).filter(_.nonEmpty).mkString("·")

    /**
     * @return the magnitude of this quantity in the given unit
     */
    inline def in(unit: Dim[L, T, P, M, Q, N, C, A, AQ, AP]): Float = x / unit

    /**
     * Usual smaller-than comparison; only defined if the two quantities to be compared have the same dimension
     */
    @targetName("smallerThan") inline def <(
      y: Dim[L, T, P, M, Q, N, C, A, AQ, AP]
    ): Boolean =
      assert(!(x.isNaN || y.isNaN))
      x < y

    /**
     * Usual larger-than comparison; only defined if the two quantities to be compared have the same dimension
     */
    @targetName("largerThan") inline def >(
      y: Dim[L, T, P, M, Q, N, C, A, AQ, AP]
    ): Boolean =
      assert(!(x.isNaN || y.isNaN))
      x > y

    /**
     * Usual smaller-or-equal comparison; only defined if the two quantities to be compared have the same dimension
     */
    @targetName("smallerOrEqual") inline def <=(
      y: Dim[L, T, P, M, Q, N, C, A, AQ, AP]
    ): Boolean =
      assert(!(x.isNaN || y.isNaN))
      x <= y

    /**
     * Usual larger-or-equal comparison; only defined if the two quantities to be compared have the same dimension
     */
    @targetName("largerOrEqual") inline def >=(
      y: Dim[L, T, P, M, Q, N, C, A, AQ, AP]
    ): Boolean =
      assert(!(x.isNaN || y.isNaN))
      x >= y

    /**
     * Usual equality; only defined if the two quantities to be compared have the same dimension
     */
    @targetName("equal") inline def =:=(
      y: Dim[L, T, P, M, Q, N, C, A, AQ, AP]
    ): Boolean =
      assert(!(x.isNaN || y.isNaN))
      x == y

    /**
     * Usual addition; only defined if the two quantities to be added have the same dimension
     */
    @targetName("plus") inline def +(
      y: Dim[L, T, P, M, Q, N, C, A, AQ, AP]
    ): Dim[L, T, P, M, Q, N, C, A, AQ, AP] = x + y

    /**
     * Usual subtraction; only defined if the two quantities to be subtracted have the same dimension
     */
    @targetName("minus") inline def -(
      y: Dim[L, T, P, M, Q, N, C, A, AQ, AP]
    ): Dim[L, T, P, M, Q, N, C, A, AQ, AP] = x - y

    /**
     * Negation
     */
    inline def unary_- : Dim[L, T, P, M, Q, N, C, A, AQ, AP] = -x

    /**
     * Usual multiplication; dimensions are also multiplied
     */
    @targetName("times") inline def *[
      Ly <: IntT, Ty <: IntT, Py <: IntT, My <: IntT, Qy <: IntT, Ny <: IntT, Cy <: IntT, Ay <: IntT, AQy <: IntT,
      APy <: IntT,
    ](
      y: Dim[Ly, Ty, Py, My, Qy, Ny, Cy, Ay, AQy, APy]
     ): Dim[L, T, P, M, Q, N, C, A, AQ, AP] *
        Dim[Ly, Ty, Py, My, Qy, Ny, Cy, Ay, AQy, APy] = x * y

    /**
     * Usual % operator (Behaves like the Scala % operator on Floats.)
     */
    @targetName("modulo") inline def %[
      Ly <: IntT, Ty <: IntT, Py <: IntT, My <: IntT, Qy <: IntT, Ny <: IntT, Cy <: IntT, Ay <: IntT, AQy <: IntT,
      APy <: IntT,
    ](
      y: Dim[Ly, Ty, Py, My, Qy, Ny, Cy, Ay, AQy, APy]
     ): Dim[L, T, P, M, Q, N, C, A, AQ, AP] = x % y

    /**
     * Usual division; dimensions are also divided
     */
    @targetName("over") inline def /[
      Ly <: IntT, Ty <: IntT, Py <: IntT, My <: IntT, Qy <: IntT, Ny <: IntT, Cy <: IntT, Ay <: IntT, AQy <: IntT,
      APy <: IntT,
    ](
       y: Dim[Ly, Ty, Py, My, Qy, Ny, Cy, Ay, AQy, APy]
     ): Dim[L, T, P, M, Q, N, C, A, AQ, AP] /
      Dim[Ly, Ty, Py, My, Qy, Ny, Cy, Ay, AQy, APy] = x / y

    /**
     * Usual exponentiation; dimensions are also exponentiated
     */
    @targetName("toThe") inline def ~[E <: IntT](
      y: E
    ): Dim[L, T, P, M, Q, N, C, A, AQ, AP] ~ E = power(x, y)

    /**
     * @param q the value/quantity to which the abstract charge is to be set
     * @return a quantity equivalent to this one, when the abstract charge is set to the given value
     */
    inline def withAbstractChargeUnitSetTo[
      Lq <: IntT, Tq <: IntT, Pq <: IntT, Mq <: IntT, Qq <: IntT, Nq <: IntT, Cq <: IntT, Aq <: IntT, AQq <: IntT,
      APq <: IntT,
    ](q: Dim[Lq, Tq, Pq, Mq, Qq, Nq, Cq, Aq, AQq, APq])(using qPower: T): WithChargeSetTo[
      Dim[L, T, P, M, Q, N, C, A, AQ, AP],
      Dim[Lq, Tq, Pq, Mq, Qq, Nq, Cq, Aq, AQq, APq],
    ] = x * power(q, qPower)

    /**
     * @param p the value/quantity to which the abstract potential is to be set
     * @return a quantity equivalent to this one, when the abstract potential is set to the given value
     */
    inline def withAbstractPotentialUnitSetTo[
      Lp <: IntT, Tp <: IntT, Pp <: IntT, Mp <: IntT, Qp <: IntT, Np <: IntT, Cp <: IntT, Ap <: IntT, AQp <: IntT,
      APp <: IntT,
    ](
      p: Dim[Lp, Tp, Pp, Mp, Qp, Np, Cp, Ap, AQp, APp]
    )(using pPower: T): WithPotentialSetTo[
      Dim[L, T, P, M, Q, N, C, A, AQ, AP],
      Dim[Lp, Tp, Pp, Mp, Qp, Np, Cp, Ap, AQp, APp],
    ] = x * power(p, pPower)

    /**
     * @return the nth root of this quantity
     */
    inline def root[E <: NonZeroIntT](n: E)(using
      Divides[E, L], Divides[E, T], Divides[E, P], Divides[E, M], Divides[E, Q], Divides[E, N], Divides[E, C],
      Divides[E, A], Divides[E, AQ], Divides[E, AP],
    ): Root[Dim[L, T, P, M, Q, N, C, A, AQ, AP], E] = typelevelint.root(x, n)
end Dimensions
