# Set up Intellij Idea to write Ev3 code

## Download and install Intellij Idea Community Edition from https://www.jetbrains.com/idea/download/#section=mac

## Clone (copy the source code onto this computer) the Ev3LangScala project from GitHub

When you first start Intellij:

* "Get From VCS" . (Or if intellij is already installed, Top Menu Bar -> Git -> Clone)
* Agree to install XCode if asked.
* GitHub -> Log into GitHub -> Authorize GitHub -> supply a GitHub username and password -> Authorize Jetbrains
* Select ev3-dev-lang-scala -> Clone (You might get an error - small red dot in bottom right corner. Install Xcode's git, then run around the "Get from VCS" again).
* Trust the project.

## Install the Scala plugin.

Open up the `Ev3ScalaExample/src/ev3dev4s/examples/FiveXHelloWorld.scala` file. (It will look monochrome.) You'll be invited to install the Scala plugin (upper right corner).

Install Scala Plugin -> OK
From the popup in the lower right, restart Intellij to get the Scala plugin going. (It will keep offering the plugin until you do).

In the upper right

* Set Up JDK ->
* Version 11, Eclipse Temurin 11.0.16 ->
* OK

In the upper right:

* Set Up Scala SDK ->
* Use Library ->
* Create ->
* Download 2.13.x (i.e. the latest in the version 2 branch -- NOT VERSION 3) ->
* OK (installs files) ->
* OK (activates plugin)

## Have Intellij understand the project structure

Start a terminal window (by launching the Terminal app or clicking the terminal button `[>_]` in the lower left sidebar).

Run this command to set up for mill in intellij (So that intellij will know how everything hangs together):

``` ./millw mill.bsp.BSP/install ```

In intellij's terminal, remove the .idea directory (`rm -rf .idea`)

``` mv .idea /tmp/dot-idea-away ```

Close intellij. Reopen it.

* Click File -> New -> Project from Existing Sources
* navigate to the ev3dev-lang-scala directory (possibly in your home directory under `IdeaProjects`)
* Select "Import project from external model" -> BSP -> Create

Test that you can navigate in the source code:

* Open up `Ev3ScalaExample/src/ev3dev4s/examples/FiveXHelloWorld.scala`
* place the cursor into the word "Sound" inside the `run` method
* touch Command-b

It should open the Ev3KeyPad.scala file.

---

# Moving compiled code to the Ev3

The default network name for the EV3 is `ev3dev.local`; if you have renamed it, say, "firefly" then its network name will be `firefly.local`.

Always change the Ev3 password from the default `maker`. Using [good history option practices](https://tinyurl.com/histignorespace), set environment variables to hold the password and hostname: ` EV3PASS=yourpassword; EV3HOST=yourbrickname.local` (note the space at start of line).

## Upload the MasterPiece bash file (Probably once)

```./millw -D ev3Password="$EV3PASS" -D ev3Hostname="$EV3HOST" Ev3LangScalaExample.DoItToRobot```

## Build and upload the Ev3LangScala library. Do this at least once and following any changes to the code library (anything in the Ev3LangScala directory)

```./millw -D ev3Password="$EV3PASS" -D ev3Hostname="$EV3HOST" Ev3LangScala.AppToRobot```

## Build and upload the MasterPiece library. Do this every time you want to see your addtions to the team's code run in the Ev3 (eg if you're using the examples, anything in the Ev3LangScalaExamples directory).

```./millw -D ev3Password="$EV3PASS" -D ev3Hostname="$EV3HOST" Ev3LangScalaExample.ToRobot```

---

# Running code in the Ev3 ev3dev OS shell

Intellij Idea will let you open up a second terminal with the big +, so you can build and copy up code without stopping the JarRunner on the Ev3.

## Shell into the Ev3

Use ssh to connect:

```ssh robot@$EV3HOST```

Enter your password when prompted, and confirm you are sure you want to continue connecting.

Run in the shell:

```brickrun -r -- java -cp Ev3LangScala.jar ev3dev4s.JarRunner Ev3LangScalaExample.jar ev3dev4s.examples.FiveXHelloWorld```

It will blink green for about 45 seconds before honking twice and starting the program. The example programs include

* FiveXHelloWorld -- how the robot can communicate back to you
* AllGadgetsExample -- reading information from the sensors and motors
* MotorExample -- drives the motors
* KitchenSink (in development) -- Does all of those

When you're done,

```exit```

---

# Run without the shell (for competitions)

In the ev3dev OS menu on the Ev3

File Browser -> MasterPiece.bash*

(It will blink green for about 45 seconds before honking and starting)
