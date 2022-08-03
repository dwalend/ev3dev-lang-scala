package ev3dev4s.sensors

import java.io.{DataInputStream, FileInputStream}

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Ev3KeyPad extends AutoCloseable {

  sealed case class Key(byte: Byte)

  object Key{
    val Up: Key = Key(0x67)
    val Down: Key = Key(0x6c)
    val Left: Key = Key(0x69)
    val Right: Key = Key(0x6a)
    val Enter: Key = Key(0x1c)
    val Escape: Key = Key(0x0e)

    val values: Array[Key] = Array(Up,Down,Left,Right,Enter,Escape)
  }

  sealed case class State(byte: Byte)

  object State {
    val Pressed: State = State(0x01)
    val Released: State = State(0x00)

    val values: Array[State] = Array(Pressed,Released)
  }

  val bytesToKeyStates: Map[(Byte, Byte), (Key,State)] = {
    val keyStates = for {key <- Key.values
                         state <- State.values} yield (key, state)
    keyStates.map(keyState => (keyState._1.byte, keyState._2.byte) -> keyState).toMap
  }
  private val keyPadEventPath = "/dev/input/by-path/platform-gpio_keys-event"
  //need a DataInputStream to use readFully
  private val keyPadInputStream = new DataInputStream(new FileInputStream(keyPadEventPath))
  private val bytes32: Array[Byte] = Array.fill[Byte](32)(0x0)

  def blockUntilAnyKey(): (Key, State) = this.synchronized {
    val KEY_INDEX = 10 // should be a key.byte
    val STATE_INDEX = 12 // should be a state.byte

    keyPadInputStream.readFully(bytes32)
    val keyAndState: (Byte, Byte) = (bytes32(KEY_INDEX), bytes32(STATE_INDEX))
    bytesToKeyStates.getOrElse(keyAndState, throw new IllegalStateException(s"No key state for $keyAndState"))
  }

  //todo - some day - there is no way to interrupt keyPadInputStream.readFully() - maybe there's a way to inject some fake input
  override def close(): Unit = this.synchronized {
    keyPadInputStream.close()
  }
}

