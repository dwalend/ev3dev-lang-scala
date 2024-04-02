package ev3dev4s.measured.dimension

import ev3dev4s.measured.dimension.Dimensions.Dim

import scala.language.implicitConversions
import ev3dev4s.measured.exponents.IntT


inline def nano[
  L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, A <: IntT,
](x: Dim[L, T, P, M, Q, A]): Dim[
  L, T, P, M, Q, A
] = x * 1e-9.toFloat

inline def micro[
  L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, A <: IntT,
](x: Dim[L, T, P, M, Q, A]): Dim[
  L, T, P, M, Q, A
] = x * 1e-6.toFloat

inline def milli[
  L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, A <: IntT,
](x: Dim[L, T, P, M, Q, A]): Dim[
  L, T, P, M, Q, A
] = x * 1e-3.toFloat

inline def centi[
  L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, A <: IntT,
](x: Dim[L, T, P, M, Q, A]): Dim[
  L, T, P, M, Q, A
] = x * 1e-2.toFloat

inline def deci[
  L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, A <: IntT,
](x: Dim[L, T, P, M, Q, A]): Dim[
  L, T, P, M, Q, A
] = x * 1e-1.toFloat

inline def kilo[
  L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, A <: IntT,
](x: Dim[L, T, P, M, Q, A]): Dim[
  L, T, P, M, Q, A
] = x * 1e3.toFloat

inline def mega[
  L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, A <: IntT,
](x: Dim[L, T, P, M, Q, A]): Dim[
  L, T, P, M, Q, A
] = x * 1e6.toFloat

inline def giga[
  L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, A <: IntT,
](x: Dim[L, T, P, M, Q, A]): Dim[
  L, T, P, M, Q, A
] = x * 1e9.toFloat

inline def tera[
  L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, A <: IntT,
](x: Dim[L, T, P, M, Q, A]): Dim[
  L, T, P, M, Q, A
] = x * 1e12.toFloat

inline def peta[
  L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, A <: IntT,
](x: Dim[L, T, P, M, Q, A]): Dim[
  L, T, P, M, Q, A
] = x * 1e15.toFloat

inline def kibi[
  L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, A <: IntT,
](x: Dim[L, T, P, M, Q, A]): Dim[
  L, T, P, M, Q, A
] = x * 1024.toFloat

inline def mebi[
  L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, A <: IntT,
](x: Dim[L, T, P, M, Q, A]): Dim[
  L, T, P, M, Q, A
] = x * math.pow(1024, 2).toFloat

inline def gibi[
  L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, A <: IntT,
](x: Dim[L, T, P, M, Q, A]): Dim[
  L, T, P, M, Q, A
] = x * math.pow(1024, 3).toFloat

inline def tebi[
  L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, A <: IntT,
](x: Dim[L, T, P, M, Q, A]): Dim[
  L, T, P, M, Q, A
] = x * math.pow(1024, 4).toFloat

inline def pebi[
  L <: IntT, T <: IntT, P <: IntT, M <: IntT, Q <: IntT, A <: IntT,
](x: Dim[L, T, P, M, Q, A]): Dim[
  L, T, P, M, Q, A
] = x * math.pow(1024, 5).toFloat

