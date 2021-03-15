package ev3dev4s.lcd;

import ev3dev4s.Log;

/**
 * Class to allow running programs over SSH
 *
 * @author Jakub Vaněk
 * @since 2.4.7
 */
class StolenDisplay extends DisplayInterface {
    private ILibc libc;

    /**
     * noop
     */
    public StolenDisplay(ILibc libc) {
        this.libc = libc;
        Brickman.disable();
    }

    /**
     * noop, graphics goes to the display
     */
    @Override
    public void switchToGraphicsMode() {
        Log.log("Switch to graphics mode");
    }

    /**
     * noop, text goes to SSH host
     */
    @Override
    public void switchToTextMode() {
        Log.log("Switch to text mode");
    }

    /**
     * noop, we do not have any resources
     */
    @Override
    public void close() {
        Log.log("Display close");
        // free objects
        closeFramebuffer();
        libc = null;
    }

    @Override
    public synchronized JavaFramebuffer openFramebuffer() {
        if (fbInstance == null) {
            Log.log("Initialing framebuffer in fake console");
            initializeFramebuffer(new NativeFramebuffer("/dev/fb0", libc), true);
        }
        return fbInstance;
    }
}
