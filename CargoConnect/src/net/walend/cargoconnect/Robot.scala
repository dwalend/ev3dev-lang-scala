package net.walend.cargoconnect

import ev3dev4s.Ev3System
import ev3dev4s.sensors.Ev3Gyroscope
import ev3dev4s.sensors.Ev3ColorSensor
import ev3dev4s.sensors.SensorPort
import ev3dev4s.actuators.{Ev3MediumMotor,MotorPort}

object Robot: 
  val gyroscope:Ev3Gyroscope = Ev3System.portsToSensors.values.collectFirst{case g:Ev3Gyroscope => g}.get
  val leftColorSensor:Ev3ColorSensor = Ev3System.portsToSensors.get(SensorPort.One).collect{case cs:Ev3ColorSensor => cs}.get
  val rightColorSensor:Ev3ColorSensor = Ev3System.portsToSensors.get(SensorPort.Four).collect{case cs:Ev3ColorSensor => cs}.get

  val forkMotor:Ev3MediumMotor = Ev3System.portsToMotors.get(MotorPort.C).collect{case m:Ev3MediumMotor => m}.get