package ev3dev4s.examples

import ev3dev4s.{Ev3System, Log}
import ev3dev4s.lego.{Display, Sound}
import ev3dev4s.scala2measure.Conversions.IntConversions

//noinspection ScalaUnusedSymbol
object KitchenSink extends Runnable {
  override def run(): Unit = {
    Log.log(s"\nKitchenSink started....\n\n")
    AllGadgetsExample.initializeSensors()
    AllGadgetsExample.describeSensors()
    AllGadgetsExample.describeMotors()

    FiveXHelloWorld.run()

    for (ii <- 1 to 4) {
      Log.log("Try turning the wheels or robot (loop $ii}")
      AllGadgetsExample.describeSensors()
      AllGadgetsExample.describeMotors()
      Thread.sleep(1000)
    }

    Log.log(s"\nKitchenSink finished....\n\n")
  }
}
