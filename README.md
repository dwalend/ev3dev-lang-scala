Set up Intellij Idea to write Ev3 code

Download and install Intellij Idea Community Edition from https://www.jetbrains.com/idea/download/#section=mac . Be agreeable.

Clone (copy the source code onto this computer) the Ev3LangScala project from github

When you first start Intellij - "Get From VCS" . (Or - if intellij is already installed) Top Menu Bar -> Git -> Clone)
Agree to install XCode if asked. 
Github -> Log into github -> Authorize github -> supply a github username and password -> Authorize Jetbrains
Select ev3-dev-lang-scala -> Clone
(You might get an error - small red dot in bottom right corner. Install Xcode's git, then run around the "Get from VCS" again)
Trust the project.

Install the Scala plugin. 

Open up the SuperPowered/src/superpowered/HelloWorld.scala file. (It will look monochrome.)
You'll be invited to install the Scala plugin (upper right corner). 

Install Scala Plugin -> OK
Restart Intellij to get the Scala plugin going

In the upper right Set Up JDK -> Version 11 , Eclipse Temurin 11.0.16 -> OK

In the upper right Set Up Scala SDK -> Use Library -> Download 2.13.8 -> OK -> OK

Set up for mill in intellij (So that intellij will know how everything hangs togeter)

In intellij's terminal,

```./millw mill.bsp.BSP/install```

In intellij's Terminal (at the bottom of the screen), 

```rm -rf .idea```

File -> Close Project . Quit intellij. Reopen it. 

File -> New -> Project from Existing Sources -> ev3dev-lang-scala
Import project from external model -> BSP -> Create

Test that you can navigate in the source code. Open up SuperPowered/src/superpowered/HelloWorld.scala , then select Ev3KeyPad and touch Command-b . It should open the Ev3KeyPad.scala file.

---

Moving compiled code to the Ev3

(Always change the Ev3 password from the default `maker` when setting up the OS on the Ev3.)

./millw won't be able to use scp until after you've ssh'ed at least once.

Shell into the Ev3
```ssh robot@ev3dev.local```
And enter your password when prompted. 
You are sure you want to continue connecting.

then 

```exit```

Build and upload the Ev3LangScala library (Do this if you change the code library - rarely)
```./millw -D ev3Password=maker Ev3LangScala.scpAssembly```

Build and upload the SuperPowered library (Do this every time you want to see your addtions to the team's code run in the Ev3)
```./millw -D ev3Password=maker SuperPowered.scpJar```

Upload the SuperPowered bash file (Probably once)
```./millw -D ev3Password=maker SuperPowered.scpBash```

---
Running code in the Ev3 ev3dev OS shell

Shell into the Ev3
```ssh robot@ev3dev.local```
And enter your password when prompted. You are sure you want to continue connecting.

Run in the shell
```brickrun -r -- java -cp Ev3LangScala.jar ev3dev4s.JarRunner SuperPowered.jar superpowered.HelloWorld```

(It will blink green for about 45 seconds before honking and starting)

Intellij Idea will let you open up a second shell with the big +, so you can build and copy up code without stopping 
the JarRunner on the Ev3. It will reload your .jar file every time the previous Runnable finishes.

(When you're done running your code exit the shell in the Ev3 with

```exit```)

---
Run without the shell (for competitions)

In the ev3dev OS menu on the Ev3

File Browser -> SuperPowered.bash*

(It will blink green for about 45 seconds before honking and starting)

