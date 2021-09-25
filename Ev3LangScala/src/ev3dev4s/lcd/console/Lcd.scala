package ev3dev4s.lcd.console

import ev3dev4s.sysfs.Shell

import java.io.{FileOutputStream, PrintStream}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Lcd extends Runnable with AutoCloseable {

  private lazy val printStream: PrintStream = {
    Shell.execute("setfont Uni3-TerminusBold32x16")
    new PrintStream(new FileOutputStream("/dev/tty"))
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
    //noinspection MakeArrayToString
    rows.foreach(i => printStream.print(characters(i)) )
    printStream.flush()
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

  def set(string:String,row:Int,justification: Justification = LEFT):Unit = {
    val chars: Array[Char] = string.toCharArray
    val start = justification.start(chars.length)
    chars.copyToArray(characters(row),start)
  }

  def main(args: Array[String]): Unit = {
    run()
  }

  override def run(): Unit = {
    clear()
    set(0,0,'B')
    set(1,1,'l')
    set(2,2,'ä')
    set(3,3,'r')
    set(2,4,'t')
    set(1,5,'y')
    set(0,6,'b')
    set(1,7,'l')
    set(2,8,'a')
    set(3,9,'r')
    set(2,10,'t')
    flush()
    Thread.sleep(5000)

    clear()
    set("Blart?",0,LEFT)   //0
    set("Blårt!",1,CENTER) //3
    set("Blärt?",2,RIGHT)  //5
    set("Blærty!",3,CENTER)//2
    flush()

    Thread.sleep(5000)
  }
}
