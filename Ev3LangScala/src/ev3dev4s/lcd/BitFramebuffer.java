package ev3dev4s.lcd;

import com.sun.jna.LastErrorException;

import java.awt.image.BufferedImage;

import static ev3dev4s.lcd.NativeConstants.FB_TYPE_PACKED_PIXELS;
import static ev3dev4s.lcd.NativeConstants.FB_VISUAL_MONO01;
import static ev3dev4s.lcd.NativeConstants.FB_VISUAL_MONO10;

/**
 * Linux black-and-white 1bpp framebuffer
 *
 * @since 2.4.7
 */
//todo not this one
public class BitFramebuffer extends LinuxFramebuffer {

    /**
     * Create and initialize new Linux 1bpp framebuffer.
     *
     * @param fb The framebuffer device (e.g. /dev/fb0)
     * @param display Display manager (e.g. /dev/tty)
     */
    public BitFramebuffer(NativeFramebuffer fb, DisplayInterface display)
        throws LastErrorException, IllegalArgumentException {

        super(fb, display);

        if (getFixedInfo().type != FB_TYPE_PACKED_PIXELS) {
            try {
                close();
            } catch (LastErrorException e) {
                throw new RuntimeException("Cannot close framebuffer", e);
            }
            System.out.println("Framebuffer uses non-packed pixels");
            throw new IllegalArgumentException("Only frame buffers with packed pixels are supported");
        }
        // probably duplicated, but this way we are sure
        boolean nonMono = getFixedInfo().visual != FB_VISUAL_MONO10 && getFixedInfo().visual != FB_VISUAL_MONO01;
        boolean non1bpp = getVariableInfo().bits_per_pixel != 1;
        if (nonMono || non1bpp) {
            try {
                close();
            } catch (LastErrorException e) {
                throw new RuntimeException("Cannot close framebuffer", e);
            }
            System.out.println("Framebuffer is not 1bpp mono");
            throw new IllegalArgumentException("Only frame buffers with 1bpp BW are supported");
        }
        // taking ownership
        initializeMemory();
        setDeviceClose(true);
    }

    @Override
    public BufferedImage createCompatibleBuffer(int width, int height) {
        int stride = (width + 7) / 8;
        return createCompatibleBuffer(width, height, stride, new byte[stride * height]);
    }

    @Override
    public BufferedImage createCompatibleBuffer(int width, int height, int stride, byte[] backed) {
        return ImageUtils.createBWImage(width, height, stride, getFixedInfo().visual == FB_VISUAL_MONO01, backed);
    }
}
