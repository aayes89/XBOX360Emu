package xbox360emu.FILES;

/**
 *
 * @author Slam
 */

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import xbox360emu.CPU.CPU;
import xbox360emu.Memory.RAM;

public class ELFLoader {

    private static final int EM_PPC = 20;  // PowerPC
    private static final int PT_LOAD = 1;  // Segmento cargable

    private static final byte ELFCLASS32 = 1;
    private static final byte ELFCLASS64 = 2;

    private static final byte ELFDATA2MSB = 2; // Big Endian

    // Tamaños estándar de encabezados
    private static final int EHDR32_SIZE = 52;
    private static final int PHDR32_SIZE = 32;

    private static final int EHDR64_SIZE = 64;
    private static final int PHDR64_SIZE = 56;

    public static void loadELF32(byte[] data, RAM ram, CPU mCPU) {
        if (data.length < EHDR32_SIZE) {
            throw new IllegalArgumentException("ELF32 data too small");
        }

        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);

        byte[] e_ident = new byte[16];
        buf.get(e_ident);

        if (e_ident[0] != 0x7F || e_ident[1] != 'E' || e_ident[2] != 'L' || e_ident[3] != 'F') {
            throw new IllegalArgumentException("Not an ELF file");
        }

        if (e_ident[4] != ELFCLASS32) {
            throw new IllegalArgumentException("Not ELF32 class");
        }

        if (e_ident[5] != ELFDATA2MSB) {
            throw new IllegalArgumentException("Unsupported endianess: only big endian supported");
        }

        // Verifica arquitectura
        if (data.length < 20) {
            throw new IllegalArgumentException("ELF32 header incomplete for machine check");
        }
        buf.position(18);
        short e_machine = buf.getShort();
        if (e_machine != EM_PPC) {
            throw new IllegalArgumentException("Unsupported architecture: not PowerPC");
        }

        // Leer entrada y tabla de segmentos
        if (data.length < 24 + 8) {
            throw new IllegalArgumentException("ELF32 header incomplete for entry and phoff");
        }
        buf.position(24);
        int e_entry = buf.getInt();
        int e_phoff = buf.getInt();

        if (e_phoff < 0 || e_phoff >= data.length) {
            throw new IllegalArgumentException("Invalid program header offset");
        }

        if (data.length < 42 + 4) {
            throw new IllegalArgumentException("ELF32 header incomplete for phentsize and phnum");
        }
        buf.position(42);
        short e_phentsz = buf.getShort();
        short e_phnum = buf.getShort();

        if (e_phentsz != PHDR32_SIZE) {
            throw new IllegalArgumentException("Unexpected ELF32 program header size");
        }

        if ((long) e_phoff + (long) e_phnum * (long) e_phentsz > data.length) {
            throw new IllegalArgumentException("Program header table exceeds file size");
        }

        for (int i = 0; i < e_phnum; i++) {
            long phdrPos = (long) e_phoff + (long) i * (long) e_phentsz;
            if (phdrPos < 0 || phdrPos + PHDR32_SIZE > data.length) {
                throw new IllegalArgumentException("Program header out of bounds");
            }

            buf.position((int) phdrPos);
            int p_type = buf.getInt();
            int p_offset = buf.getInt();
            int p_vaddr = buf.getInt();
            buf.getInt(); // p_paddr ignorado
            int p_filesz = buf.getInt();
            int p_memsz = buf.getInt();
            buf.getInt(); // p_flags ignorado
            buf.getInt(); // p_align ignorado

            if (p_type != PT_LOAD) continue;

            if (p_offset < 0 || p_offset + p_filesz > data.length) {
                throw new IllegalArgumentException("Segment exceeds ELF size");
            }

            // Validar rango RAM (asumiendo int válido)
            if (!ram.isAddressRangeValid(p_vaddr, p_memsz)) {
                throw new IllegalArgumentException("Segment memory range invalid or out of RAM bounds");
            }

            byte[] segment = new byte[p_filesz];
            System.arraycopy(data, p_offset, segment, 0, p_filesz);
            ram.writeRegion(segment, p_vaddr, p_vaddr + p_filesz);

            if (p_memsz > p_filesz) {
                ram.fillRegion(p_vaddr + p_filesz, p_vaddr + p_memsz, (byte) 0);
            }
        }

        if (!ram.isAddressValid(e_entry)) {
            throw new IllegalArgumentException("Entry point outside RAM range");
        }
        mCPU.getRegisters().setPC(e_entry);
    }

    public static void loadELF64(byte[] data, RAM ram, CPU mCPU) {
        if (data.length < EHDR64_SIZE) {
            throw new IllegalArgumentException("ELF64 data too small");
        }

        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);

        byte[] e_ident = new byte[16];
        buf.get(e_ident);

        if (e_ident[0] != 0x7F || e_ident[1] != 'E' || e_ident[2] != 'L' || e_ident[3] != 'F') {
            throw new IllegalArgumentException("Not an ELF file");
        }

        if (e_ident[4] != ELFCLASS64) {
            throw new IllegalArgumentException("Not ELF64 class");
        }

        if (e_ident[5] != ELFDATA2MSB) {
            throw new IllegalArgumentException("Unsupported endianess: only big endian supported");
        }

        buf.position(16);
        short e_type = buf.getShort();
        short e_machine = buf.getShort();
        if (e_machine != EM_PPC) {
            throw new IllegalArgumentException("Unsupported architecture: not PowerPC");
        }

        buf.getInt(); // e_version

        long e_entry = buf.getLong();
        long e_phoff = buf.getLong();

        if (e_phoff < 0 || e_phoff >= data.length) {
            throw new IllegalArgumentException("Invalid program header offset");
        }

        buf.getLong(); // e_shoff
        buf.getInt();  // e_flags
        buf.getShort(); // e_ehsize
        short e_phentsz = buf.getShort();
        short e_phnum = buf.getShort();

        if (e_phentsz != PHDR64_SIZE) {
            throw new IllegalArgumentException("Unexpected ELF64 program header size");
        }

        if (e_phoff + (long) e_phnum * (long) e_phentsz > data.length) {
            throw new IllegalArgumentException("Program header table exceeds file size");
        }

        for (int i = 0; i < e_phnum; i++) {
            long phdrBase = e_phoff + (long) i * (long) e_phentsz;
            if (phdrBase < 0 || phdrBase + PHDR64_SIZE > data.length) {
                throw new IllegalArgumentException("Program header out of bounds");
            }

            buf.position((int) phdrBase);
            int p_type = buf.getInt();
            int p_flags = buf.getInt();
            long p_offset = buf.getLong();
            long p_vaddr = buf.getLong();
            buf.getLong(); // p_paddr
            long p_filesz = buf.getLong();
            long p_memsz = buf.getLong();
            buf.getLong(); // p_align

            if (p_type != PT_LOAD) continue;

            if (p_offset < 0 || p_offset + p_filesz > data.length) {
                throw new IllegalArgumentException("Segment exceeds ELF size");
            }

            if (p_vaddr < 0 || p_vaddr > Integer.MAX_VALUE
             || p_filesz > Integer.MAX_VALUE
             || p_memsz > Integer.MAX_VALUE) {
                throw new IllegalArgumentException("Segment address or size too large for RAM addressing");
            }

            int ivaddr = (int) p_vaddr;
            int ifilesz = (int) p_filesz;
            int imemsz = (int) p_memsz;

            if (!ram.isAddressRangeValid(ivaddr, imemsz)) {
                throw new IllegalArgumentException("Segment memory range invalid or out of RAM bounds");
            }

            byte[] segment = new byte[ifilesz];
            System.arraycopy(data, (int) p_offset, segment, 0, ifilesz);
            ram.writeRegion(segment, ivaddr, ivaddr + ifilesz);

            if (imemsz > ifilesz) {
                ram.fillRegion(ivaddr + ifilesz, ivaddr + imemsz, (byte) 0);
            }
        }

        if (e_entry < 0 || e_entry > Integer.MAX_VALUE || !ram.isAddressValid((int) e_entry)) {
            throw new IllegalArgumentException("Entry point outside RAM range");
        }

        mCPU.getRegisters().setPC((int) e_entry);
    }
}
