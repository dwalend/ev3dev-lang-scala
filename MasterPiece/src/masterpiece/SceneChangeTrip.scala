package masterpiece

import ev3dev4s.actuators.MotorPort
import ev3dev4s.lego.{Gyroscope, Motors}
import ev3dev4s.scala2measure.Conversions.IntConversions
import ev3dev4s.sensors.SensorPort

import java.lang.Runnable
import scala.Unit

/**
 * Start with back of robot on very back wall - zero there.
 */
object SceneChangeTrip extends Runnable {
  override def run(): Unit = {
    setGyro()

    fromStartToSceneChanger()
    pushLeverOnSceneChanger()
    fromScenchangertoStart()
  }

//WOPPER RULES
  private def setGyro(): Unit = {
    //Set the gyroscope
    Gyroscope.reset(SensorPort.One)
    Gyroscope.setHeading(SensorPort.One, 0.degrees)
  }
//Avril is the best in the entire world and she is much better than Aanya
  private def fromStartToSceneChanger(): Unit ={
    //move from start to the scene changer
    Robot.movestraight(605.mm)

    Robot.leftRotation(-45.degrees)
  }

  private def pushLeverOnSceneChanger(): Unit = {
    //Push lever on scene changer
    Robot.movestraight(70.mm)
    Robot.moveStraightBackward(55.mm)
    if (PinkOrange.color == "Orange") {
      Robot.movestraight(70.mm)
      //Raise the left lift - use run for duration to deal with a jam
      Motors.runForDuration(MotorPort.B, (5 * 1000).milliseconds, -100.degreesPerSecond)
      Robot.moveStraightBackward(55.mm)
    }
  }

  private def fromScenchangertoStart():Unit = {
     Robot.leftRotation(-180.degrees)
     Robot.movestraight(695.mm)
   }
}

//Whopper, Whopper, Whopper, Whopper
  //Junior, Double, Triple Whopper
    //Flame-grilled taste with perfect toppers
    //I rule this day
    //Lettuce, mayo, pickle, ketchup
  //It's okay if I don't want that
  //Impossible or bacon Whopper
    //Any Whopper my way