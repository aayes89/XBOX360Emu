package xbox360emu.Interfaces;

/**
 *
 * @author Slam
 */
public interface MemoryAccessListener {
    void onRead(int address, byte value);
    void onWrite(int address, byte value);
}
