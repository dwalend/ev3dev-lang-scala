#!/bin/bash
#Command line for getting SuperPowered.jar up and running
java -DlogFile=log.txt -jar Ev3LangScala.jar Ev3LangScalaExample.jar ev3dev4s.examples.FiveXHelloWorld

# In a shell use
# brickrun -r -- java -DlogFile=log.txt -jar Ev3LangScala.jar Ev3LangScalaExample.jar ev3dev4s.examples.FiveXHelloWorld
