package ev3dev4s.lcd;

public class Brickman {

    private static final String DISABLE_BRICKMAN_COMMAND = "sudo systemctl stop brickman";
    private static final String ENABLE_BRICKMAN_COMMAND = "sudo systemctl start brickman";

    /**
     * Disable Brickman.
     */
    public static void disable() {
        System.out.println("Disabling Brickman service");

        Shell.execute(DISABLE_BRICKMAN_COMMAND);

        Runtime.getRuntime().addShutdownHook(new Thread(Brickman::restoreBrickman, "restore brickman"));
    }

    /**
     * Enable Brickman.
     */
    private static void restoreBrickman() {
        System.out.println("Enabling Brickman service");

        Shell.execute(ENABLE_BRICKMAN_COMMAND);
    }
}
