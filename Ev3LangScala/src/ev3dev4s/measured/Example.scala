package ev3dev4s.measured

import AdditionalUnits.*
import Measured.{*, given}
import Exponents.*
import Angle.*

import scala.language.implicitConversions

object Example:
  def main(args: Array[String]): Unit =
    println("start")
    val v1: Velocity      = 0.1 * meter / milli(second)

    val v2: Length / Time = 15 * meter / second
    println((v1 + v2).in(kilo(meter) / minute).pretty)

    val lightYear: Length = 9460730472580800.0 * meter

    val squashed: Length = lightYear.in(giga(kilo(meter)))

    println(squashed.pretty)
/*
    val someVolts:ElectricPotential = 6 * volt
    println(someVolts.pretty)

    val lotsOfVolts:ElectricPotential = 16 * giga(volt)

    println(lotsOfVolts.pretty)
*/
    val someAngle = 445 * degree

    println(someAngle.pretty)

    val smallerAngle = someAngle % Angle.rotation

    println(smallerAngle.pretty)

    println(someAngle.unwind.prettier)

    println( (- someAngle.unwind).pretty)

    println( (- someAngle).unwind.pretty)

    println(someAngle < smallerAngle)
    println(someAngle <= smallerAngle)
    println(someAngle === smallerAngle)
    println(someAngle >= smallerAngle)
    println(someAngle > smallerAngle)

//    val incorrect1 = v1 + lightYear
//    val incorrect2: Time = 1 * metre
//    val incorrect3: Boolean = v1 < lightYear
//    val incorrect4: Boolean = v1 === lightYear

end Example
