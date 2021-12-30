Ship Sortie

Start with: 3 grey containers in home, 1 in cargo plane, 3 in sorting center, 1 (chest) on mat
End with: 2 grey containers and the chest in home, 2 grey containers on the ship, 3 in the sorting center

The arm is extra-specialized 

* Reaches over the top to have a specialised left and right side
* Main design driver is opening the cargo plane and sweeping the container therein - built on the left side

* Bracket near back of robot for the truck to attach to the second truck on right side
* Bar (maybe same bracket) to attach the truck to the bridge
** And push the crane
** And push the little truck
** And push the little plane
** And tickle the helicopter

Robot set up

* Home from the chest north to the other helicopter must be clear for the robot
* Run Load Ship Sortie to extend tines slightly
* Load ship brace with two crates and attach to the robot
* Attach cargo plane arm, vertical position, to power-take-off
* Use the -45 jig to aim right color sensor at the helicopter road
* Run Ship Sortie

Route

* From start to truck-on-brdige
** Leave start at -45
** -45 gyro drive to pick up line with right side color sensor
** Hybred gyro-line follow (right side color sensor) to bend in line - measure from pond spur with left side color sensor
** lower arm to catch truck
** Forward-pivot on right wheel to 0
** Pick up line with right side color sensor
** Hybred gyro-line follow (right side color sensor) to latch truck on bridge
** raise arm to bridge-catching height
** Hybred gyro-line follow to bump bridge (right side color sensor) - measure from parking line (left color sensor)
** Raise arm to vertical - out of way

* Drive to the ship
** Forward pivot on left wheel to -90 to line up on ship
** Cross gap to the ship
*** Maybe use cargo connect spur to Hybred gyro-line follow (right side color sensor) at start
*** Span gap with gyro straight at -90
*** Maybe Pick up ship L line and line follow to ship with right color sensor
** Gyro straight to insert brace until stall detected
** Unload containers onto braced ship deck
** Retract tines
** Gyro straight reverse (maybe line follow) to escape the equipment

* Ship's crane
** (May need to go forwards instead of backwards, depending on arm position I'm assuming forwards)
** Gyro straight reverse at -90 to position for pivot
** Gyro pivot on left wheel to 0
** Lower arm to catch crane
** Gyro drive 0 - slowly - to slide crane and raise cargo off deck (to stall with fixed distance limit)
** Raise arm to escape crane

* Little truck
** Gyro drive reverse 0 to detect bottom of L with left sensor (and start measuring distance travelled)
** Find long edge of L with left sensor (in reverse) 
** Set robot on long edge of L with heading of 0 (keep measuring)
** Figure out where the robot is. Hybrd gyro line follow reverse to western-most long-edge of L
** Gyro straight reverse to distance to lower arm (can be done earlier)
** Lower arm to catch truck
** Gyro straight reverse to slide truck past blue line
** Raise arm to vertical to escape truck 

* Chest
** (Haven't alligned on anything in a while - maybe use the back corner of the little plane)
** (Maybe go ahead and do the little plane now)
** Pivot (forward) on right wheel to 180 (or a little less)
** Find helicopter road with left color sensor via forward swerve
** Hybred gyro-line follow at 180 a short distance
** Swerve to avoid the little plane
** Gyro straight 180 past little plane
** Swerve to line up on the chest
** Gyro straight 180 - measure from black line - to push the chest into home 
*** go the right distance to aquire the cargo plane road at -45

* Unload cargo plane
** Gyro pivot on left wheel to -45
** Gyro drive forwards at -45 to aquire the cargo plane road
** Lower arm to find cargo plane model
** Hybred drive forwards at -45 fixed guessed distance
** Gyro drive forwards at -45 until NW spur of road detected on left sensor - maybe a bit further
** Lower arm to open cargo plane, release the container, and capture it in the armpit
** Gyro drive reverse at -45 to pick up road while lowering arm to keep container
** Hybred drive reverse at -45, guessed distance
** gyro drive reverse at -45 to get robot and container home
** raise arm to release the container in home (and fit the robot in home)

* Claim helicopter air drop pack
** Right wheel pivot to -90
** Arm to right side
** Gyro drive -90 forward until stall (or about the right length)
** Rotate arm to just-past-vertical-on-left-side to smack the cargo

* Recovery
** Tines to positions for the next sortie
**  Set menu to next sortie
** Announce robot recovery to operator