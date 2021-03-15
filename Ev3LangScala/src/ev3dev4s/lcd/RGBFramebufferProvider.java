package ev3dev4s.lcd;

import com.sun.jna.LastErrorException;

/**
 * Creates new Linux RGB framebuffer.
 */
public class RGBFramebufferProvider implements FramebufferProvider {

    @Override
    public JavaFramebuffer createFramebuffer(NativeFramebuffer fb, DisplayInterface disp)
        throws LastErrorException, IllegalArgumentException {
        return new RGBFramebuffer(fb, disp);
    }
}
