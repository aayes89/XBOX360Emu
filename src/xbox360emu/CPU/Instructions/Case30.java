package xbox360emu.CPU.Instructions;

import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;
import xbox360emu.UTILS.Utilities;

/**
 *
 * @author Slam
 */
public class Case30 {

    public static void rl_case30(int instr, Registers regs, RAM mem) { // 30
        switch (Utilities.ExtractBits(instr, 27, 29)) {
            case 0: { // rldiclx
                int rt = Utilities.ExtractBits(instr, 6, 10), ra = Utilities.ExtractBits(instr, 11, 15);
                int sh = Utilities.ExtractBits(instr, 21, 25);
                int val = regs.getGPR(ra);
                regs.setGPR(rt, val << sh);
                break;
            }
            case 1: { // rldicrx
                int rt = Utilities.ExtractBits(instr, 6, 10), ra = Utilities.ExtractBits(instr, 11, 15);
                int sh = Utilities.ExtractBits(instr, 21, 25);
                int val = regs.getGPR(ra);
                regs.setGPR(rt, (val >> sh));
                break;
            }
            case 2: { // rldicx
                int rt = Utilities.ExtractBits(instr, 6, 10), ra = Utilities.ExtractBits(instr, 11, 15);
                int sh = Utilities.ExtractBits(instr, 21, 25);
                int val = regs.getGPR(ra);
                regs.setGPR(rt, (val << sh) | (val >> (32 - sh)));
                break;
            }
            case 3: { // rldimix
                int rt = Utilities.ExtractBits(instr, 6, 10), ra = Utilities.ExtractBits(instr, 11, 15);
                int sh = Utilities.ExtractBits(instr, 21, 25);
                int mb = Utilities.ExtractBits(instr, 16, 20);
                int val = regs.getGPR(ra);
                int rot = (val << sh) | (val >> (32 - sh));
                int mask = Utilities.MaskFromMBME(mb, mb + sh);
                regs.setGPR(rt, (rot & mask) | (regs.getGPR(rt) & ~mask));
                break;
            }
        }
        switch (Utilities.ExtractBits(instr, 27, 30)) {
            case 8: { // rldclx
                int rt = Utilities.ExtractBits(instr, 6, 10), ra = Utilities.ExtractBits(instr, 11, 15);
                int mb = Utilities.ExtractBits(instr, 21, 25);
                int val = regs.getGPR(ra);
                int mask = Utilities.MaskFromMBME(mb, 31);
                regs.setGPR(rt, val & mask);
                break;
            }
            case 9: { // rldcrx
                int rt = Utilities.ExtractBits(instr, 6, 10), ra = Utilities.ExtractBits(instr, 11, 15);
                int me = Utilities.ExtractBits(instr, 21, 25);
                int val = regs.getGPR(ra);
                int mask = Utilities.MaskFromMBME(0, me);
                regs.setGPR(rt, val & mask);
                break;
            }
        }
        // PPC_DECODER_MISS;
    }

}
