package ev3dev4s.measured

import ev3dev4s.measured.Measured.{*, given}
import ev3dev4s.measured.Exponents.*

import scala.language.implicitConversions

object AdditionalUnits:
  val percent     : Unitless        = new Unitless(1f / 100f)

  val stud        : Length          = 8 * milli(meter)
  val inch        : Length          = 0.0254 * meter
  val foot        : Length          = 12 * inch

  val minute      : Time            = 60 * second
  val millisecond : Time            = milli(second)
  val ms          : Time            = millisecond

  inline def nano[
    L <: Exponent, T <: Exponent, P <: Exponent, M <: Exponent, Q <: Exponent, C <: Exponent
  ](x: Measurable[L, T, P, M, Q, C]): Measurable[L, T, P, M, Q, C] = x * 1e-9f

  inline def micro[
    L <: Exponent, T <: Exponent, P <: Exponent, M <: Exponent, Q <: Exponent, C <: Exponent
  ](x: Measurable[L, T, P, M, Q, C]): Measurable[L, T, P, M, Q, C] = x * 1e-6f

  inline def milli[
    L <: Exponent, T <: Exponent, P <: Exponent, M <: Exponent, Q <: Exponent, C <: Exponent
  ](x: Measurable[L, T, P, M, Q, C]): Measurable[L, T, P, M, Q, C] = x * 1e-3f

  inline def centi[
    L <: Exponent, T <: Exponent, P <: Exponent, M <: Exponent, Q <: Exponent, C <: Exponent
  ](x: Measurable[L, T, P, M, Q, C]): Measurable[L, T, P, M, Q, C] = x * 1e-2f

  inline def deci[
    L <: Exponent, T <: Exponent, P <: Exponent, M <: Exponent, Q <: Exponent, C <: Exponent
  ](x: Measurable[L, T, P, M, Q, C]): Measurable[L, T, P, M, Q, C] = x * 1e-1f

  inline def kilo[
    L <: Exponent, T <: Exponent, P <: Exponent, M <: Exponent, Q <: Exponent, C <: Exponent
  ](x: Measurable[L, T, P, M, Q, C]): Measurable[L, T, P, M, Q, C] = x * 1e3f

  inline def mega[
    L <: Exponent, T <: Exponent, P <: Exponent, M <: Exponent, Q <: Exponent, C <: Exponent
  ](x: Measurable[L, T, P, M, Q, C]): Measurable[L, T, P, M, Q, C] = x * 1e6f

  inline def giga[
    L <: Exponent, T <: Exponent, P <: Exponent, M <: Exponent, Q <: Exponent, C <: Exponent
  ](x: Measurable[L, T, P, M, Q, C]): Measurable[L, T, P, M, Q, C] = x * 1e9f

end AdditionalUnits
