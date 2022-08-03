package net.walend.cargoconnect

import ev3dev4s.measure.Conversions._

import ev3dev4s.actuators.{MotorStopCommand ,MotorState}

import ev3dev4s.os.Time

import net.walend.lessons.Move

object ForkMoves { //todo three states: In, Out, OutAndUp

  /**
   * Runs the fork out to a best-guess maximum length before it traverses up
   */
  object ForkOut extends Move {
    def move(): Unit = {
      Robot.forkMotor.coast()
      Robot.forkMotor.writeStopAction(MotorStopCommand.HOLD)
      Robot.forkMotor.runToRelativePosition(500.degreesPerSecond, 450.degrees) //out 450
      while (
        Robot.forkMotor.readState().contains(MotorState.RUNNING) //todo use isRunning after recompile
      ) {
        Time.pause(1.milliseconds)
      }
    }
  }

  /**
   * Runs the fork out, then up, until it stalls.
   */
  object ForkOutUp extends Move {
    def move(): Unit = {
      Robot.forkMotor.run(500.degreesPerSecond)

      while (!Robot.forkMotor.readIsStalled()) {
        Time.pause(1.milliseconds)
      }
      Robot.forkMotor.hold()
    }
  }

  /**
   * Runs the fork in until it stalls.
   */
  object ForkIn extends Move {
    def move(): Unit = {
      Robot.forkMotor.run(-500.degreesPerSecond)

      while (!Robot.forkMotor.readIsStalled()) {
        Time.pause(1.milliseconds)
      }
      Robot.forkMotor.brake()
    }
  }
}


