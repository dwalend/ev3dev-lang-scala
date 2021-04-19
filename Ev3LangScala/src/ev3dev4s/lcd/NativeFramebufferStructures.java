package ev3dev4s.lcd;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * A Java container for NativeFramebuffer's JNA Structures.
 *
 * @author David Walend
 * @since v0.0.0
 */
//todo someday add a feature to JNA to support Scala. See https://groups.google.com/g/scala-user/c/xdNt0OrhvMg?pli=1
public final class NativeFramebufferStructures {
    private NativeFramebufferStructures(){}//Never construct

    /**
     * fb_bitfield mapping
     */
    public static class fb_bitfield extends Structure {
        /**
         * beginning of bitfield
         */
        public int offset;
        /**
         * length of bitfield
         */
        public int length;
        /**
         * != 0 : Most significant bit is right
         */
        public int msb_right;

        /**
         * Initialize this structure.
         */
        public fb_bitfield() {
            super(ALIGN_GNUC);
        }

        public fb_bitfield(Pointer p) {
            super(p, ALIGN_GNUC);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("offset", "length", "msb_right");
        }

        /**
         * Calculate little-endian byte offset from the bitshift number.
         *
         * @return Offset in byte array
         */
        public int toLEByteOffset() {
            if (length != 8) {
                throw new IllegalArgumentException("Byte offset is applicable only to 8bit sized components");
            }
            if ((offset % 8) != 0) {
                throw new IllegalArgumentException("Byte offset is applicable only to 8bit aligned components");
            }
            return offset / 8;
        }

        /**
         * Reference wrapper
         */
        public static class ByReference extends fb_bitfield implements Structure.ByReference {
        }

        /**
         * Value wrapper
         */
        public static class ByValue extends fb_bitfield implements Structure.ByValue {
        }
    }

    /**
     * fb_var_screeninfo mapping
     */
    public static class fb_var_screeninfo extends Structure {
        /**
         * visible X resolution
         */
        public int xres;
        /**
         * visible Y resolution
         */
        public int yres;
        /**
         * virtual X resolution
         */
        public int xres_virtual;
        /**
         * virtual Y resolution
         */
        public int yres_virtual;
        /**
         * offset from virtual to visible X resolution
         */
        public int xoffset;
        /**
         * offset from virtual to visible Y resolution
         */
        public int yoffset;
        /**
         * BPP value
         */
        public int bits_per_pixel;
        /**
         * 0 = color, 1 = grayscale, >1 = FOURCC
         */
        public int grayscale;
        /**
         * info about red channel
         */
        public fb_bitfield.ByValue red;
        /**
         * info about green channel
         */
        public fb_bitfield.ByValue green;
        /**
         * info about blue channel
         */
        public fb_bitfield.ByValue blue;
        /**
         * info about transparency channel
         */
        public fb_bitfield.ByValue transp;
        /**
         * != 0 Non standard pixel format
         */
        public int nonstd;
        /**
         * see FB_ACTIVATE_*
         */
        public int activate;
        /**
         * height of picture in mm
         */
        public int height;
        /**
         * width of picture in mm
         */
        public int width;
        /**
         * (OBSOLETE) see fb_info.flags
         */
        public int accel_flags;
        /**
         * pixel clock in ps (pico seconds)
         */
        public int pixclock;
        /**
         * time from sync to picture (pixclocks)
         */
        public int left_margin;
        /**
         * time from picture to sync (pixclocks)
         */
        public int right_margin;
        /**
         * time from sync to picture (pixclocks)
         */
        public int upper_margin;
        /**
         * time from sync to picture (pixclocks)
         */
        public int lower_margin;
        /**
         * length of horizontal sync (pixclocks)
         */
        public int hsync_len;
        /**
         * length of vertical sync (pixclocks)
         */
        public int vsync_len;
        /**
         * see FB_SYNC_*
         */
        public int sync;
        /**
         * see FB_VMODE_*
         */
        public int vmode;
        /**
         * angle we rotate counter clockwise
         */
        public int rotate;
        /**
         * colorspace for FOURCC-based modes
         */
        public int colorspace;
        /**
         * Reserved for future compatibility
         */
        public int[] reserved = new int[4];

        /**
         * Initialize this structure.
         */
        public fb_var_screeninfo() {
            super(ALIGN_GNUC);
        }

        public fb_var_screeninfo(Pointer p) {
            super(p, ALIGN_GNUC);
        }


        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("xres", "yres", "xres_virtual", "yres_virtual",
                    "xoffset", "yoffset", "bits_per_pixel", "grayscale",
                    "red", "green", "blue", "transp", "nonstd", "activate",
                    "height", "width", "accel_flags", "pixclock",
                    "left_margin", "right_margin", "upper_margin", "lower_margin",
                    "hsync_len", "vsync_len", "sync", "vmode", "rotate", "colorspace", "reserved");
        }

        /**
         * Reference wrapper
         */
        public static class ByReference extends fb_var_screeninfo implements Structure.ByReference {
        }

        /**
         * Value wrapper
         */
        public static class ByValue extends fb_var_screeninfo implements Structure.ByValue {
        }
    }

    public static class fb_con2fbmap extends Structure {
        public int console;
        public int framebuffer;

        public fb_con2fbmap() {
            super(ALIGN_GNUC);
        }

        public fb_con2fbmap(Pointer p) {
            super(p, ALIGN_GNUC);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("console", "framebuffer");
        }
    }
}
