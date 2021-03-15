package ev3dev4s.lcd;

import com.sun.jna.LastErrorException;

import java.util.ServiceLoader;

/**
 * Framebuffer factory service provider
 *
 * @author Jakub Vaněk
 * @since 2.4.7
 */
public interface FramebufferProvider {

    /**
     * Initialize system framebuffer
     *
     * @param fb Framebuffer device.
     * @return Initialized framebuffer for the specified path.
     * @throws RuntimeException if no suitable framebuffer is found
     */
    static JavaFramebuffer load(NativeFramebuffer fb, DisplayInterface display) throws AllImplFailedException {

        //todo getting rid of this will get rid of a scan of .class files in the .jar, should really speed things up!
        System.out.println("Loading framebuffer");
        ServiceLoader<FramebufferProvider> loader = ServiceLoader.load(FramebufferProvider.class);
        System.out.println(loader.findFirst());
        for (FramebufferProvider provider : loader) {
            try {
                JavaFramebuffer ok = provider.createFramebuffer(fb, display);
                System.out.println("Framebuffer "+provider.getClass().getSimpleName()+" is compatible");
                return ok;
            } catch (IllegalArgumentException ex) {
                System.out.println("Framebuffer "+provider.getClass().getSimpleName()+" is not compatible");
            } catch (LastErrorException e) {
                System.out.println("Framebuffer "+provider.getClass().getSimpleName()+" threw Exception");
                e.printStackTrace();
            }
        }
        System.out.println("All framebuffer implementations failed");
        throw new AllImplFailedException("No suitable framebuffer found");
    }

    /**
     * Create and initialize a new framebuffer.
     *
     * @param fb   The framebuffer device (e.g. /dev/fb0)
     * @param display Display manager (e.g. /dev/tty)
     * @throws IllegalArgumentException When this framebuffer is not compatible with this device.
     * @throws LastErrorException       When there was an error accessing the device.
     */
    JavaFramebuffer createFramebuffer(NativeFramebuffer fb, DisplayInterface display)
            throws LastErrorException, IllegalArgumentException;

}