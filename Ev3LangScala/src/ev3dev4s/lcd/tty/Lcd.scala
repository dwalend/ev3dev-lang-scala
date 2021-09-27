package ev3dev4s.lcd.tty

import ev3dev4s.sysfs.Shell

import java.io.{BufferedOutputStream, FileOutputStream, PrintStream}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Lcd extends AutoCloseable {

  private lazy val printStream: PrintStream = {
    Shell.execute("setfont Uni3-TerminusBold32x16")
    new PrintStream(
      new BufferedOutputStream(
        new FileOutputStream("/dev/tty"),64
      )
    )
  }

  /**
   * 4 Rows of 11 Characters
   */
  val maxRow = 3
  val maxColumn=10
  private lazy val characters: Array[Array[Char]] = Array.fill(maxRow+1,maxColumn+1)(' ')
  val rows: Seq[Int] = 0.to(maxRow)

  private lazy val clearRow:Array[Char] = Array.fill(maxColumn+1)(' ')

  def flush():Unit = {
    printStream.print("\n")
    //noinspection MakeArrayToString
    rows.foreach(i => printStream.print(characters(i)) )
    printStream.flush()

    rows.foreach(i => {print(new String(characters(i)));println("")} )

  }

  def clear():Unit ={

    rows.foreach(i => clearRow.copyToArray(characters(i)))
    characters
  }

  override def close(): Unit = {
    printStream.close()
  }

  sealed abstract class Justification{
    def start(length:Int):Int
  }

  val LEFT: Justification = new Justification {
    override def start(length: Int): Int = 0
  }
  val RIGHT: Justification = new Justification {
    override def start(length: Int): Int = Math.max(maxColumn + 1 - length,0)
  }
  val CENTER: Justification = new Justification {
    override def start(length: Int): Int = Math.max((maxColumn/2) + 1 - ((length+1)/2),0)
    // 11:0 10:1 9:1 8:2 7:2 6:3 5:3 4:4 3:4 2:5 1:5
  }

  def set(row:Int,column:Int,char: Char):Unit = {
    characters(row)(column) = char
  }

  def set(row:Int,string:String,justification: Justification = LEFT):Unit = {
    val chars: Array[Char] = string.toCharArray
    val start = justification.start(chars.length)
    chars.copyToArray(characters(row),start)
  }

}
