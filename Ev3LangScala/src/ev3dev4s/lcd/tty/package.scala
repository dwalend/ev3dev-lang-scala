package ev3dev4s.lcd

/**
 * Use  brickrun -r -- to get the tty LCD screen and still have stdout go to /dev/tty0 - your terminal
 *
 * brickrun -r -- java -Xmx3M -Xms3M -cp Ev3LangScala.jar ev3dev4s.lcd.tty.Hello
 *
 * @author David Walend
 * @since v0.0.0
 */
package object tty {

}

//todo is there a better way to call setfont? Java console? Java library? JNA?

//todo is there any advantage to using NativeTty ?
