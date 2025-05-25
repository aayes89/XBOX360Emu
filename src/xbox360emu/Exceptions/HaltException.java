package xbox360emu.Exceptions;

/**
 *
 * @author Slam
 */

public class HaltException extends RuntimeException {
    public HaltException() { super("Halted CPU"); }
}
