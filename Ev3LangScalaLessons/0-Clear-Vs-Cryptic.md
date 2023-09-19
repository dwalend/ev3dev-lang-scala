Clear vs Cryptic

There's this jarring division in engineering between things that are absolutely clear and things that are remarkably cryptic. For FLL, writing code, and life in general we want everything we do to be clear. Having the code tell a clear story in FLL is remarkably useful; we'll be looking at everything as a group, then sharing our work in a very terse session with judges. The main reason to use Scala instead of Scratch or LabView is to get to this clarity at a scale that fits our needs.

However, in order to get started we have to begin with some very cryptic things: Setting up the operating system on the EV3, connecting to it from a lap top, and setting up a new Scala project in source code control. 

ev3dev OS for the EV3

See https://www.ev3dev.org/docs/getting-started/ for how to install a Linux (Debian Stretch) operating system on a 32GB flash card for the EV3. Do all that

Set up a wifi dongle as well. That'll have to be unplugged for FLL competitions, but is extremely useful while working with the robot. 

Updating the OS

```shell
sudo apt-get update
sudo apt-get upgrade
sudo apt-get full-upgrade
```

Check JVM version

```shell
java --version
```

Create a Copy Of The Starter Project

Use git to Fork the ev3-dev-lang Scala Repository. Change to a directory where you'd like to have this project and type the following commands to create your own copy of the project.

TODO                          

Share That Copy Via Github

Make sure that others on your team can reach that copy.

TODO                                  


For Each Computer That Will Be Using the Robot


Change to keyfile-based encryption

On the computer where you'll be programming, open a shell and run these commands to set up a cryptographic key file. Replace username with a good user name for that person. Adding this file will allow each person to access the robot's command line via secure shell - ssh - and copy files to the robot via secure copy - scp - without using a password.

```shell
ssh-keygen -f ~/.ssh/username_ev3_id_rsa -t rsa -m PEM
chmod -w ~/.ssh/username_ev3_id_rsa
chmod -w ~/.ssh/username_ev3_id_rsa.pub 
ssh-copy-id -i ~/.ssh/username_ev3_id_rsa robot@ev3dev.local.
```

Create a Local Copy of Your Team's Repository


TODO

Intellij

Download and install intellij to edit some code.

On the command line run this command to create an intellij project

> ./millw mill.scalalib.GenIdea/idea

Now open intellij and edit some code in the lesson 0 project to do something more interesting

```scala
//todo
```

Upload and Run the code on the EV3

> ./millw Ev3LangScala.jar
> ./millw Ev3LangScala.scpAssembly

> ./millw Ev3LangScalaLesson.scpJar

todo do this all-in-one, along with the ssh command

ssh into the ev3 and run

> brickrun -r -- java -jar Ev3LangScala.jar Ev3LangScalaLesson.jar Lesson0

todo do away with brickrun