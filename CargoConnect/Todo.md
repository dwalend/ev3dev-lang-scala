# Robot

# Robot Parts

## Axial Power Takeoff
### High and back on the robot?
### Standard frame (5 x 7 or 7 x 11 frame pairs?)
### Standard gear options?
### Medium motor or large?

## Front passive attachment socket - todo next
### Add standard-sized passive attachment socket to front of robot

## Wire conduits
### Add wire conduit up from large motors and color sensors, but below power takeoff
### Run sensor wires parallel to brick in cable stays 


# Power-take-off Attachments

## Cargo plane arm
### Opens cargo plane
### Retrieves cargo
### Moves big trucks
### Smacks bridge
### Pushes crane
### Pushes small truck
### Pushes plane
### Bumps helicopter

## Train repair arm
## Wing carrier
## Engine twister

# Other attachments

## Package delivery protein spike 

# Equipment

## Jigs
### 45-degree jig for ship sortie, train sortie, and cargo circle sortie
### 0-degree jig for wing sortie

## Ship's brace
### Widen ship brace top pinch to 13 studs to prevent them from touching the containers on the ship
### Raise the 5x7 supporting frames by one with a bottom skid that extends one stud forward. Top of the 5x7 frame is the new track
### Add rubber band or shock spring to pull back the plunger after pushing containers forward
### Robot coupling

## Train loader
### Build a separate protein spike for the robot to find the center
### Add more of a protein spike one-stud deep to the long arms to better center them
### Replace the long arms and their attachment with something sturdier
### Maybe use blue (instead of beige) pins in the folding arms
### Add top rails, maybe even a front edge, to the folding arms to better hold the containers
### Robot coupling

## Abstractions
### Sortie
### Route
### Route Segment
### Robot state ??

## Routes
### Program ship route
### Define train and sorting center route *
#### Blue east to blue circle
#### Blue center to blue circle
#### Blue east to blue circle
### Define wing route
### Define park route

## Maneuver Library
#### Accel/Decel *
### Hybred sensor line follower *
#### Can this go backward ? *
### Sideslip as a combo of arc drives

## Core library
### Use ant tasks T
### scp from ant task in build.sc T
### ssh start jvm from ant task in build.sc T
### Auto scp and reload - based on a checksum T


Make this not fatal:

Exception in thread "Thread-0" java.util.NoSuchElementException: None.get
at scala.None$.get(Option.scala:627)
at scala.None$.get(Option.scala:626)
at net.walend.cargoconnect.Robot$.gyroscope(Robot.scala:19)
at net.walend.lessons.FeedbackDriveTest$.$anonfun$1(FeedbackDrive.scala:74)
at ev3dev4s.sysfs.UnpluggedException$.safeString(Gadget.scala:68)
at net.walend.lessons.FeedbackDriveTest$.setSensorRows(FeedbackDrive.scala:74)
at net.walend.lessons.FeedbackDriveTest$.$init$$$anonfun$1(FeedbackDrive.scala:80)
at net.walend.lessons.Controller.setLcd(Controller.scala:35)
at net.walend.lessons.Controller.$init$$$anonfun$1(Controller.scala:31)
at scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
at scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
at net.walend.lessons.TtyMenu.drawScreen(TtyMenu.scala:61)
at net.walend.lessons.Controller$UpdateScreen$.run(Controller.scala:45)
at java.base/java.lang.Thread.run(Thread.java:834)
java.util.NoSuchElementException: None.get
at scala.None$.get(Option.scala:627)
at scala.None$.get(Option.scala:626)
at net.walend.cargoconnect.Robot$.gyroscope(Robot.scala:19)
at net.walend.lessons.FeedbackDriveTest$.$anonfun$1(FeedbackDrive.scala:74)
at ev3dev4s.sysfs.UnpluggedException$.safeString(Gadget.scala:68)
at net.walend.lessons.FeedbackDriveTest$.setSensorRows(FeedbackDrive.scala:74)
at net.walend.lessons.FeedbackDriveTest$.$init$$$anonfun$1(FeedbackDrive.scala:80)
at net.walend.lessons.Controller.setLcd(Controller.scala:35)
at net.walend.lessons.Controller.$init$$$anonfun$1(Controller.scala:31)
at scala.runtime.function.JProcedure1.apply(JProcedure1.java:15)
at scala.runtime.function.JProcedure1.apply(JProcedure1.java:10)
at net.walend.lessons.TtyMenu.drawScreen(TtyMenu.scala:61)
at net.walend.lessons.TtyMenu.loop(TtyMenu.scala:22)
at net.walend.lessons.Controller.run(Controller.scala:27)
at net.walend.lessons.FeedbackDriveTest$.run(FeedbackDrive.scala:82)
at ev3dev4s.JarRunner$.main(JarRunner.scala:27)
at ev3dev4s.JarRunner.main(JarRunner.scala)
End JarRunner 
