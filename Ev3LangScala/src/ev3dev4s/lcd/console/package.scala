package ev3dev4s.lcd

/**
 * Use  brickrun -r -- to get the console LCD screen and still have stdout go to /dev/tty0 - your terminal
 *
 * brickrun -r -- java -Xmx3M -Xms3M -cp Ev3LangScala.jar ev3dev4s.lcd.console.Hello
 *
 * @author David Walend
 * @since v0.0.0
 */
package object console {

}


//todo create an NxM character grid (4 X 11 for Lat15-TerminusBold32x16 and Uni3-TerminusBold32x16)
//todo and write that to /dev/tty

//todo does using the clear command make sense, or just let things scroll off the top? Does anything build up in the terminal buffer?

//todo is there a better way to call setfont? Java console? Java library? JNA? 

//todo is there any advantage to using NativeTty ?
