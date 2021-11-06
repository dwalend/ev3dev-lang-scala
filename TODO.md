To Test:


In reportProgress:
      
gyro calibratiuon


ant scp to copy up the .jar
ant ssh to run the .jar - need to get a signal into the jvm from the command prompt

        
To Do:
              
File polling for a reload - how can you know when a file is complete via scp? (Maybe send the file size as meta info first??)

Investigate jlink and jdeps for a smaller .jar file - faster start? - use ant tasks? (and the build file as an analogy for programming the robot)
                  
Units as types - how to make Millimeters / Seconds into MillimetersPerSecond as Value Classes

README.md

Lessons

Is there another option? C interface maybe? https://github.com/theZiz/ev3c ( http://ziz.gp2x.de/ev3c_documentation/files/include/ev3c_lcd-h.html ) looks promising
                        
Or immitate theZiz' work, do it as almost-pure-scala. (Will still need NativeFile for some of that.)

Fill in existing classes

IR sensor

Ultrasonic sensor

Direct Sound

tut examples

ant task for mill yguard or proguard to shrink the code down

Faster start-up

Remove dependency on brickman

Faster start-up of the OS
  
Scala-friendly JNA (SNA?)

Lesson plan

mill and mdoc and examples

Set up
