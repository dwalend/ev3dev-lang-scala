package ev3dev4s.lcd;

import com.sun.jna.LastErrorException;

/**
 * Creates new Linux BW framebuffer.
 */
//todo not this one
public class BitFramebufferProvider implements FramebufferProvider {

    @Override
    public JavaFramebuffer createFramebuffer(NativeFramebuffer fb, DisplayInterface disp)
        throws LastErrorException, IllegalArgumentException {

        return new BitFramebuffer(fb, disp);
    }
}
