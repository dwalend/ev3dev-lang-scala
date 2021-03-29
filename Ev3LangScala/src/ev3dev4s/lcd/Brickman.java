package ev3dev4s.lcd;

import ev3dev4s.Log;
import ev3dev4s.sysfs.Shell;

//todo move to StolenDisplay
public class Brickman {

    private static final String DISABLE_BRICKMAN_COMMAND = "sudo systemctl stop brickman";
    private static final String ENABLE_BRICKMAN_COMMAND = "sudo systemctl start brickman";

    /**
     * Disable Brickman.
     */
    public static void disable() {
        Log.log("Disabling Brickman service");

        Shell.execute(DISABLE_BRICKMAN_COMMAND);

        Runtime.getRuntime().addShutdownHook(new Thread(Brickman::restoreBrickman, "restore brickman"));
    }

    /**
     * Enable Brickman.
     */
    private static void restoreBrickman() {
        Log.log("Enabling Brickman service");

        Shell.execute(ENABLE_BRICKMAN_COMMAND);
    }
}
