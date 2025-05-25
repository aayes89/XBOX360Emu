package xbox360emu.UTILS;

import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;
import xbox360emu.PPCEmuConfig;

/**
 *
 * @author Slam
 */
public class Utilities {

    public static void checkAlignment(long address, int alignment) {
        if (address % alignment != 0) {
            throw new RuntimeException(
                    String.format("Unaligned access at 0x%X (alignment: %d)", address, alignment));
        }
    }

    public static int swap16(int value) {
        return ((value & 0xFF) << 8) | ((value >> 8) & 0xFF);
    }

    public static int swap32(int value) {
        return ((value >>> 24) & 0xFF) // byte 3 -> byte 0
                | ((value >>> 8) & 0xFF00) // byte 2 -> byte 1
                | ((value << 8) & 0xFF0000) // byte 1 -> byte 2
                | ((value << 24) & 0xFF000000);  // byte 0 -> byte 3
    }

    public static long swap64(long value) {
        return ((value >>> 56) & 0xFFL) // byte 7 -> byte 0
                | ((value >>> 40) & 0xFF00L) // byte 6 -> byte 1
                | ((value >>> 24) & 0xFF0000L) // byte 5 -> byte 2
                | ((value >>> 8) & 0xFF000000L) // byte 4 -> byte 3
                | ((value << 8) & 0xFF00000000L) // byte 3 -> byte 4
                | ((value << 24) & 0xFF0000000000L) // byte 2 -> byte 5
                | ((value << 40) & 0xFF000000000000L) // byte 1 -> byte 6
                | ((value << 56) & 0xFF00000000000000L); // byte 0 -> byte 7
    }

    // Helpers to convert big endian 32/16
    public static int be32(int v) {
        return ((v >> 24) & 0xFF) | ((v >> 8) & 0xFF00) | ((v << 8) & 0xFF0000) | ((v << 24) & 0xFF000000);
    }

    public static int be16(int v) {
        return (v >> 8) | (v << 8);
    }

    public static long be64(long v) {
        return ((v >> 56) & 0xFFL) | ((v >> 40) & 0xFF00L) | ((v >> 24) & 0xFF0000L)
                | ((v >> 8) & 0xFF000000L) | ((v << 8) & 0xFF00000000L)
                | ((v << 24) & 0xFF0000000000L) | ((v << 40) & 0xFF000000000000L)
                | ((v << 56) & 0xFF00000000000000L);
    }

    // Extraer los bits
    public static int ExtractBits0(int instr, int start, int end) {
        return (instr >>> start) & ((1 << (end - start + 1)) - 1);
    }

    public static int ExtractBits(int instr, int start, int end) {
        int mask = (1 << (end - start + 1)) - 1;
        return (instr >> start) & mask;
    }

    // Manejo de excepciones
    public static void TriggerException(int vector, Registers regs) {
        //regs.setSRR0(regs.getPC()); // Guardar PC actual
        //regs.setSRR1(regs.getMSR()); // Guardar estado de MSR
        regs.setMSR(regs.getMSR() & ~0x8000); // Deshabilitar interrupciones externas (EE=0)
        regs.setPC(vector); // Saltar al vector de excepción

        if (vector == 0x200 || vector == 200) {
            //HandleSyscall();
        }
        System.err.println("CPU Exception triggered, vector=0x%08X" + vector);
    }

    public static int MaskFromMBME(int MB, int ME) {       // Máscara 
        if (MB <= ME) {
            return ((0xFFFFFFFF >> MB) & (0xFFFFFFFF << (31 - ME)));
        } else {
            return ((0xFFFFFFFF >> MB) | (0xFFFFFFFF << (31 - ME)));
        }
    }

    public static int computeMask(int mb, int me) {
        if (mb <= me) {
            return (int) ((0xFFFFFFFFL >>> mb) & (0xFFFFFFFFL << (31 - me)));
        } else {
            int mask1 = (int) (0xFFFFFFFFL >>> mb);
            int mask2 = (int) (0xFFFFFFFFL << (31 - me));
            return mask1 | mask2;
        }
    }

    public static void DCACHE_Store(int addr) {
        if (PPCEmuConfig.verbose_logging_) {
            System.out.println("[DCACHE_Store] addr=0x" + Integer.toHexString(addr) + "\n");
        }
    }

    public static void DCACHE_Flush(int addr) {
        if (PPCEmuConfig.verbose_logging_) {
            System.out.println("[DCACHE_Flush] addr=0x" + Integer.toHexString(addr) + "\n");
        }
    }

    public static void DCACHE_CleanInvalidate(int addr) {
        if (PPCEmuConfig.verbose_logging_) {
            System.out.println("[DCACHE_CleanInvalidate] addr=0x" + Integer.toHexString(addr) + "\n");
        }
    }

    public static int[] rotate(int[] values, int dir) {
        int n = values.length;
        int[] res = new int[n];
        // normalizar dir en [0,n)
        int shift = ((dir % n) + n) % n;
        for (int i = 0; i < n; i++) {
            res[i] = values[(i + shift) % n];
        }
        return res;
    }

    /**
     * LVSL: carga 16 bytes consecutivos a partir de `addr` y rota el vector
     * resultante una posición a la izquierda (–1).
     */
    public static int[] loadVectorShiftLeft(RAM mem, int addr) {
        // Leer 16 bytes de memoria
        int[] vec = new int[16];
        for (int i = 0; i < 16; i++) {
            // cada elemento es el byte sin signo
            vec[i] = Byte.toUnsignedInt(mem.readByte(addr + i));
        }
        // rotar un byte hacia la izquierda
        return rotate(vec, -1);
    }

    /**
     * LVSR / Load Vector Shift Right: igual que LVSL pero rota a la derecha.
     */
    public static int[] loadVectorShiftRight(RAM mem, int addr) {
        int[] vec = new int[16];
        for (int i = 0; i < 16; i++) {
            vec[i] = Byte.toUnsignedInt(mem.readByte(addr + i));
        }
        // rotar un byte hacia la derecha
        return rotate(vec, +1);
    }

    public static void ICACHE_Invalidate(int addr) {
        if (PPCEmuConfig.verbose_logging_) {
            System.out.println("[ICACHE_Invalidate] addr=0x" + Integer.toHexString(addr) + "\n");
        }
    }
}
