package xbox360emu.CPU.Instructions;

import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;
import xbox360emu.UTILS.Utilities;

/**
 *
 * @author Slam
 */
public class Case58 {

    /**
     * Opcodes 58 subcase: - sub=0: ld RT, D(RA) - sub=1: ldu RT, D(RA) + update
     * RA - sub=2: lwa RT, D(RA) + update LR
     */
    public static void case_58(int instr, Registers regs, RAM mem) {
        int sub = Utilities.ExtractBits(instr, 30, 31);
        int rt = Utilities.ExtractBits(instr, 6, 10);
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int D = (short) (instr & 0xFFFF);           // signed 16-bit offset
        int base = (ra == 0 ? 0 : regs.getGPR(ra));
        int ea = base + D;

        // Validar alineamiento a 8 bytes
        if (ea % 8 != 0) {
            throw new RuntimeException(
                    String.format("Unaligned %s at address 0x%08X",
                            (sub == 1 ? "ldu" : sub == 2 ? "lwa" : "ld"), ea));
        }

        long w = mem.readDoubleWord(ea);

        // Parte alta y baja del dobleword
        int hi = (int) (w >>> 32);
        int lo = (int) w;

        // Escribe en RT y RT+1
        regs.setGPR(rt, hi);
        regs.setGPR(rt + 1, lo);

        switch (sub) {
            case 1: // ldu: además actualiza RA
                if (ra != 0) {
                    regs.setGPR(ra, ea);
                }
                break;
            case 2: // lwa: además actualiza el Link Register (LR)
                regs.setLR(ea);
                break;
            // case 0: solo ld, nada más
        }
    }
}
