package ev3dev4s.measured.dimension

import ev3dev4s.measured.dimension.Dimensions.{degree, kilogram, meter, second, unitless, watt, *, given}
import ev3dev4s.measured.exponents._3

import scala.language.implicitConversions

val percent: Uno = 0.01f * unitless

val radian: Angle = (180 / math.Pi).toFloat * degree

val stud: Length = 8 * milli(meter)

val minute    : Time = 60 * second
val hour      : Time = 60 * minute
val day       : Time = 24 * hour
val julianYear: Time = 365.25f * day

val wattHour    : Energy = watt * hour
val kiloWattHour: Energy = kilo(wattHour)

val inch        : Length = 0.0254f * meter
val foot        : Length = 12 * inch
val yard        : Length = 3 * foot
val furlong     : Length = 220 * yard

val gram     : Mass = milli(kilogram)
val poundMass: Mass = 0.45359237f * kilogram
val stone    : Mass = 14 * poundMass

val liter      : Volume = 1e-3f * meter ^ _3
val fluidOunce : Volume = 29.5735295625f * milli(liter)
val liquidPint : Volume = 16 * fluidOunce
val liquidQuart: Volume = 2 * liquidPint
val usGallon   : Volume = 4 * liquidQuart
