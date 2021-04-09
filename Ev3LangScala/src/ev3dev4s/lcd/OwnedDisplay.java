package ev3dev4s.lcd;

import com.sun.jna.LastErrorException;
import ev3dev4s.Log;

import java.io.IOException;

import static ev3dev4s.lcd.NativeConstants.KD_GRAPHICS;
import static ev3dev4s.lcd.NativeConstants.KD_TEXT;
import static ev3dev4s.lcd.NativeConstants.K_OFF;
import static ev3dev4s.lcd.NativeConstants.O_RDWR;
import static ev3dev4s.lcd.NativeConstants.SIGUSR2;
import static ev3dev4s.lcd.NativeConstants.VT_AUTO;
import static ev3dev4s.lcd.NativeConstants.VT_PROCESS;

/**
 * <p>System console manager.</p>
 *
 * <p>It manages the output mode of the display. It is possible
 * to switch between graphics mode and text mode. Graphics mode reserves
 * the display for drawing operation and hides text output. On the
 * other hand, text mode suspends drawing operations and shows the text
 * on the Linux console.
 * This class also manages VT (= Virtual Terminal = console) switches
 * in the case of a VT switch occuring when graphics mode is set.</p>
 *
 * <p>Implementation of this class is based on the GRX3 linuxfb plugin.</p>
 *
 * @author Jakub Vaněk
 * @since 2.4.7
 */
class OwnedDisplay extends DisplayInterface {

    private String fbPath = null;
    private NativeTTY ttyfd = null;
    private int old_kbmode;
    private Thread deinitializer;

    /**
     * <p>Initialize the display, register event handlers and switch to text mode.</p>
     *
     * @throws RuntimeException when initialization or mode switch fails.
     */
    public OwnedDisplay() {
        try {
            Log.log("Initialing system console");
            initialize();
            deinitializer = new Thread(this::deinitialize, "console restore");
            Runtime.getRuntime().addShutdownHook(deinitializer);
            switchToTextMode();
        } catch (IOException e) {
            Log.log("System console initialization failed");
            throw new RuntimeException("Error initializing system console", e);
        }
    }

    /**
     * <p>Initialize the display state.</p>
     *
     * <p>Opens the current VT, saves keyboard mode and
     * identifies the appropriate framebuffer device.</p>
     *
     * @throws IOException When the initialization fails.
     */
    private void initialize() throws IOException {
        NativeFramebuffer fbfd = null;
        boolean success = false;
        try {
            Log.log("Opening TTY");
            ttyfd = new NativeTTY("/dev/tty", O_RDWR);
            //TODO Review to put final (Checkstyle)
            int activeVT = ttyfd.getVTstate().v_active;
            old_kbmode = ttyfd.getKeyboardMode();

            Log.log("Opening FB 0");
            fbfd = new NativeFramebuffer("/dev/fb0");
            int fbn = fbfd.mapConsoleToFramebuffer(activeVT);
            Log.log("map vt"+activeVT+" -> fb "+fbn);

            if (fbn < 0) {
                Log.log("No framebuffer for current TTY");
                throw new IOException("No framebuffer device for the current VT");
            }
            fbPath = "/dev/fb" + fbn;
            if (fbn != 0) {
                Log.log("Redirected to FB " + fbn);
                fbfd.close();
                fbfd = new NativeFramebuffer(fbPath);
            }

            success = true;
        } finally {
            if (fbfd != null) {
                fbfd.close();
            }
            if (!success) {
                if (ttyfd != null) {
                    ttyfd.close();
                }
            }
        }
    }

    @Override
    public void close() {
        if (ttyfd != null && ttyfd.isOpen()) {
            deinitialize();
            Runtime.getRuntime().removeShutdownHook(deinitializer);
            deinitializer = null;
        }
    }

    /**
     * <p>Put the display to a state where it is ready for returning.</p>
     *
     * <p>Keyboard mode is restored, text mode is set and VT autoswitch is enabled.
     * Then, console file descriptor is closed.</p>
     */
    private void deinitialize() {
        Log.log("Closing system console");
        try {
            ttyfd.setKeyboardMode(old_kbmode);
            ttyfd.setConsoleMode(KD_TEXT);

            NativeTTY.vt_mode vtm = new NativeTTY.vt_mode();
            vtm.mode = VT_AUTO;
            vtm.relsig = 0;
            vtm.acqsig = 0;
            ttyfd.setVTmode(vtm);

            ttyfd.close();

        } catch (LastErrorException e) {
            System.err.println("Error occured during console shutdown: " + e.getMessage());
            e.printStackTrace();
        }
        // free objects
        closeFramebuffer();
        ttyfd = null;
    }

    /**
     * <p>Switch the display to a graphics mode.</p>
     *
     * <p>It switches VT to graphics mode with keyboard turned off.
     * Then, it tells kernel to notify Java when VT switch occurs.
     * Also, framebuffer contents are restored and write access is enabled.</p>
     *
     * @throws RuntimeException when the switch fails
     */
    public void switchToGraphicsMode() {
        Log.log("Switching console to graphics mode");
        try {
            ttyfd.setKeyboardMode(K_OFF);
            ttyfd.setConsoleMode(KD_GRAPHICS);

            NativeTTY.vt_mode vtm = new NativeTTY.vt_mode();
            vtm.mode = VT_PROCESS;
            vtm.relsig = SIGUSR2;
            vtm.acqsig = SIGUSR2;
            ttyfd.setVTmode(vtm);
        } catch (LastErrorException e) {
            throw new RuntimeException("Switch to graphics mode failed", e);
        }

        if (fbInstance != null) {
            fbInstance.restoreData();
            fbInstance.setFlushEnabled(true);
        }
    }

    /**
     * <p>Switch the display to a text mode.</p>
     *
     * <p>It stores framebuffer data and disables write access. Then,
     * it switches VT to text mode and allows kernel to auto-switch it.</p>
     *
     * @throws RuntimeException when the switch fails
     */
    public void switchToTextMode() {
        Log.log("Switching console to text mode");
        if (fbInstance != null) {
            fbInstance.setFlushEnabled(false);
            fbInstance.storeData();
        }
        try {
            ttyfd.setConsoleMode(KD_TEXT);

            NativeTTY.vt_mode vtm = new NativeTTY.vt_mode();
            vtm.mode = VT_AUTO;
            vtm.relsig = 0;
            vtm.acqsig = 0;
            ttyfd.setVTmode(vtm);
        } catch (LastErrorException e) {
            throw new RuntimeException("Switch to text mode failed", e);
        }
        Log.log("Switching to text mode succeeded");
    }

    /**
     * <p>Get the framebuffer for the system display.</p>
     *
     * <p>The framebuffer is initialized only once, later calls
     * return references to the same instance.</p>
     *
     * @return Java framebuffer compatible with the system display.
     * @throws RuntimeException when switch to graphics mode or the framebuffer initialization fails.
     */
    public synchronized JavaFramebuffer openFramebuffer() {
        if (fbInstance == null) {
            Log.log("Initialing framebuffer in system console");
            switchToGraphicsMode();
            initializeFramebuffer(new NativeFramebuffer(fbPath), true);
        }
        return fbInstance;
    }
}
