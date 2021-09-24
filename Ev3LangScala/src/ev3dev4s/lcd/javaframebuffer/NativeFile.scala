package ev3dev4s.lcd.javaframebuffer

import com.sun.jna.LastErrorException
import com.sun.jna.NativeLong
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import java.nio.ByteBuffer

/**
 * <p>This class provides access to Linux files using native I/O operations. It is
 * implemented using the JNA package. The class is required because certain
 * operations (like ioctl) that are required by the Lego kernel module interface are
 * not support by standard Java methods. In addition standard Java memory mapped
 * files do not seem to function correctly when used with Linux character devices.</p>
 *
 * <p>Only JNA is used, the original interface used combination of Java and JNA.</p>
 *
 * @param fname the name of the file to open
 * @param flags Linux style file access flags
 * @param mode  Linux style file access mode

 * @author andy, Jakub Vaněk
 */
final class NativeFile(fname: String, flags: Int, mode: Int = NativeConstants.DEFAULT_PRIVS)
  extends AutoCloseable {
  protected val fd: Int = NativeLibc.open(fname, flags, mode)

  /**
   * Check whether this file has been open()en.
   *
   * @return True when the file descriptor is valid.
   */
  def isOpen: Boolean = fd != -1

  /**
   * Attempt to read the requested number of bytes from the associated file.
   *
   * @param buf location to store the read bytes
   * @param len number of bytes to attempt to read
   * @return number of bytes read
   * @throws LastErrorException when operations fails
   */
  @throws[LastErrorException]
  def read(buf: Array[Byte], len: Int): Int =
    NativeLibc.read(fd, ByteBuffer.wrap(buf), len)

  /**
   * Attempt to write the requested number of bytes to the associated file.
   *
   * @param buf    location to store the read bytes
   * @param offset the offset within buf to take data from for the write
   * @param len    number of bytes to attempt to read
   * @return number of bytes read
   * @throws LastErrorException when operations fails
   */
  @throws[LastErrorException]
  def write(buf: Array[Byte], offset: Int, len: Int): Int =
    NativeLibc.write(fd, ByteBuffer.wrap(buf, offset, len), len)

  /**
   * Attempt to read the requested number of byte from the associated file.
   *
   * @param buf    location to store the read bytes
   * @param offset offset with buf to start storing the read bytes
   * @param len    number of bytes to attempt to read
   * @return number of bytes read
   * @throws LastErrorException when operations fails
   */
  @throws[LastErrorException]
  def read(buf: Array[Byte], offset: Int, len: Int): Int =
    NativeLibc.read(fd, ByteBuffer.wrap(buf, offset, len), len)

  /**
   * Attempt to write the requested number of bytes to the associated file.
   *
   * @param buf location to store the read bytes
   * @param len number of bytes to attempt to read
   * @return number of bytes read
   * @throws LastErrorException when operations fails
   */
  @throws[LastErrorException]
  def write(buf: Array[Byte], len: Int): Int =
    NativeLibc.write(fd, ByteBuffer.wrap(buf), len)

  /**
   * Perform a Linux style ioctl operation on the associated file.
   *
   * @param req  ioctl operation to be performed
   * @param info output as integer
   * @return Linux style ioctl return
   * @throws LastErrorException when operations fails
   */
  @throws[LastErrorException]
  def ioctl(req: Int, info: IntByReference): Int =
    NativeLibc.ioctl(fd, req, info.getPointer)

  /**
   * Perform a Linux style ioctl operation on the associated file.
   *
   * @param req  ioctl operation to be performed
   * @param info input as integer
   * @return Linux style ioctl return
   * @throws LastErrorException when operations fails
   */
  @throws[LastErrorException]
  def ioctl(req: Int, info: Int): Int =
    NativeLibc.ioctl(fd, req, info)

  /**
   * Perform a Linux style ioctl operation on the associated file.
   *
   * @param req ioctl operation to be performed
   * @param buf pointer to ioctl parameters
   * @return Linux style ioctl return
   * @throws LastErrorException when operations fails
   */
  @throws[LastErrorException]
  def ioctl(req: Int, buf: Pointer): Int =
    NativeLibc.ioctl(fd, req, buf)

  /**
   * Close the associated file
   *
   * @throws LastErrorException when operations fails
   */
  @throws[LastErrorException]
  override def close(): Unit =
    NativeLibc.close(fd)

  /**
   * Map a portion of the associated file into memory and return a pointer
   * that can be used to access that memory.
   *
   * @param len   size of the region to map
   * @param prot  protection for the memory region
   * @param flags Linux mmap flags
   * @param off   offset within the file for the start of the region
   * @return a pointer that can be used to access the mapped data
   * @throws LastErrorException when operations fails
   */
  @throws[LastErrorException]
  def mmap(len: Long, prot: Int, flags: Int, off: Long): Pointer = {
    val p = NativeLibc.mmap(new Pointer(0), new NativeLong(len), prot, flags, fd, new NativeLong(off))
    if (p == new Pointer(-1)) throw new LastErrorException("mmap() failed")
    p
  }

  /**
   * Unmap mapped memory region.
   *
   * @param addr Mapped address.
   * @param len  Region length.
   * @throws LastErrorException when operations fails
   */
  @throws[LastErrorException]
  def munmap(addr: Pointer, len: Long): Int =
    NativeLibc.munmap(addr, new NativeLong(len))

  /**
   * Synchronize mapped memory region.
   *
   * @param addr  Mapped address.
   * @param len   Region length.
   * @param flags Synchronization flags
   * @throws LastErrorException when operations fails
   */
  @throws[LastErrorException]
  def msync(addr: Pointer, len: Long, flags: Int): Int =
    NativeLibc.msync(addr, new NativeLong(len), flags)
}