package net.walend.cargoconnect

import ev3dev4s.measure.Conversions.*

import ev3dev4s.actuators.MotorStopCommand

object ForkCommands: //todo rename fork? //todo three states: In, Out, OutAndUp
  
  object ForkOutAndUp extends TtyMenuAction: //todo instead of a menu action, create a trait Move and extend that    
    def act(menu: TtyMenu):Unit =
      Robot.forkMotor.coast()
      Robot.forkMotor.writeStopAction(MotorStopCommand.HOLD)
      Robot.forkMotor.runToRelativePosition(500.degreesPerSecond,460.degrees) //out 450, up 10

  object ForkIn extends TtyMenuAction:
    def act(menu: TtyMenu):Unit =
      Robot.forkMotor.coast()
      Robot.forkMotor.writeStopAction(MotorStopCommand.HOLD)
      Robot.forkMotor.runToRelativePosition(-500.degreesPerSecond,-460.degrees)

  object ForkUpABit extends TtyMenuAction:
    def act(menu: TtyMenu):Unit =
      Robot.forkMotor.coast()
      Robot.forkMotor.writeStopAction(MotorStopCommand.HOLD)
      Robot.forkMotor.runToRelativePosition(500.degreesPerSecond,10.degrees)



