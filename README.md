# Set up Intellij Idea to write Ev3 code

## Download and install Intellij Idea Community Edition from https://www.jetbrains.com/idea/download/#section=mac 
            
## Install bleep from https://bleep.build/docs/installing/

## Fork this repository in github

This project is at the "it works on our robots and laptops" stage. You'll very likely want to work in your own fork.

We would love to have pull requests, especially where the Ev3LangScala is incomplete. Please be aware that kids use this library for FLL competitions; do not propose code beyond what would be analogous to what Lego offers as a starting point to Ev3LangScala.

## Clone (copy the source code onto this computer) the Ev3LangScala project from github

When you first start Intellij - "Get From VCS" . (Or - if intellij is already installed) Top Menu Bar -> Git -> Clone)
Agree to install XCode if asked. 
Github -> Log into github -> Authorize github -> supply a github username and password -> Authorize Jetbrains
Select ev3-dev-lang-scala -> Clone
(You might get an error - small red dot in bottom right corner. Install Xcode's git, then run around the "Get from VCS" again)
Trust the project.

## Install the Scala plugin in Intellij 

Open up the Ev3ScalaExample/src/ev3dev4s/HelloWorld.scala file. (It will look monochrome.)
You'll be invited to install the Scala plugin (upper right corner). 

Install Scala Plugin -> OK
Restart Intellij to get the Scala plugin going

In the upper right Set Up JDK -> Version 11 , Eclipse Temurin 11.0.16 -> OK

In the upper right Set Up Scala SDK -> Use Library -> Download 2.13.8 -> OK -> OK
         
## Install bleep

bleep is the command line tool that will compile your Scala code and copy it to your robot. To get it you first have to install another tool called Coursier. Install it via Intellij's terminal. 

On a modern Mac use 

```
curl -fL https://github.com/VirtusLab/coursier-m1/releases/latest/download/cs-aarch64-apple-darwin.gz | gzip -d > cs
chmod +x cs
./cs setup
```
See https://get-coursier.io/docs/cli-installation for other operating systems

Next install bleep:

```cs install --channel https://raw.githubusercontent.com/oyvindberg/bleep/master/coursier-channel.json bleep```

## Have Intellij understand the project structure

Use bleep to set up the ide (So that intellij will know how everything hangs togeter)

In intellij's terminal,

```shell
bleep setup-ide
```
. Download and compile all the code

```shell
bleep compile
```

Remove whatever intellij created on its own. 

```rm -rf .idea```

Close Intellij. Reopen it. 

File -> New -> Project from Existing Sources -> ev3dev-lang-scala
Import project from external model -> BSP -> Create

Test that you can navigate in the source code. Open up Ev3ScalaExample/src/ev3dev4s/HelloWorld.scala , then select Ev3KeyPad and touch Command-b . It should open the Ev3KeyPad.scala file.

---

# Moving compiled code to the Ev3

(The command line arguments `-- -J-Dev3Username=robot -J-Dev3Password=maker -J-Dev3Hostname=ev3dev.local` are optional if all the values are default, but you really should change at least the password.)

## Build and upload the Ev3LangScala library (Do this if you change the ev3dev4s library - first and rarely)

```bleep appToRobot Ev3LangScala -- -J-Dev3Username=robot -J-Dev3Password=maker -J-Dev3Hostname=ev3dev.local```

## Build and upload the Ev3LangScalaExample library (Do this every time you want to see your changes to the team's code run in the Ev3)

```bleep toRobot Ev3LangScalaExample -- -J-Dev3Username=robot -J-Dev3Password=maker -J-Dev3Hostname=ev3dev.local```

## Upload the Ev3LangScalaExample bash file (Do this if you change the DoIt.bash script - rarely)

```bleep doItToRobot Ev3LangScalaExample -- -J-Dev3Username=robot -J-Dev3Password=maker -J-Dev3Hostname=ev3dev.local```

---

# Running code in the Ev3 ev3dev OS shell

Intellij Idea will let you open up a second terminal with the big +, so you can build and copy up code without stopping the JarRunner on the Ev3.

## Shell into the Ev3

```ssh robot@ev3dev.local```
And enter your password when prompted. (You are sure you want to continue connecting.)

To run in the shell use
```brickrun -r -- java -cp Ev3LangScala.jar ev3dev4s.JarRunner Ev3LangScalaExample.jar examples.HelloWorld```

(It will blink green for about 45 seconds before honking and starting)

When you're done, 

```exit```

---
# Run without the shell (for competitions)

In the ev3dev OS menu on the Ev3

File Browser -> DoIt.bash*

(It will blink green for about 45 seconds before honking and starting)

# What is Here

Ev3langScalaExample holds examples. For a particular project, start by copying this project, renaming it, and removing everything but a reasonable starting point.

Ev3LangScala holds a core library. Of earliest interest is the ev3dev4s.lego package, which holds API analogous to the Blocky/Scratch library you can download from Lego. The actuators, sensors, lcd.tty, and os packages provide a lower-level programmer's API. The sysfs is where most of the inner workings and interface to the ev3dev OS reside.

Ev3LangScalaExperimental holds my work-in-progress plus abandoned experiments.

Ev3LangScalaLessons is a work-in-progress guide for kids to use this library.

