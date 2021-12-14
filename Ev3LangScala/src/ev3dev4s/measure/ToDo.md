I've figured out the beginnings of solutions with both value classes and opaque types, but I'm stuck on what to do for multiplication and division. I have a lot of dots, but don't see quite how to connect them. I think I need to do some type level programming to generate something the compiler can verify. After that, I'm beholden to scalac to remove everything but the underlying Float or Int after all the compile-time checks.

Measurable things are the low-level concept that can be measured: Distance, Angle, Time, and compounds of those like Speed and AngularVelocity, and versions with no great name like Distance/Angle. I don't think need to represent anything this fundamental, so I'll likey leave it out in the first pass.

UnitOfMeasure[M <: Measurable] s are the measurement of a thing that can be measured: Meters, Degrees, Seconds, and compounds of those like MetersPerSecond, DegreesPerSecond, MetersPerDegree. And I'll need a Unitless here as well. (I'll likely work with millimeters and milliseconds - at the scale I care about - as Int.)

Measurement[U <: UnitOfMeasure] is the actual quantity in the right units. It carries the Int with the right number of degrees, milliseconds, millimeters per second, or what have you. Inside Measurement I can define all the operations. That saves a combitorial explosion rather neatly. Comparison operators that return a Boolean, like <,>,!=, are straight-forward. I found a cheezy hack around contravariance for addition, subtraction, min, and max (where the result is the same type as both parameters).

But I see trouble with multiplication and division even at the planning stage. 

I think I need something like a QuotientUnitOfMeasure[NumeratorUnits <: UnitOfMeasure, DenomicatorUnits <: UnitOfMeasure] extends UnitOfMeasure . I also need a way to reduce the resulting QuotientUnitOfMeasure to remove those that appear in both the numerator and denominator, and a way to recognize that "Meters Degrees Per Second" is the same as "Degrees Meters Per Second" .

 I have this undercurrent concern of scalac clearing it all out and using Ints after proving the types are right, but I think that's all possible. I found this article describing how to do type-level quicksort, which is more than I need: https://blog.rockthejvm.com/type-level-programming-scala-3/ .

However, I'm out of my depth. What do I go read to build up from generic highher-kinds to type-level programming?