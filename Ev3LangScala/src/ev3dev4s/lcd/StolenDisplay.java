package ev3dev4s.lcd;

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
        System.out.println("Switch to graphics mode");
    }

    /**
     * noop, text goes to SSH host
     */
    @Override
    public void switchToTextMode() {
        System.out.println("Switch to text mode");
    }

    /**
     * noop, we do not have any resources
     */
    @Override
    public void close() {
        System.out.println("Display close");
        // free objects
        closeFramebuffer();
        libc = null;
    }

    @Override
    public synchronized JavaFramebuffer openFramebuffer() {
        if (fbInstance == null) {
            System.out.println("Initialing framebuffer in fake console");
            initializeFramebuffer(new NativeFramebuffer("/dev/fb0", libc), true);
        }
        return fbInstance;
    }
}
