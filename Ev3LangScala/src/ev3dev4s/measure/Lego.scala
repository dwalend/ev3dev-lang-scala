package ev3dev4s.measure

import coulomb.si.Meter
import coulomb.CoulombExtendWithUnits
import coulomb.accepted.Degree
import coulomb.Quantity
import coulomb.accepted.Percent

object Lego: 
      type Degrees = Quantity[Int,Degree]

      type Percents = Quantity[Int,Percent]

      extension(i:Int) {
            def degrees = i.withUnit[Degree]
            def percent = i.withUnit[Percent]
      }




