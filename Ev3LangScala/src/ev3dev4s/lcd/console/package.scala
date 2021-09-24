package ev3dev4s.lcd

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
package object console {

}

//todo test "hello" with brickrun (no -r) to /dev/tty

//todo test out setfont to Uni3-TerminusBold32x16 hello

//todo create an NxM character grid (4 X 11 for Lat15-TerminusBold32x16 and Uni3-TerminusBold32x16)
//todo and write that to /dev/tty

//todo does using the clear command make sense, or just let things scroll off the top? Does anything build up in the terminal buffer?

//todo is there a better way to call setfont? Java console? Java library? JNA? 

//todo is there any advantage to using NativeTty ?
