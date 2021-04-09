package ev3dev4s.lcd;

import com.sun.jna.LastErrorException;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.io.Closeable;
import java.nio.ByteBuffer;

/**
 * <p>This class provides access to Linux files using native I/O operations. It is
 * implemented using the JNA package. The class is required because certain
 * operations (like ioctl) that are required by the Lego kernel module interface are
 * not support by standard Java methods. In addition standard Java memory mapped
 * files do not seem to function correctly when used with Linux character devices.</p>
 *
 * <p>Only JNA is used, the original interface used combination of Java and JNA.</p>
 *
 * @author andy, Jakub Vaněk
 */
@SuppressWarnings({"unused", "OctalInteger"})
public class NativeFile implements Closeable, AutoCloseable {  //todo why both??
    protected int fd = -1;

    /**
     * Basic constructor.
     */
    protected NativeFile() {
    }
    


    /**
     * Create a NativeFile object and open the associated file/device
     * for native access.
     *
     * @param fname the name of the file to open
     * @param flags Linux style file access flags
     * @throws LastErrorException when operations fails
     */
    public NativeFile(String fname, int flags) throws LastErrorException {
        open(fname, flags);
    }

    /**
     * Create a NativeFile object and open the associated file/device
     * for native access.
     *
     * @param fname the name of the file to open
     * @param flags Linux style file access flags
     * @param mode  Linux style file access mode
     * @throws LastErrorException when operations fails
     */
    public NativeFile(String fname, int flags, int mode) throws LastErrorException {
        open(fname, flags, mode);
    }

    /**
     * Check whether this file has been open()en.
     *
     * @return True when the filedescriptor is valid.
     */
    public boolean isOpen() {
        return fd != -1;
    }

    /**
     * Open the specified file/device for native access.
     *
     * @param fname the name of the file to open
     * @param flags Linux style file access flags
     * @throws LastErrorException when operations fails
     */
    public void open(String fname, int flags) throws LastErrorException {
        int DEFAULT_PRIVS = 0777;
        fd = NativeLibc.open(fname, flags, DEFAULT_PRIVS);
    }

    /**
     * Open the specified file/device for native access.
     *
     * @param fname the name of the file to open
     * @param flags Linux style file access flags
     * @param mode  Linux style file access mode
     * @throws LastErrorException when operations fails
     */
    public void open(String fname, int flags, int mode) throws LastErrorException {
        fd = NativeLibc.open(fname, flags, mode);
    }

    /**
     * Attempt to read the requested number of bytes from the associated file.
     *
     * @param buf location to store the read bytes
     * @param len number of bytes to attempt to read
     * @return number of bytes read
     * @throws LastErrorException when operations fails
     */
    public int read(byte[] buf, int len) throws LastErrorException {
        return NativeLibc.read(fd, ByteBuffer.wrap(buf), len);
    }

    /**
     * Attempt to write the requested number of bytes to the associated file.
     *
     * @param buf    location to store the read bytes
     * @param offset the offset within buf to take data from for the write
     * @param len    number of bytes to attempt to read
     * @return number of bytes read
     * @throws LastErrorException when operations fails
     */
    public int write(byte[] buf, int offset, int len) throws LastErrorException {
        return NativeLibc.write(fd, ByteBuffer.wrap(buf, offset, len), len);
    }

    /**
     * Attempt to read the requested number of byte from the associated file.
     *
     * @param buf    location to store the read bytes
     * @param offset offset with buf to start storing the read bytes
     * @param len    number of bytes to attempt to read
     * @return number of bytes read
     * @throws LastErrorException when operations fails
     */
    public int read(byte[] buf, int offset, int len) throws LastErrorException {
        return NativeLibc.read(fd, ByteBuffer.wrap(buf, offset, len), len);
    }

    /**
     * Attempt to write the requested number of bytes to the associated file.
     *
     * @param buf location to store the read bytes
     * @param len number of bytes to attempt to read
     * @return number of bytes read
     * @throws LastErrorException when operations fails
     */
    public int write(byte[] buf, int len) throws LastErrorException {
        return NativeLibc.write(fd, ByteBuffer.wrap(buf), len);
    }

    /**
     * Perform a Linux style ioctl operation on the associated file.
     *
     * @param req  ioctl operation to be performed
     * @param info output as integer
     * @return Linux style ioctl return
     * @throws LastErrorException when operations fails
     */
    public int ioctl(int req, IntByReference info) throws LastErrorException {
        return NativeLibc.ioctl(fd, req, info.getPointer());
    }

    /**
     * Perform a Linux style ioctl operation on the associated file.
     *
     * @param req  ioctl operation to be performed
     * @param info input as integer
     * @return Linux style ioctl return
     * @throws LastErrorException when operations fails
     */
    public int ioctl(int req, int info) throws LastErrorException {
        return NativeLibc.ioctl(fd, req, info);
    }

    /**
     * Perform a Linux style ioctl operation on the associated file.
     *
     * @param req ioctl operation to be performed
     * @param buf pointer to ioctl parameters
     * @return Linux style ioctl return
     * @throws LastErrorException when operations fails
     */
    public int ioctl(int req, Pointer buf) throws LastErrorException {
        return NativeLibc.ioctl(fd, req, buf);
    }

    /**
     * Close the associated file
     *
     * @throws LastErrorException when operations fails
     */
    @Override
    public void close() throws LastErrorException {
        if (fd != -1) {
            int copy = fd;
            fd = -1;
            NativeLibc.close(copy);
        }
    }

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
    public Pointer mmap(long len, int prot, int flags, long off) throws LastErrorException {
        Pointer p = NativeLibc.mmap(new Pointer(0), new NativeLong(len), prot, flags, fd, new NativeLong(off));
        if (p.equals(new Pointer(-1))) {
            throw new LastErrorException("mmap() failed");
        }
        return p;
    }

    /**
     * Unmap mapped memory region.
     *
     * @param addr Mapped address.
     * @param len  Region length.
     * @throws LastErrorException when operations fails
     */
    public void munmap(Pointer addr, long len) throws LastErrorException {
        NativeLibc.munmap(addr, new NativeLong(len));
    }

    /**
     * Synchronize mapped memory region.
     *
     * @param addr  Mapped address.
     * @param len   Region length.
     * @param flags Synchronization flags
     * @throws LastErrorException when operations fails
     */
    public void msync(Pointer addr, long len, int flags) throws LastErrorException {
        NativeLibc.msync(addr, new NativeLong(len), flags);
    }

}
