package xbox360emu.CPU.Instructions;

import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;
import xbox360emu.UTILS.Utilities;

/**
 *
 * @author Slam
 */

public class Case59 {

    /**
     * Case 59: instrucciones de simulación de punto flotante simple:
     * sub = ExtractBits(instr, 26, 30):
     *   18 fdivsx   20 fsubsx   21 faddsx
     *   22 fsqrtsx  24 fresx    25 fmulsx
     *   28 fmsubsx  29 fmaddsx  30 fnmsubsx  31 fnmaddsx
     */
    public static void case_59(int instr, Registers regs, RAM mem) {
        int sub = Utilities.ExtractBits(instr, 26, 30);
        int ft  = Utilities.ExtractBits(instr, 6, 10);
        int fa  = Utilities.ExtractBits(instr, 11, 15);
        int fb  = Utilities.ExtractBits(instr, 16, 20);

        float A = (float)regs.getFPR(fa);
        float B = (float)regs.getFPR(fb);
        float R = (float)regs.getFPR(ft);  // necesario para casos con acumulación

        switch (sub) {
            case 18: // fdivsx: R = A / B
                regs.putFPR(ft, A / B);
                break;

            case 20: // fsubsx: R = A - B
                regs.putFPR(ft, A - B);
                break;

            case 21: // faddsx: R = A + B
                regs.putFPR(ft, A + B);
                break;

            case 22: // fsqrtsx: R = sqrt(A)
                regs.putFPR(ft, (float)Math.sqrt(A));
                break;

            case 24: // fresx: R = 1.0f / B
                regs.putFPR(ft, 1.0f / B);
                break;

            case 25: // fmulsx: R = A * B
                regs.putFPR(ft, A * B);
                break;

            case 28: // fmsubsx: R = A * B - R
                regs.putFPR(ft, A * B - R);
                break;

            case 29: // fmaddsx: R = A * B + R
                regs.putFPR(ft, A * B + R);
                break;

            case 30: // fnmsubsx: R = -(A * B) - R
                regs.putFPR(ft, -(A * B) - R);
                break;

            case 31: // fnmaddsx: R = -(A * B) + R
                regs.putFPR(ft, -(A * B) + R);
                break;

            default:
                // sub no soportado: ignora
                break;
        }
    }
}
