package ev3dev4s.lcd;

import com.sun.jna.LastErrorException;

/**
 * Helper class for initializing real on-brick display.
 *
 * @author Jakub Vaněk
 * @since 2.4.7
 */
public final class SystemDisplay {

    private SystemDisplay() {
    }

    /**
     * <p>Initialize real on-brick display.</p>
     * <p><b>BEWARE:</b> this function may be called only once,
     * otherwise the behavior is undefined.</p>
     *
     * @return new instance of display appropriate for the current session
     * @throws RuntimeException initialization of the display fails
     */
    public static DisplayInterface initializeRealDisplay() {
        ILibc libc = new NativeLibc();

        System.out.println("initializing new real display");
        try {
            return new OwnedDisplay(libc);
        } catch (LastErrorException e) {
            int errno = e.getErrorCode();
            if (errno == NativeConstants.ENOTTY || errno == NativeConstants.ENXIO) {
                System.out.println("real display init failed, "
                    + "but it was caused by not having a real TTY, using fake console");
                // we do not run from Brickman
                return new StolenDisplay(libc);
            } else {
                throw e;
            }
        }
    }

    /**
     * <p>Initialize real on-brick display with framebuffer.</p>
     * <p><b>BEWARE:</b> this function may be called only once,
     * otherwise the behavior is undefined.</p>
     *
     * @return new instance of framebuffer appropriate for the current session
     * @throws RuntimeException initialization of the display or framebuffer fails
     */
    public static JavaFramebuffer initializeRealFramebuffer() {
        return initializeRealDisplay().openFramebuffer();
    }

}
