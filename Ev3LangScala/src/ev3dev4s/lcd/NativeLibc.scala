package ev3dev4s.lcd

import com.sun.jna.LastErrorException
import com.sun.jna.Native
import com.sun.jna.NativeLong
import com.sun.jna.Platform
import com.sun.jna.Pointer
import java.nio.Buffer

/**
 * Native library bindings for standard C library
 *
 * @author leJOS, Jakub Vaněk
 * @since 2.4.7
 */
object NativeLibc {
  Native.register(Platform.C_LIBRARY_NAME)

  // file descriptor operations
  /**
   * Manipulate file descriptor
   *
   * @param fd  File descriptor to operate upon.
   * @param cmd Command code, see manpages for details.
   * @param arg Command argument, command-speciic. See manpages for details.
   * @return Depends on the command. On failure, -1 is returned.
   * @see <a href="https://man7.org/linux/man-pages/man2/fcntl.2.html">man 2 fcntl</a>
   * @throws LastErrorException If errno is set during the operation. Use Native#getLastError to query the error code.
   */
  @throws[LastErrorException]
  @native
  def fcntl(fd: Int, cmd: Int, arg: Int):Int

  /**
   * Invoke an I/O control request
   *
   * @param fd  Opened file descriptor to act upon.
   * @param cmd IO command code; this is device-specific (see manpages and/or kernel sources for details).
   * @param arg IOCTL integer argument, this is device-specific (see manpages and/or kernel sources for details).
   * @return -1 on failure, 0 otherwise (usually).
   * @see <a href="https://man7.org/linux/man-pages/man2/ioctl.2.html">man 2 ioctl</a>
   * @throws LastErrorException If errno is set during the operation. Use Native#getLastError to query the error code.
   */
  @throws[LastErrorException]
  @native
  def ioctl(fd: Int, cmd: Int, arg: Int):Int

  /**
   * Invoke an I/O control request
   *
   * @param fd  Opened file descriptor to act upon.
   * @param cmd IO command code; this is device-specific (see manpages and/or kernel sources for details).
   * @param arg IOCTL integer argument, this is device-specific (see manpages and/or kernel sources for details).
   * @return -1 on failure, 0 otherwise (usually).
   * @see <a href="https://man7.org/linux/man-pages/man2/ioctl.2.html">man 2 ioctl</a>
   * @throws LastErrorException If errno is set during the operation. Use Native#getLastError to query the error code.
   */
  @throws[LastErrorException]
  @native
  def ioctl(fd: Int, cmd: Int, arg: Pointer):Int

  // open/close
  /**
   * Try to open (or create) a file from the specified path.
   *
   * @param path  Path to try to open.
   * @param flags Open mode; typically O_RDONLY/O_WRONLY/O_RDWR combined with other modifiers (see POSIX).
   * @param mode  Permissions to be set on the file if it is going to be created (O_CREAT).
   * @return File descriptor or -1 if the file cannot be opened.
   * @see <a href="https://man7.org/linux/man-pages/man2/open.2.html">man 2 open</a>
   * @throws LastErrorException If errno is set during the operation. Use Native#getLastError to query the error code.
   */
  @throws[LastErrorException]
  @native
  def open(path: String, flags: Int, mode: Int):Int

  /**
   * Close the specified file descriptor.
   *
   * @param fd File descriptor to close.
   * @return -1 on failure, 0 otherwise.
   * @see <a href="https://man7.org/linux/man-pages/man2/close.2.html">man 2 close</a>
   * @throws LastErrorException If errno is set during the operation. Use Native#getLastError to query the error code.
   */
  @throws[LastErrorException]
  @native
  def close(fd: Int):Int

  // read/write
  /**
   * Request a write to the current position in the file referred by a file descriptor.
   *
   * @param fd     File descriptor of the file to write.
   * @param buffer Buffer with bytes to write.
   * @param count  Size of the buffer.
   * @return Number of bytes written or -1 if an error occurred.
   * @see <a href="https://man7.org/linux/man-pages/man2/write.2.html">man 2 write</a>
   * @throws LastErrorException If errno is set during the operation. Use Native#getLastError to query the error code.
   */
  @throws[LastErrorException]
  @native
  def write(fd: Int, buffer: Buffer, count: Int):Int

  /**
   * Request a read from the current position in the file referred by a file descriptor.
   *
   * @param fd     File descriptor of the file to read.
   * @param buffer Buffer where to put the data.
   * @param count  Size of the buffer.
   * @return Number of bytes read or -1 if an error occurred.
   * @see <a href="https://man7.org/linux/man-pages/man2/read.2.html">man 2 read</a>
   * @throws LastErrorException If errno is set during the operation. Use Native#getLastError to query the error code.
   */
  @throws[LastErrorException]
  @native
  def read(fd: Int, buffer: Buffer, count: Int):Int

  // map/unmap
  /**
   * Map a file to memory.
   *
   * @param addr  Address hint from the userspace where to put the mapping in memory. Pass NULL to ignore the hint.
   * @param len   Length of the memory to map.
   * @param prot  Memory protection flags (PROT_READ/PROT_WRITE/PROT_EXEC). See manpages for details.
   * @param flags Memory mapping flags (MAP_SHARED/MAP_FILE/...). See manpages for deatils.
   * @param fd    Opened file descriptor of the file that needs to be mapped to memory.
   * @param off   Offset in the file from which to start the mapping.
   * @return On success, address of the mapped memory. On failure, -1 (MAP_FAILED) is returned.
   * @see <a href="https://man7.org/linux/man-pages/man2/mmap.2.html">man 2 mmap</a>
   * @throws LastErrorException If errno is set during the operation. Use Native#getLastError to query the error code.
   */
  @throws[LastErrorException]
  @native
  def mmap(addr: Pointer, len: NativeLong, prot: Int, flags: Int, fd: Int, off: NativeLong): Pointer

  /**
   * Unmap a file from memory.
   *
   * @param addr Address where the file was mapped.
   * @param len  Length of the mapped area.
   * @return -1 on failure, 0 otherwise.
   * @see <a href="https://man7.org/linux/man-pages/man2/munmap.2.html">man 2 munmap</a>
   * @throws LastErrorException If errno is set during the operation. Use Native#getLastError to query the error code.
   */  @throws[LastErrorException]
  @native
  def munmap(addr: Pointer, len: NativeLong):Int

  /**
   * Synchronize memory-mapped data with file contents.
   *
   * @param addr  Address where the file was mapped.
   * @param len   Length of the mapped area.
   * @param flags Synchronization type (MS_SYNC/MS_ASYNC/MS_INVALIDATE). See manpages for details.
   * @return -1 on failure, 0 otherwise.
   * @see <a href="https://man7.org/linux/man-pages/man2/msync.2.html">man 2 msync</a>
   * @throws LastErrorException If errno is set during the operation. Use Native#getLastError to query the error code.
   */
  @throws[LastErrorException]
  @native
  def msync(addr: Pointer, len: NativeLong, flags: Int):Int
}