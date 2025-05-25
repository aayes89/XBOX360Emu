package xbox360emu.Memory;

import java.util.Arrays;

/**
 *
 * @author Slam
 */

public abstract class MemoryDevice {

    private final String name;
    private final int size;
    protected final byte[] memoryBuffer;

    public MemoryDevice(String name, int size) {
        this.name = name;
        this.size = size;
        this.memoryBuffer = new byte[size];
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public byte readByte(int offset) {
        if (offset < 0 || offset >= size) {
            throw new IndexOutOfBoundsException(
                    String.format("Offset 0x%X out of bounds for memory device '%s'", offset, name));
        }
        return memoryBuffer[offset];
    }

    public void writeByte(int offset, byte value) {
        if (offset < 0 || offset >= size) {
            throw new IndexOutOfBoundsException(
                    String.format("Offset 0x%X out of bounds for memory device '%s'", offset, name));
        }
        memoryBuffer[offset] = value;
    }

    public void fillRegion(int offset, int length, byte value) {
        if (offset < 0 || offset + length > size) {
            throw new IndexOutOfBoundsException(
                    String.format("Fill memoryBuffer out of bounds (offset=0x%X, length=0x%X) for '%s'",
                            offset, length, name));
        }
        for (int i = 0; i < length; i++) {
            memoryBuffer[offset + i] = value;
        }
    }

    public byte[] readRegion(int start, int end) {
        if (start < 0 || end > size || start > end) {
            throw new IndexOutOfBoundsException("Invalid readRegion range");
        }
        return Arrays.copyOfRange(memoryBuffer, start, end);
    }

    public void writeRegion(byte[] data, int start, int end) {
        if (start < 0 || end > size || start > end) {
            throw new IndexOutOfBoundsException("Invalid writeRegion range");
        }
        if (data.length < (end - start)) {
            throw new IllegalArgumentException("Source data too small");
        }
        System.arraycopy(data, 0, memoryBuffer, start, end - start);
    }

    

    /**
     * Exposición directa del arreglo interno, úsalo con precaución.
     */
    public byte[] getRegion() {
        return Arrays.copyOf(memoryBuffer, size);
    }
}
