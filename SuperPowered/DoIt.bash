#!/bin/bash
#Command line for getting SuperPowered.jar up and running
#java -DlogFile=log.txt -cp Ev3LangScala.jar ev3dev4s.JarRunner SuperPowered.jar superpowered.Menu
java -DlogFile=log.txt -cp Ev3LangScala.jar ev3dev4s.JarRunner SuperPowered.jar superpowered.FiveXHelloWorld

# In a shell use
# brickrun -r -- java -cp Ev3LangScala.jar ev3dev4s.JarRunner SuperPowered.jar superpowered.Menu
# brickrun -r --java -DlogFile=log.txt -cp Ev3LangScala.jar ev3dev4s.JarRunner SuperPowered.jar superpowered.FiveXHelloWorld