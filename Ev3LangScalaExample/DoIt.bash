#!/bin/bash
#Command line for getting SuperPowered.jar up and running
java -DlogFile=log.txt -cp Ev3LangScala.jar ev3dev4s.JarRunner Ev3LangScalaExample.jar examples.FiveXHelloWorld

# In a shell use
# brickrun -r --java -DlogFile=log.txt -cp Ev3LangScala.jar ev3dev4s.JarRunner Ev3LangScalaExample.jar examples.FiveXHelloWorld
