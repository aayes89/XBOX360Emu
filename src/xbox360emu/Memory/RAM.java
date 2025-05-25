package xbox360emu.Memory;

import java.util.Arrays;
import xbox360emu.PPCEmuConfig;
import xbox360emu.UTILS.Utilities;

/**
 *
 * @author Slam RAM extends MemoryDevice to provide byte, halfword, word and
 * doubleword access with alignment checks and a physical-to-offset mapping
 * based on a base address.
 */

public class RAM extends MemoryDevice {

    private final int baseAddr;

    public RAM(String name, int size, int baseAddr) {
        super(name, size);
        this.baseAddr = baseAddr;
        initRAM();
    }

    private void initRAM() {
        // Llenamos todo el buffer con ceros usando offset 0 (no baseAddr físico)
        super.fillRegion(0, getSize(), (byte) 0);
    }

    private int toOffset(int address) {
        long off = Integer.toUnsignedLong(address) - Integer.toUnsignedLong(baseAddr);
        if (off < 0 || off >= getSize()) {
            throw new IndexOutOfBoundsException(
                String.format("Invalid physical address 0x%08X for region '%s' (base=0x%08X, size=0x%X)",
                    address, getName(), baseAddr, getSize()));
        }
        return (int) off;
    }

    private void checkAlignment(int address, int alignment) {
        if ((address % alignment) != 0) {
            throw new IllegalArgumentException(
                String.format("Unaligned access at 0x%08X, alignment %d required", address, alignment));
        }
    }

    @Override
    public byte readByte(int address) {
        int off = toOffset(address);
        return super.readByte(off);
    }

    @Override
    public void writeByte(int address, byte value) {
        int off = toOffset(address);
        super.writeByte(off, value);
    }

    public int readHalfWord(int address) {
        checkAlignment(address, 2);
        int off = toOffset(address);
        int b0 = Byte.toUnsignedInt(super.readByte(off));
        int b1 = Byte.toUnsignedInt(super.readByte(off + 1));
        return (b0 << 8) | b1;
    }

    public void writeHalfWord(int address, int value) {
        checkAlignment(address, 2);
        int off = toOffset(address);
        super.writeByte(off, (byte) ((value >> 8) & 0xFF));
        super.writeByte(off + 1, (byte) (value & 0xFF));
    }

    public int readWord(int address) {
        checkAlignment(address, 4);
        int off = toOffset(address);
        int b0 = Byte.toUnsignedInt(super.readByte(off));
        int b1 = Byte.toUnsignedInt(super.readByte(off + 1));
        int b2 = Byte.toUnsignedInt(super.readByte(off + 2));
        int b3 = Byte.toUnsignedInt(super.readByte(off + 3));
        return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
    }

    public void writeWord(int address, int value) {
        checkAlignment(address, 4);
        int off = toOffset(address);
        super.writeByte(off, (byte) ((value >> 24) & 0xFF));
        super.writeByte(off + 1, (byte) ((value >> 16) & 0xFF));
        super.writeByte(off + 2, (byte) ((value >> 8) & 0xFF));
        super.writeByte(off + 3, (byte) (value & 0xFF));
    }

    public long readDoubleWord(int address) {
        checkAlignment(address, 8);
        int off = toOffset(address);
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result = (result << 8) | Byte.toUnsignedLong(super.readByte(off + i));
        }
        return result;
    }

    public void writeDoubleWord(int address, long value) {
        checkAlignment(address, 8);
        int off = toOffset(address);
        for (int i = 7; i >= 0; i--) {
            super.writeByte(off + i, (byte) (value & 0xFF));
            value >>= 8;
        }
    }

    @Override
    public byte[] readRegion(int address, int length) {
        if (!isAddressRangeValid(address, length)) {
            throw new IndexOutOfBoundsException(
                String.format("Invalid physical address range 0x%08X - 0x%08X for region '%s'",
                    address, address + length - 1, getName()));
        }
        int off = toOffset(address);
        byte[] result = new byte[length];
        System.arraycopy(memoryBuffer, off, result, 0, length);
        return result;
    }

    public void writeRegion(int address, byte[] data) {
        if (!isAddressRangeValid(address, data.length)) {
            throw new IndexOutOfBoundsException(
                String.format("Invalid physical address range 0x%08X - 0x%08X for region '%s'",
                    address, address + data.length - 1, getName()));
        }
        int off = toOffset(address);
        System.arraycopy(data, 0, memoryBuffer, off, data.length);
    }

    public boolean isAddressValid(int address) {
        long off = Integer.toUnsignedLong(address) - Integer.toUnsignedLong(baseAddr);
        return off >= 0 && off < getSize();
    }

    public boolean isAddressRangeValid(int address, int length) {
        long startOff = Integer.toUnsignedLong(address) - Integer.toUnsignedLong(baseAddr);
        long endOff = startOff + length - 1;
        return startOff >= 0 && endOff < getSize();
    }
}
