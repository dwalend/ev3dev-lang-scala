package ev3dev4s.sensors

import ev3dev4s.os.Time

import java.io.{DataInputStream, FileInputStream}
import scala.annotation.tailrec

/**
 *
 *
 * @author David Walend
 * @since v0.0.0
 */
object Ev3KeyPad extends AutoCloseable {

  sealed case class Key(byte: Byte, name:String)

  object Key{
    val Up: Key = Key(0x67,"Up")
    val Down: Key = Key(0x6c,"Down")
    val Left: Key = Key(0x69,"Left")
    val Right: Key = Key(0x6a,"Right")
    val Enter: Key = Key(0x1c,"Enter")
    val Escape: Key = Key(0x0e,"Escape")

    val values: Array[Key] = Array(Up,Down,Left,Right,Enter,Escape)
  }

  sealed case class State(byte: Byte, name:String)

  object State {
    val Pressed: State = State(0x01,"Pressed")
    val Released: State = State(0x00,"Released")

    val values: Array[State] = Array(Pressed,Released)
  }

  val bytesToKeyStates: Map[(Byte, Byte), (Key,State)] = {
    val keyStates: Array[(Key, State)] = for {key <- Key.values
                                              state <- State.values} yield (key, state)
    keyStates.map(keyState => (keyState._1.byte, keyState._2.byte) -> keyState).toMap
  }
  private val keyPadEventPath = "/dev/input/by-path/platform-gpio_keys-event"
  //need a DataInputStream to use readFully
  private val keyPadInputStream = new DataInputStream(new FileInputStream(keyPadEventPath))
  private val bytes32: Array[Byte] = Array.fill[Byte](32)(0x0)

  @tailrec
  def blockUntilAnyKey(startTime:Long = Time.now()): (Key, State) = this.synchronized {
    val KEY_INDEX = 10 // should be a key.byte
    val STATE_INDEX = 12 // should be a state.byte

    keyPadInputStream.readFully(bytes32)
    //debounce the keypad
    if(Time.now() - startTime > 5) {// long enough to clear the old pushes
      val keyAndState: (Byte, Byte) = (bytes32(KEY_INDEX), bytes32(STATE_INDEX))
      bytesToKeyStates.getOrElse(keyAndState, throw new IllegalStateException(s"No key state for $keyAndState"))
    } else {
      blockUntilAnyKey(startTime)
    }
  }

  //todo - some day - there is no way to interrupt keyPadInputStream.readFully() - maybe there's a way to inject some fake input
  override def close(): Unit = this.synchronized {
    keyPadInputStream.close()
  }
}