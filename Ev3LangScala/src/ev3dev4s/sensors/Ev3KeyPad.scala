package ev3dev4s.sensors

import java.io.{DataInputStream, FileInputStream}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Ev3KeyPad extends AutoCloseable {

  //todo use enum with Scala3
  sealed case class Key(byte: Byte, name: String)

  val UP: Key = Key(0x67, "Up")
  val DOWN: Key = Key(0x6c, "Down")
  val LEFT: Key = Key(0x69, "Left")
  val RIGHT: Key = Key(0x6a, "Right")
  val ENTER: Key = Key(0x1c, "Enter")
  val ESCAPE: Key = Key(0x0e, "Escape")
  val keys = Seq(UP, DOWN, LEFT, RIGHT, ENTER, ESCAPE)

  //todo use enum with Scala3
  sealed case class State(byte: Byte, name: String)

  val PRESSED: State = State(0x01, "Pressed")
  val RELEASED: State = State(0x00, "Released")
  val states = Seq(PRESSED, RELEASED)

  val bytesToKeyStates: Map[(Byte, Byte), (Key,State)] = {
    val keyStates = for {key <- keys
                         state <- states} yield (key, state)
    keyStates.map(keyState => (keyState._1.byte, keyState._2.byte) -> keyState).toMap
  }

  private val keyPadEventPath = "/dev/input/by-path/platform-gpio_keys-event"
  //need a DataInputStream to use readFully
  private val keyPadInputStream = new DataInputStream(new FileInputStream(keyPadEventPath))
  private val bytes32:Array[Byte] = Array.fill[Byte](32)(0x0)

  def blockUntilAnyKey():(Key,State) = this.synchronized{
    val KEY_INDEX = 10 // should be a key.byte
    val STATE_INDEX = 12 // should be a state.byte

    keyPadInputStream.readFully(bytes32)
    val keyAndState: (Byte, Byte) = (bytes32(KEY_INDEX),bytes32(STATE_INDEX))
    bytesToKeyStates.getOrElse(keyAndState,throw new IllegalStateException(s"No key state for $keyAndState"))
  }

  override def close(): Unit = this.synchronized{
    keyPadInputStream.close()
  }
}

