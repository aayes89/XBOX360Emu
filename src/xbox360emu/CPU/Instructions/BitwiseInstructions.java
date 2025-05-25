
package xbox360emu.CPU.Instructions;

/**
 *
 * @author Slam
 */

import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;
import xbox360emu.UTILS.Utilities;

/**
 * Instrucciones de rotación y enmascarado de palabra:
 * rlwimi, rlwinm, rlwnm.
 */
public class BitwiseInstructions {

    /**
     * rlwimi: Rotate Left Word Immediate then Mask Insert
     * Opcode = 20
     * @param instr
     * @param regs
     * @param mem
     */
    public static void rlwimi(int instr, Registers regs, RAM mem) {
        int rt = Utilities.ExtractBits(instr, 6, 10);
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int sh = Utilities.ExtractBits(instr, 16, 20);
        int mb = Utilities.ExtractBits(instr, 21, 25);
        int me = Utilities.ExtractBits(instr, 26, 30);

        int val  = regs.getGPR(ra);
        int res  = Integer.rotateLeft(val, sh);
        int mask = Utilities.computeMask(mb, me);

        int orig = regs.getGPR(rt);
        regs.setGPR(rt, (res & mask) | (orig & ~mask));
    }

    /**
     * rlwinm: Rotate Left Word Immediate then Mask
     * Opcode = 21
     * @param instr
     * @param regs
     * @param mem
     */
    public static void rlwinm(int instr, Registers regs, RAM mem) {
        int rt = Utilities.ExtractBits(instr, 6, 10);
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int sh = Utilities.ExtractBits(instr, 16, 20);
        int mb = Utilities.ExtractBits(instr, 21, 25);
        int me = Utilities.ExtractBits(instr, 26, 30);

        int val  = regs.getGPR(ra);
        int res  = Integer.rotateLeft(val, sh);
        int mask = Utilities.computeMask(mb, me);

        regs.setGPR(rt, res & mask);
    }

    /**
     * rlwnm: Rotate Left Word then Mask with XOR
     * Opcode = 23
     * @param instr
     * @param regs
     * @param mem
     */
    public static void rlwnm(int instr, Registers regs, RAM mem) {
        int rt = Utilities.ExtractBits(instr, 6, 10);
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int rb = Utilities.ExtractBits(instr, 16, 20);
        int sh = Utilities.ExtractBits(instr, 21, 25);
        int mb = Utilities.ExtractBits(instr, 26, 30);

        int val  = regs.getGPR(ra) ^ regs.getGPR(rb);
        int res  = Integer.rotateLeft(val, sh);
        // rlwnm mask semantics: window starting at MB, length = SH+1
        int me   = (mb + sh) & 0x1F;
        int mask = Utilities.computeMask(mb, me);

        regs.setGPR(rt, res & mask);
    }
}
