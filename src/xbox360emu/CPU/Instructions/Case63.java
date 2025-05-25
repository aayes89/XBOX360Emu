package xbox360emu.CPU.Instructions;

import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;
import xbox360emu.UTILS.Utilities;

/**
 *
 * @author Slam
 */
public class Case63 {

    public static void case_63(int instr, Registers regs, RAM mem) {
        // Extraer campos del opcode
        int sub1 = Utilities.ExtractBits(instr, 21, 30);
        int sub2 = Utilities.ExtractBits(instr, 26, 30);
        int ft = Utilities.ExtractBits(instr, 6, 10);
        int fb = Utilities.ExtractBits(instr, 16, 20);
        int faIdx = Utilities.ExtractBits(instr, 11, 15);

        // Recuperar operandos
        double FA = regs.getFPR(faIdx);
        double FB = regs.getFPR(fb);
        //double DA = regs.getFPR(faIdx);
        //double DB = regs.getFPR(fb);

        switch (sub1) {
            case 0:   // fcmpu
                // cr0: LT=8, GT=4, EQ=2
                regs.setCRValue(
                        ((FA < FB) ? 8 : 0)
                        | ((FA > FB) ? 4 : 0)
                        | ((FA == FB) ? 2 : 0));
                break;

            case 12:  // frspx: round single precision
                regs.putFPR(ft, FA);
                break;

            case 14:  // fctiwx: truncate toward zero, result in single
            case 15:  // fctiwzx: same as above for unsigned
                regs.putFPR(ft, (float) Math.floor(FA));
                break;

            case 32:  // fcmpo (identical a fcmpu excepto por señalización)
                regs.setCRValue(
                        ((FA < FB) ? 8 : 0)
                        | ((FA > FB) ? 4 : 0)
                        | ((FA == FB) ? 2 : 0));
                break;

            case 38:  // mtfsb1x: set bit in FPSCR from CR0[LT]
                boolean lt = ((regs.getCTR() >> 31) & 1) != 0;
                if (lt) {
                    regs.setFPSCR(31);/* bit position = LT */
                }
                break;

            case 40:  // fnegx
                regs.putFPR(ft, -FA);
                break;

            case 64:  // mcrfs: move from FPSCR to CR
                int crm = faIdx;
                regs.setCTR(regs.getFPSCR());//.moveCRField(crm, regs.getCR().value);
                break;

            case 70:  // mtfsb0x: clear bit in FPSCR from CR
                crm = faIdx;
                if (((regs.getFPSCR() >> ((7 - crm) * 4 + 2)) & 1) != 0) {
                    regs.setFPSCR(/* bit = EQ pos */(7 - crm) * 4 + 2);
                }
                break;

            case 72:  // fmrx: move FPSCR to FPR
                float fpscrLo = (float) regs.getFPSCR();
                regs.putFPR(ft, fpscrLo);
                break;

            case 134: // mtfsfix
                regs.setFPSCR(regs.getGPR(Utilities.ExtractBits(instr, 6, 10)));
                break;

            case 136: // fnabsx: absolute of single neg FA
                regs.putFPR(ft, Math.abs(-FA));
                break;

            case 264: // fabsx: absolute of single
                regs.putFPR(ft, Math.abs(FA));
                break;

            case 583: // mffsx: move from FPSCR to FPR single
                regs.putFPR(ft, (float) regs.getFPSCR());
                break;

            case 711: // mtfsfx
                regs.setFPSCR(regs.getGPR(Utilities.ExtractBits(instr, 6, 10)));
                break;

            case 814: // fctidx
            case 815: // fctidzx
            case 846: // fcfidx
                regs.putFPR(ft, (float) Math.floor(FA));
                break;

            default:
                // nada para otros sub1
                break;
        }

        switch (sub2) {
            case 18:  // fdivx
                regs.putFPR(ft, FA / FB);
                break;
            case 20:  // fsubx
                regs.putFPR(ft, FA - FB);
                break;
            case 21:  // faddx
                regs.putFPR(ft, FA + FB);
                break;
            case 22:  // fsqrtx
                regs.putFPR(ft, (float) Math.sqrt(FA));
                break;
            case 23:  // fselx
                regs.putFPR(ft, (FA >= 0 ? FA : -FA));
                break;
            case 25:  // fmulx
                regs.putFPR(ft, FA * FB);
                break;
            case 26:  // frsqrtex
                regs.putFPR(ft, 1.0f / (float) Math.sqrt(FA));
                break;
            case 28:  // fmsubx
                regs.putFPR(ft, FA * FB - regs.getFPR(ft));
                break;
            case 29:  // fmaddx
                regs.putFPR(ft, FA * FB + regs.getFPR(ft));
                break;
            case 30:  // fnmsubx
                regs.putFPR(ft, -(FA * FB) - regs.getFPR(ft));
                break;
            case 31:  // fnmaddx
                regs.putFPR(ft, -(FA * FB) + regs.getFPR(ft));
                break;
            default:
                // nada para otros sub2
                break;
        }
    }

}
