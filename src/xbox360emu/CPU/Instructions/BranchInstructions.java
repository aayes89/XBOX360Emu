package xbox360emu.CPU.Instructions;


/*
* @author Slam
 */
import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;
import xbox360emu.Exceptions.HaltException;

public class BranchInstructions {

    /**
     * Instrucción B (opcode 18) — salto incondicional, halt si offset==0
     */
    public static void b0(int instr, Registers regs, RAM mem) {
        int li = instr & 0x03FFFFFC;  // Bits 6-29, desplazados a la derecha por 2 bits
        int disp = (li << 6) >> 6;    // Extensión de signo para 24 bits
        disp <<= 2;                   // Multiplicar por 4 para obtener el desplazamiento en bytes

        boolean aa = (instr & 0x02) != 0;  // Bit 30
        boolean lk = (instr & 0x01) != 0;  // Bit 31

        int nextPC = regs.getPC();  // PC ya incrementado por step()
        int target = aa ? disp : nextPC + disp;

        // Halt si es un salto a sí mismo (offset == 0 y aa == 0)
        if (disp == 0 && !aa) {
            throw new HaltException();
        }

        if (lk) {
            regs.setLR(nextPC);
        }

        regs.setPC(target & ~0x3);  // Alinear a múltiplo de 4
    }

    public static void b(int instr, Registers regs, RAM mem) {
        // Extraer LI (bits 6-29)
        int li = (instr & 0x03FFFFFC);
        // Extender el signo de 24 bits a 32 bits
        if ((li & 0x00800000) != 0) { // Bit 23 (bit de signo en 24 bits)
            li |= 0xFF000000; // Extender el signo con los bits superiores
        }
        int disp = li << 2; // Desplazamiento en bytes

        boolean aa = (instr & 0x02) != 0;  // Bit 30
        boolean lk = (instr & 0x01) != 0;  // Bit 31

        int nextPC = regs.getPC() + 4; // PC de la instrucción siguiente (0x10018)
        int target = aa ? disp : (nextPC + disp) & 0xFFFFFFFF; // Salto relativo

        // Ajuste para b -4: el desplazamiento debe apuntar a 0x10010
        if (aa) {
            target = (nextPC - 8) & 0xFFFFFFFF; // 0x10018 - 8 = 0x10010
        }

        // Depuración
        System.out.printf("b instruction: nextPC=0x%X, li=0x%X, disp=0x%X, target=0x%X\n", nextPC, li, disp, target);

        // Halt si es un salto a sí mismo (offset == 0 y aa == 0)
        if (disp == 0 && !aa) {
            throw new HaltException();
        }

        if (lk) {
            regs.setLR(nextPC);
        }

        regs.setPC(target & ~0x3);  // Alinear a múltiplo de 4
    }

    /**
     * BC (opcode 16): Branch Conditional
     */
    public static void bc(int instr, Registers regs, RAM mem) {
        int bo = (instr >>> 21) & 0x1F;  // Bits 6-10
        int bi = (instr >>> 16) & 0x1F;  // Bits 11-15

        int bd = instr & 0xFFFC;         // Bits 16-29
        int disp = (bd << 16) >> 16;     // Extensión de signo para 14 bits
        disp <<= 2;                      // Multiplicar por 4

        boolean aa = (instr & 0x02) != 0;  // Bit 30
        boolean lk = (instr & 0x01) != 0;  // Bit 31

        int nextPC = regs.getPC();
        int target = aa ? disp : nextPC + disp;

        boolean conditionMet = evaluateCondition(bo, bi, regs);
        boolean ctrOk = checkCounter(bo, regs);

        if (conditionMet && ctrOk) {
            if (lk) {
                regs.setLR(nextPC);
            }
            regs.setPC(target & ~0x3);
        }
    }

    /**
     * Instrucción BCLR/BCCTR/RFI (opcode 19)
     */
    public static void bcl(int instr, Registers regs, RAM mem) {
        int xo = (instr >>> 1) & 0x3FF;  // Bits 21-30
        boolean lk = (instr & 0x01) != 0;  // Bit 31
        int bo = (instr >>> 21) & 0x1F;  // Bits 6-10
        int bi = (instr >>> 16) & 0x1F;  // Bits 11-15

        int nextPC = regs.getPC();

        switch (xo) {
            case 16:  // BCLR
                if (evaluateCondition(bo, bi, regs) && checkCounter(bo, regs)) {
                    if (lk) {
                        regs.setLR(nextPC);
                    }
                    regs.setPC(regs.getLR() & ~0x3);
                }
                break;

            case 528:  // BCCTR
                if (evaluateCondition(bo, bi, regs) && checkCounter(bo, regs)) {
                    if (lk) {
                        regs.setLR(nextPC);
                    }
                    regs.setPC(regs.getCTR() & ~0x3);
                }
                break;

            case 50:  // RFI
                regs.setPC(regs.getSRR0() & ~0x3);
                regs.setMSR(regs.getSRR1());
                break;

            default:
                throw new IllegalArgumentException("Instrucción no soportada para XO=" + xo);
        }
    }

    private static boolean evaluateCondition(int bo, int bi, Registers regs) {
        boolean bo0 = (bo & 0x10) != 0;  // BO[0]: ignore CR
        boolean bo1 = (bo & 0x08) != 0;  // BO[1]: branch if condition true
        boolean bo2 = (bo & 0x04) != 0;  // BO[2]: check CTR
        boolean bo3 = (bo & 0x02) != 0;  // BO[3]: branch if CTR != 0
        boolean bo4 = (bo & 0x01) != 0;  // BO[4]: decrement CTR

        boolean conditionMet = true;
        if (!bo0) {  // Si BO[0] == 0, evaluar CR
            int crField = regs.getCRField(bi / 4);
            int crBit = (crField >>> (3 - (bi % 4))) & 1;
            conditionMet = (crBit != 0) == bo1;
        }

        return conditionMet;
    }

    private static boolean checkCounter(int bo, Registers regs) {
        boolean bo2 = (bo & 0x04) != 0;  // BO[2]: check CTR
        boolean bo3 = (bo & 0x02) != 0;  // BO[3]: branch if CTR != 0
        boolean bo4 = (bo & 0x01) != 0;  // BO[4]: decrement CTR

        if (!bo2) {
            return true;  // No check CTR
        }

        if (bo4) {
            regs.setCTR(regs.getCTR() - 1);
        }

        int ctr = regs.getCTR();
        return (ctr != 0) == bo3;
    }
}
