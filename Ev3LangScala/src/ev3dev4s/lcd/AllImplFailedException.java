package ev3dev4s.lcd;

/**
 * Situation when SPI is to be open, but none of
 * the available implementations worked.
 *
 * @author Jakub Vaněk
 * @since 2.4.7
 */
public class AllImplFailedException extends RuntimeException {

    /**
     * Initialize new exception with message.
     *
     * @param message Message detailing the problem.
     */
    public AllImplFailedException(String message) {
        super(message);
    }
}
