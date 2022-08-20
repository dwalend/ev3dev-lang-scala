To set up for intellij
```./millw mill.bsp.BSP/install```

(Always change the password from the default `maker`)

To build and upload the Ev3LangScala library
```./millw -D ev3Password=maker Ev3LangScala.scpAssembly```

To build and upload the SuperPowered library
```./millw -D ev3Password=maker SuperPowered.scpJar```

To upload the SuperPowered bash file
```./millw -D ev3Password=maker SuperPowered.scpBash```

To shell into the Ev3
```ssh robot@ev3dev.local```

To run in the shell
```brickrun -r -- java -cp Ev3LangScala.jar ev3dev4s.JarRunner SuperPowered.jar superpowered.HelloWorld```