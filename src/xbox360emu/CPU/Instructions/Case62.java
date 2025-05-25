
package xbox360emu.CPU.Instructions;

import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;
import xbox360emu.UTILS.Utilities;
/**
 *
 * @author Slam
 */
public class Case62 {

    /**
     * Case 62 subcases:
     *  sub=0: std  RS, D(RA)    (Store Doubleword)
     *  sub=1: stdu RS, D(RA)    (Store Doubleword with Update)
     */
    public static void case_62(int instr, Registers regs, RAM mem) {
        int sub = Utilities.ExtractBits(instr, 30, 31);
        int rs  = Utilities.ExtractBits(instr, 6, 10);
        int ra  = Utilities.ExtractBits(instr, 11, 15);
        short D = (short)(instr & 0xFFFF);      // desplazamiento con signo

        int base = (ra == 0 ? 0 : regs.getGPR(ra));
        int ea   = base + D;

        // Alineamiento a 8 bytes
        if (ea % 8 != 0) {
            throw new RuntimeException(
                String.format("Unaligned %s at address 0x%08X",
                    (sub == 1 ? "stdu" : "std"), ea));
        }

        // Construir el valor de 64 bits: high=GPR[rs], low=GPR[rs+1]
        long high = ((long)regs.getGPR(rs)) & 0xFFFFFFFFL;
        long low  = ((long)regs.getGPR(rs + 1)) & 0xFFFFFFFFL;
        long value = (high << 32) | low;

        // Escribir en memoria
        mem.writeDoubleWord(ea, value);

        if (sub == 1 && ra != 0) { // stdu: actualizar RA salvo si es r0
            regs.setGPR(ra, ea);
        }
    }
}
