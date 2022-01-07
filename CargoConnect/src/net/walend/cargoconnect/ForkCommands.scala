package net.walend.cargoconnect

import ev3dev4s.measure.Conversions.*

import ev3dev4s.actuators.MotorStopCommand

import ev3dev4s.os.Time

object ForkCommands: //todo rename fork? //todo three states: In, Out, OutAndUp
  
  /**
   * Runs the fork out to a best-guess maximum length before it traverses up
   */ 
  object ForkOut extends TtyMenuAction: //todo instead of a menu action, create a trait Move and extend that    
    def act(menu: TtyMenu):Unit =
      Robot.forkMotor.coast()
      Robot.forkMotor.writeStopAction(MotorStopCommand.HOLD)
      Robot.forkMotor.runToRelativePosition(500.degreesPerSecond,450.degrees) //out 450

/*      
  object ForkIn extends TtyMenuAction:
    def act(menu: TtyMenu):Unit =
      Robot.forkMotor.coast()
      Robot.forkMotor.writeStopAction(MotorStopCommand.HOLD)
      Robot.forkMotor.runToRelativePosition(-500.degreesPerSecond,-460.degrees)
*/
  /**
   * Runs the fork out, then up, until it stalls.
   */ 
  object ForkOutUp extends TtyMenuAction:   
    def act(menu: TtyMenu):Unit =
      Robot.forkMotor.run(500.degreesPerSecond)

      while{
        !Robot.forkMotor.readIsStalled()
      } do {
        Time.pause(1.milliseconds)  
      }
      Robot.forkMotor.hold()

  /**
   * Runs the fork in until it stalls.
   */ 
  object ForkIn extends TtyMenuAction:   
    def act(menu: TtyMenu):Unit =
      Robot.forkMotor.run(-500.degreesPerSecond)

      while{
        !Robot.forkMotor.readIsStalled()
      } do {
        Time.pause(1.milliseconds)  
      }
      Robot.forkMotor.brake()


