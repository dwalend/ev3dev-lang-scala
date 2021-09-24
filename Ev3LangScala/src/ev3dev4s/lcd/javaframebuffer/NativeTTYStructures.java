package ev3dev4s.lcd.javaframebuffer;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * A Java container for NativeTTY's JNA Structures.
 *
 * @author David Walend
 * @since v0.0.0
 */
//todo someday add a feature to JNA to support Scala. See https://groups.google.com/g/scala-user/c/xdNt0OrhvMg?pli=1
public final class NativeTTYStructures {
    private NativeTTYStructures(){}//don't make one of these

    /**
     * Info about an active VT.
     */
    public static class vt_stat extends Structure {
        public short v_active; /* active vt */
        public short v_signal; /* signal to send */
        public short v_state; /* vt bitmask */

        public vt_stat() {
            super(ALIGN_GNUC);
        }

        public vt_stat(Pointer p) {
            super(p, ALIGN_GNUC);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("v_active", "v_signal", "v_state");
        }
    }

    /**
     * Info about VT configuration.
     */
    public static class vt_mode extends Structure {
        public byte mode;        /* vt mode */
        public byte waitv;        /* if set, hang on writes if not active */
        public short relsig;        /* signal to raise on release req */
        public short acqsig;        /* signal to raise on acquisition */
        public short frsig;        /* unused (set to 0) */

        public vt_mode() {
            super(ALIGN_GNUC);
        }

        public vt_mode(Pointer p) {
            super(p, ALIGN_GNUC);
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("mode", "waitv", "relsig", "acqsig", "frsig");
        }
    }
}
