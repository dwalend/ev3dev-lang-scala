package ev3dev4s.measured.dimension


import ev3dev4s.measured.typelevelint.{Diff, IntT, IntQuotient, NonZeroIntT, Prod, Sum}
import Dimensions.Dim
import scala.annotation.targetName

/**
 * Multiplies the two given dims.
 */
@targetName("times") type *[D1, D2] = DimMap2[Sum, D1, D2]

/**
 * Divides the two given dims.
 */
@targetName("over") type /[D1, D2] = DimMap2[Diff, D1, D2]

/**
 * Raises the given dim to the given power.
 *
 * @tparam D the given dim
 * @tparam P the given power
 */
@targetName("toThe") type ^[D, P <: IntT] = DimMap[[Q <: IntT] =>> Prod[Q, P], D]

/**
 * Returns the Nth root of the given dim, assuming it's a valid operation.
 *
 * @tparam D the given dim, whose exponents should be divisible by N
 * @tparam N the root to take
 */
type Root[D, N <: NonZeroIntT] = DimMap[[Z <: IntT] =>> IntQuotient[Z, N], D]

/**
 * Maps the given function over the components of the given dim.
 *
 * @tparam F the given type function
 * @tparam D the given dim
 */
private[dimension] type DimMap[F[_ <: IntT] <: IntT, D] = D match
  case Dim[l, t, p, m, q, a] => Dim[
    F[l], F[t], F[p], F[m], F[q], F[a]
  ]

/**
 * Maps the given binary function over the components of the two given dims.
 *
 * @tparam F  the given binary type function
 * @tparam D1 the first given dim
 * @tparam D2 the second given dim
 */
private[dimension] type DimMap2[Op[_ <: IntT, _ <: IntT] <: IntT, D1, D2] = D1 match
  case Dim[l1, t1, p1, m1, q1, a1] => D2 match
    case Dim[l2, t2, p2, m2, q2, a2] => Dim[
      Op[l1, l2], Op[t1, t2], Op[p1, p2], Op[m1, m2], Op[q1, q2], Op[a1, a2]
    ]

/**
 * Helper for abstract dimension setters.
 */
private[dimension] type SetterHelper[D, R, M <: IntT] = DimMap2[[I <: IntT, J <: IntT] =>> Sum[I, Prod[M, J]], D, R]
