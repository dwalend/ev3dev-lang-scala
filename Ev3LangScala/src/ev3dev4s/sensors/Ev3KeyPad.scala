package ev3dev4s.sensors

import java.io.{DataInputStream, FileInputStream}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Ev3KeyPad extends AutoCloseable:

  enum Key(val byte: Byte):
    case Up extends Key(0x67)
    case Down extends Key(0x6c)
    case Left extends Key(0x69)
    case Right extends Key(0x6a)
    case Enter extends Key(0x1c)
    case Escape extends Key(0x0e)

  enum State(val byte: Byte):
    case Pressed extends State(0x01)
    case Released extends State(0x00)

  val bytesToKeyStates: Map[(Byte, Byte), (Key,State)] =
    val keyStates = for key <- Key.values
                         state <- State.values yield (key, state)
    keyStates.map(keyState => (keyState._1.byte, keyState._2.byte) -> keyState).toMap

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

  //todo - some day - there is no way to interrupt keyPadInputStream.readFully() - maybe there's a way to inject some fake input
  override def close(): Unit = this.synchronized{
    keyPadInputStream.close()
  }

