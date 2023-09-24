#!/bin/bash
#Command line for getting SuperPowered.jar up and running
#java -DlogFile=log.txt -cp Ev3LangScala.jar ev3dev4s.JarRunner MasterPiece.jar masterpiece.Menu
java -DlogFile=log.txt -cp Ev3LangScala.jar ev3dev4s.JarRunner MasterPiece.jar masterpiece.FiveXHelloWorld

# In a shell use
# brickrun -r -- java -cp Ev3LangScala.jar ev3dev4s.JarRunner MasterPiece.jar masterpiece.Menu
# brickrun -r --java -DlogFile=log.txt -cp Ev3LangScala.jar ev3dev4s.JarRunner MasterPiece.jar masterpiece.FiveXHelloWorld
