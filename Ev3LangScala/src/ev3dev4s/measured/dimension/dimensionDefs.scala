package ev3dev4s.measured.dimension

import ev3dev4s.measured.exponents.{_0, _1, _2, _3}
import Dimensions.Dim

/**
 * Trivial dimension to represent dimensionless quantities
 */
type Uno = Dim[_0, _0, _0, _0, _0, _0]

// Base dimensions
type Length            = Dim[_1, _0, _0, _0, _0, _0]
type Time              = Dim[_0, _1, _0, _0, _0, _0]
type Temperature       = Dim[_0, _0, _1, _0, _0, _0]
type Mass              = Dim[_0, _0, _0, _1, _0, _0]
type ElectricCharge    = Dim[_0, _0, _0, _0, _1, _0]
type Angle             = Dim[_0, _0, _0, _0, _0, _1]

// Derived dimensions
type Acceleration = Velocity / Time
type Area = Length ^ _2
type Density = Mass / Volume
type Diffusivity = Area / Time
type ElectricCurrent = ElectricCharge / Time
type ElectricPotential = Energy / ElectricCharge
type ElectricResistance = ElectricPotential / ElectricCurrent
type Energy = Force * Length
type Force = Mass * Acceleration
type Frequency = Uno / Time
type Momentum = Mass * Velocity
type Power = Energy / Time
type Pressure = Force / Area
type Velocity = Length / Time
type Viscosity = Pressure * Time
type Volume = Length ^ _3
