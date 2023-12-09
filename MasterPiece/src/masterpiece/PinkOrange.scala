package masterpiece

import java.lang.Runnable
import scala.Unit

/**
 * Choose between Pink - one bump - or Orange - two bumps for the scene changer
 */
object PinkOrange extends Runnable{
  var color = "Orange"
  override def run(): Unit = {
    if(color =="Orange"){
      color ="Pink"
    }else{
      color="Orange"
    }
  }
}