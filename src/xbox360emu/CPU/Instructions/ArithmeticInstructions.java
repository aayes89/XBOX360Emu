package xbox360emu.CPU.Instructions;

/**
 *
 * @author Slam
 */
import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;
import xbox360emu.PPCEmuConfig;
import xbox360emu.UTILS.Utilities;

public class ArithmeticInstructions {

    // addi rA, rS, SIMM → rA = rS + SIMM
    public static void addi0(int instr, Registers regs, RAM mem) {
        int rD = (instr >> 21) & 0x1F;  // destino
        int rA = (instr >> 16) & 0x1F;  // fuente/base
        short simm = (short) (instr & 0xFFFF);
        int base = (rA == 0) ? 0 : regs.getGPR(rA);
        regs.setGPR(rD, base + simm);

        // Para el log, si imm<0 imprime “subi”
        if (PPCEmuConfig.verbose_logging_) {
            if (simm < 0) {
                System.out.printf("Executing [subi] r%d = r%d + (%d) → 0x%08X%n",
                        rD, rA, simm, (base + simm));
            } else {
                System.out.printf("Executing [addi] r%d = r%d + %d → 0x%08X%n",
                        rD, rA, simm, (base + simm));
            }
        }
    }

    public static void addi(int instr, Registers regs, RAM mem) {
        int rt = (instr >> 21) & 0x1F; // Registro destino
        int ra = (instr >> 16) & 0x1F; // Registro base
        int imm = (short) (instr & 0xFFFF); // Inmediato (sign-extended)

        int result = (ra == 0) ? imm : (regs.getGPR(ra) + imm);
        regs.setGPR(rt, result);

        System.out.printf("addi: rt=%d, ra=%d, imm=0x%X, result=0x%X\n", rt, ra, imm, result);
    }

    public static void subf(int instr, Registers regs, RAM mem) {
        int rt = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        int rb = (instr >> 11) & 0x1F;
        regs.setGPR(rt, regs.getGPR(rb) - regs.getGPR(ra));
    }

    // lis rD, IMM → rD = IMM << 16
    public static void lis(int instr, Registers regs, RAM mem) {
        int rd = (instr >> 21) & 0x1F;
        int imm = instr & 0xFFFF;
        if ((imm & 0x8000) != 0) {
            imm |= 0xFFFF0000; // signo extendido
        }
        regs.setGPR(rd, imm << 16);
    }

    // ori rA, rS, UIMM → rA = rS | (UIMM & 0xFFFF)
    public static void ori0(int instr, Registers regs, RAM mem) {
        int rs = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        int uimm = instr & 0xFFFF;
        int result = regs.getGPR(rs) | (uimm & 0xFFFF);
        regs.setGPR(ra, result);
    }

    public static void ori(int instr, Registers regs, RAM mem) {
        int rs = (instr >> 21) & 0x1F; // Registro fuente
        int ra = (instr >> 16) & 0x1F; // Registro destino
        int imm = instr & 0xFFFF; // Inmediato (zero-extended)

        int result = regs.getGPR(rs) | imm;
        regs.setGPR(ra, result);

        // Depuración
        System.out.printf("ori: rs=%d, ra=%d, imm=0x%X, result=0x%X\n", rs, ra, imm, result);
    }

    public static void mulli(int instr, Registers regs, RAM mem) {
        //7 mulli rA, rS, SI
        int rt = Utilities.ExtractBits(instr, 6, 10);
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int si = instr & 0xFFFF;
        int prod = regs.getGPR(ra) * si;
        regs.setGPR(rt, prod);
    }

    public static void subficx(int instr, Registers regs, RAM mem) {
        //8 subficx rA, rS, UI
        int rt = Utilities.ExtractBits(instr, 6, 10);
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int ui = instr & 0xFFFF;
        int val = ui - regs.getGPR(ra);
        regs.setGPR(rt, val);
    }

    public static void addic(int instr, Registers regs, RAM mem) {
//12 addic rA, rS, SI
        int rt = Utilities.ExtractBits(instr, 6, 10);
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int si = instr & 0xFFFF;
        int sum = regs.getGPR(ra) + si;
        regs.setGPR(rt, sum);
        regs.setXER((sum < regs.getGPR(ra)) ? (regs.getXER() | 0x02) : (regs.getXER() & ~0x02));
    }

    public static void addicx(int instr, Registers regs, RAM mem) {// 13 addicx rA, rS, SI with carry
        int rt = Utilities.ExtractBits(instr, 6, 10);
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int si = instr & 0xFFFF;
        int carry = (regs.getXER() >> 2) & 1;
        int sum = regs.getGPR(ra) + si + carry;
        regs.setGPR(rt, sum);
        regs.setXER((sum < regs.getGPR(ra)) ? (regs.getXER() | 0x02) : (regs.getXER() & ~0x02));
        //regs.setXER((sum >> 32) ? (regs.getXER() | 0x02) : (regs.getXER() & ~0x02));
    }

}
