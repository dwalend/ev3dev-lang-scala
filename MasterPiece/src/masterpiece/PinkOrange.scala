package masterpiece

import java.lang.Runnable
import scala.Unit

object PinkOrange extends Runnable{
  var color = "Orange"
  override def run(): Unit = {
    if(color =="Orange"){
      color ="Pink"
    }else{
      color="Orange"
    }
  }
// 004

}