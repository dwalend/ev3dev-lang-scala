package ev3dev4s.lcd;

import com.sun.jna.LastErrorException;
import com.sun.jna.Pointer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * <p>Linux Java2D framebuffer.</p>
 *
 * @since 2.4.7
 */
public abstract class LinuxFramebuffer implements JavaFramebuffer {
    /**
     * Underlying fixed framebuffer info.
     */
    private NativeFramebuffer.fb_fix_screeninfo fixinfo;
    /**
     * Underlying variable framebuffer info.
     */
    private NativeFramebuffer.fb_var_screeninfo varinfo;
    /**
     * Underlying native Linux device.
     */
    private NativeFramebuffer device;
    /**
     * Memory-mapped memory from Linux framebuffer device.
     */
    private Pointer videomem;
    /**
     * Whether to enable display output.
     */
    private boolean flushEnabled;
    /**
     * Framebuffer backup for VT switches.
     */
    private byte[] backup;
    /**
     * Cache blank image.
     */
    private BufferedImage blank;
    /**
     * Whether to close the nativeframebuffer device when closing this framebuffer.
     */
    private boolean closeDevice;
    /**
     * Display manager
     */
    private DisplayInterface display;

    /**
     * Create and initialize new Linux-based Java2D framebuffer.
     *
     * @param fb   Framebuffer device (e.g. /dev/fb0)
     * @param display Display manager (e.g. /dev/tty)
     */
    public LinuxFramebuffer(NativeFramebuffer fb, DisplayInterface display) throws LastErrorException {
        setDeviceClose(false);
        device = fb;
        this.display = display;
        fixinfo = device.getFixedScreenInfo();
        varinfo = device.getVariableScreenInfo();
        varinfo.xres_virtual = varinfo.xres;
        varinfo.yres_virtual = varinfo.yres;
        varinfo.xoffset = 0;
        varinfo.yoffset = 0;
        device.setVariableScreenInfo(varinfo);
        videomem = null;
        backup = new byte[(int) getBufferSize()];
        blank = null;
        flushEnabled = true;
        System.out.println("Opened LinuxFB, mode "+varinfo.xres+"x"+varinfo.yres+"x"+varinfo.bits_per_pixel+"bpp");
    }

    protected void initializeMemory() throws LastErrorException {
        videomem = device.mmap(getBufferSize());
    }

    @Override
    public void close() throws LastErrorException {
        System.out.println("Closing LinuxFB");
        if (videomem != null) {
            device.munmap(videomem, getBufferSize());
        }
        if (closeDevice && device != null) {
            device.close();
        }
        // free objects
        if (display != null) {
            display.releaseFramebuffer(this);
        }
        display = null;
        blank = null;
        device = null;
        backup = null;
        fixinfo = null;
        varinfo = null;
    }

    @Override
    public int getWidth() {
        return varinfo.xres;
    }

    @Override
    public int getHeight() {
        return varinfo.yres;
    }

    @Override
    public int getStride() {
        return fixinfo.line_length;
    }

    @Override
    public BufferedImage createCompatibleBuffer() {
        return createCompatibleBuffer(getWidth(), getHeight(), getFixedInfo().line_length);
    }

    @Override
    public abstract BufferedImage createCompatibleBuffer(int width, int height);

    @Override
    public BufferedImage createCompatibleBuffer(int width, int height, int stride) {
        return createCompatibleBuffer(width, height, stride, new byte[height * stride]);
    }

    @Override
    public abstract BufferedImage createCompatibleBuffer(int width, int height, int stride, byte[] backed);

    @Override
    public void flushScreen(BufferedImage compatible) {
        if (flushEnabled) {
            System.out.println("Drawing frame on framebuffer");
            videomem.write(0, ImageUtils.getImageBytes(compatible), 0, (int) getBufferSize());
            device.msync(videomem, getBufferSize(), NativeConstants.MS_SYNC);
        } else {
            System.out.println("Not drawing frame on framebuffer");
        }
    }

    @Override
    public void setFlushEnabled(boolean rly) {
        this.flushEnabled = rly;
    }

    @Override
    public void storeData() {
        System.out.println("Storing framebuffer snapshot");
        videomem.read(0, backup, 0, (int) getBufferSize());
    }

    @Override
    public void restoreData() {
        System.out.println("Restoring framebuffer snapshot");
        videomem.write(0, backup, 0, (int) getBufferSize());
        device.msync(videomem, getBufferSize(), NativeConstants.MS_SYNC);
    }

    @Override
    public void clear() {
        System.out.println("Clearing framebuffer");
        if (blank == null) {
            blank = createCompatibleBuffer();
            Graphics2D gfx = blank.createGraphics();
            gfx.setColor(Color.WHITE);
            gfx.fillRect(0, 0, getWidth(), getHeight());
            gfx.dispose();
        }
        flushScreen(blank);
    }

    @Override
    public DisplayInterface getDisplay() {
        return display;
    }

    /**
     * Get Linux framebuffer fixed info.
     *
     * @return Fixed information about the framebuffer.
     */
    public NativeFramebuffer.fb_fix_screeninfo getFixedInfo() {
        return fixinfo;
    }

    /**
     * Get Linux framebuffer variable info.
     *
     * @return Variable information about the framebuffer.
     */
    public NativeFramebuffer.fb_var_screeninfo getVariableInfo() {
        return varinfo;
    }

    /**
     * Get the underlying native device.
     *
     * @return Linux device
     */
    public NativeFramebuffer getDevice() {
        return device;
    }

    /**
     * Get direct access to the video memory.
     *
     * @return JNA pointer to the framebuffer
     * @see LinuxFramebuffer#getBufferSize() for memory size.
     */
    public Pointer getMemory() {
        return videomem;
    }

    /**
     * Get video memory size.
     *
     * @return Size of video memory in bytes.
     * @see LinuxFramebuffer#getMemory() for memory pointer.
     */
    public long getBufferSize() {
        return (long) getHeight() * getStride();
    }

    /**
     * Set whether to close the underlying device on exit.
     */
    protected void setDeviceClose(boolean rly) {
        closeDevice = rly;
    }
}
