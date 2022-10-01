package superpowered

import ev3dev4s.actuators.MotorPort
import ev3dev4s.lego.Movement

object Robot {
  Movement.setMovementMotorsTo(MotorPort.A,MotorPort.C)

}
