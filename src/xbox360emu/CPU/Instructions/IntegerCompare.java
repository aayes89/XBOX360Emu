package xbox360emu.CPU.Instructions;

import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;
import xbox360emu.UTILS.Utilities;

/**
 *
 * @author Slam
 */
public class IntegerCompare {

    // cmpwi rA, SIMM → CR0 = compare(rA, SIMM)
    public static void cmpwi(int instr, Registers regs, RAM mem) {
        int ra = (instr >> 16) & 0x1F;
        int simm = (short) (instr & 0xFFFF);
        int a = regs.getGPR(ra);

        int crf = 0; // solo CR0 aquí
        if (a < simm) {
            crf = 0b100;
        } else if (a > simm) {
            crf = 0b010;
        } else {
            crf = 0b001;
        }

        regs.setCRField(0, crf); // CR0 ← resultado
    }

    public static void cmpli(int instr, Registers regs, RAM mem) { //10 cmpli crfD, rA, UI
        int crfD = Utilities.ExtractBits(instr, 6, 8);
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int ui = instr & 0xFFFF;
        boolean lt = regs.getGPR(ra) < ui;
        boolean gt = regs.getGPR(ra) > ui;
        boolean eq = regs.getGPR(ra) == ui;
        regs.setCRField(0, (lt ? 8 : 0) | (gt ? 4 : 0) | (eq ? 2 : 0) | 0);

    }

    public static void cmpi(int instr, Registers regs, RAM mem) { //11 cmpi crfD, rA, SI
        int crfD = Utilities.ExtractBits(instr, 6, 8);
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int si = instr & 0xFFFF;
        int v = regs.getGPR(ra) - si;
        boolean lt = v < 0;
        boolean gt = v > 0;
        boolean eq = v == 0;
        regs.setCRField(0, (lt ? 8 : 0) | (gt ? 4 : 0) | (eq ? 2 : 0) | 0);

    }

}
