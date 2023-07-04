package ev3dev4s.measured

import scala.annotation.unused

object Exponents:
  sealed trait Exponent
  sealed trait Natural extends Exponent
  final case class Zero() extends Natural
  final case class Succ[+N <: Natural](n: N) extends Natural
  final case class Minus[+N <: Succ[Natural]](n: N) extends Exponent

  type _0 = Zero
  type _1 = Succ[_0]
  type _2 = Succ[_1]
  type _3 = Succ[_2]
  type _4 = Succ[_3]
  type _5 = Succ[_4]
  type _6 = Succ[_5]
  type _7 = Succ[_6]
  type _8 = Succ[_7]
  type _9 = Succ[_8]

  val _0: _0 = Zero()
  val _1: _1 = Succ(_0)
  val _2: _2 = Succ(_1)
  val _3: _3 = Succ(_2)
  val _4: _4 = Succ(_3)
  val _5: _5 = Succ(_4)
  val _6: _6 = Succ(_5)
  val _7: _7 = Succ(_6)
  val _8: _8 = Succ(_7)
  @unused //something has to be last
  val _9: _9 = Succ(_8)

  given Conversion[Int, Exponent] with
    transparent inline def apply(i: Int): Exponent =
      if i < 0 then Minus(positive(-i)) else if i == 0 then Zero() else positive(i)

    private[this] def positive(i: Int): Succ[Natural] = if i == 1 then Succ(Zero()) else Succ(positive(i - 1))

  type NatSum[X <: Natural, Y <: Natural] <: Natural = Y match
    case _0 => X
    case Succ[y] => NatSum[Succ[X], y]

  type NatDif[X <: Natural, Y <: Natural] <: Exponent = Y match
    case _0 => X
    case Succ[y] => X match
      case _0 => Minus[Y]
      case Succ[x] => NatDif[x, y]

  type Neg[X <: Exponent] <: Exponent = X match
    case _0 => _0
    case Minus[x] => x
    case _ => Minus[X]

  type Sum[X <: Exponent, Y <: Exponent] <: Exponent = Y match
    case _0 => X
    case Minus[y] => X match
      case Minus[x] => Minus[NatSum[x, y]]
      case _ => NatDif[X, y]
    case _ => X match
      case Minus[x] => NatDif[Y, x]
      case _ => NatSum[X, Y]

  type Dif[X <: Exponent, Y <: Exponent] <: Exponent = Y match
    case _ => Sum[X, Neg[Y]]

  type IntNatProd[X <: Exponent, Y <: Natural] <: Exponent = Y match
    case _0 => _0
    case _1 => X
    case Succ[y] => Sum[IntNatProd[X, y], X]

  type Prod[X <: Exponent, Y <: Exponent] <: Exponent = Y match
    case Minus[y] => Neg[IntNatProd[X, y]]
    case _ => IntNatProd[X, Y]

  def natPower(x: Float, y: Natural): Float = y match
    case _0() => 1
    case Succ(n) => x * natPower(x, n)

  def power(x: Float, y: Exponent): Float = y match
    case Minus(n) => math.pow(natPower(x, n), -1).toFloat
    case n: Natural => natPower(x, n)
end Exponents
