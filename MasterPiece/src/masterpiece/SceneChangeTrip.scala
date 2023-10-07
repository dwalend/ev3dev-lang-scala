package masterpiece

import ev3dev4s.lego.Gyroscope
import ev3dev4s.scala2measure.Conversions.IntConversions
import ev3dev4s.sensors.SensorPort

import java.lang.Runnable
import scala.Unit

object SceneChangeTrip extends Runnable{
  //start with back of robot on very back wall - zero there.
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
    Robot.movestraight(675.mm, 245.degreesPerSecond)
    Robot.leftRotation(-45.degrees)
  }

  private def pushLeverOnSceneChanger(): Unit = {
    //Push lever on scene changer
    //todo figure out one push or two

    Robot.movestraight(70.mm, 245.degreesPerSecond)
    Robot.movestraight(-50.mm, -245.degreesPerSecond)
    if(PinkOrange.color =="Orange"){
      Robot.movestraight(70.mm, 245.degreesPerSecond)
      Robot.movestraight(-50.mm, -245.degreesPerSecond)

    }

  }
   private def fromScenchangertoStart():Unit = {
     //come back from Scenechanger
     Robot.leftRotation(-180.degrees)
     Robot.movestraight(675.mm, 245.degreesPerSecond)
   }

  //WOPPER RULES

  //WO0PER RULES
}

//Whopper, Whopper, Whopper, Whopper
  //Junior, Double, Triple Whopper
    //Flame-grilled taste with perfect toppers
    //I rule this day
    //Lettuce, mayo, pickle, ketchup
  //It's okay if I don't want that
  //Impossible or bacon Whopper
    //Any Whopper my way

