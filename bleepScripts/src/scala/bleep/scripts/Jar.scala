package bleep.scripts

import bleep.{BleepScript,Started,Commands}

object Jar extends BleepScript("Jar") {
  override def run(started: Started, commands: Commands, args: List[String]): Unit = {
    println("It Worked!")
  }
}

