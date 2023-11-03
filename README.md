# Set up Intellij Idea to write Ev3 code

## Download and install Intellij Idea Community Edition from https://www.jetbrains.com/idea/download/#section=mac 

## Clone (copy the source code onto this computer) the Ev3LangScala project from github

When you first start Intellij - "Get From VCS" . (Or - if intellij is already installed) Top Menu Bar -> Git -> Clone)
Agree to install XCode if asked. 
Github -> Log into github -> Authorize github -> supply a github username and password -> Authorize Jetbrains
Select ev3-dev-lang-scala -> Clone
(You might get an error - small red dot in bottom right corner. Install Xcode's git, then run around the "Get from VCS" again)
Trust the project.

## Install the Scala plugin. 

Open up the Ev3ScalaExample/src/ev3dev4s/HelloWorld.scala file. (It will look monochrome.)
You'll be invited to install the Scala plugin (upper right corner). 

Install Scala Plugin -> OK
Restart Intellij to get the Scala plugin going

In the upper right Set Up JDK -> Version 11 , Eclipse Temurin 11.0.16 -> OK

In the upper right Set Up Scala SDK -> Use Library -> Download 2.13.8 -> OK -> OK
       
## Have Intellij understand the project structure

Set up for mill in intellij (So that intellij will know how everything hangs togeter)
```./millw mill.bsp.BSP/install```

In intellij's terminal, 

```rm -rf .idea```

Close intellij. Reopen it. 

File -> New -> Project from Existing Sources -> ev3dev-lang-scala
Import project from external model -> BSP -> Create

Test that you can navigate in the source code. Open up Ev3ScalaExample/src/ev3dev4s/HelloWorld.scala , then select Ev3KeyPad and touch Command-b . It should open the Ev3KeyPad.scala file.

---

# Moving compiled code to the Ev3

(Always change the Ev3 password from the default `maker`)

## Build and upload the Ev3LangScala library (Do this if you change the code library - first and rarely)

```./millw -D ev3Password=maker Ev3LangScala.AppToRobot```

## Build and upload the MasterPiece library (Do this every time you want to see your addtions to the team's code run in the Ev3)

```./millw -D ev3Password=maker Ev3LangScalaExample.ToRobot```

## Upload the MasterPiece bash file (Probably once)

```./millw -D ev3Password=maker Ev3LangScalaExample.DoItToRobot```

---

# Running code in the Ev3 ev3dev OS shell

Intellij Idea will let you open up a second terminal with the big +, so you can build and copy up code without stopping the JarRunner on the Ev3.

## Shell into the Ev3

```ssh robot@ev3dev.local```
And enter your password when prompted. (You are sure you want to continue connecting.)

Run in the shell
```brickrun -r -- java -cp Ev3LangScala.jar ev3dev4s.JarRunner Ev3LangScalaExample.jar examples.HelloWorld```

(It will blink green for about 45 seconds before honking and starting)

When you're done, 

```exit```

---
# Run without the shell (for competitions)

In the ev3dev OS menu on the Ev3

File Browser -> MasterPiece.bash*

(It will blink green for about 45 seconds before honking and starting)
