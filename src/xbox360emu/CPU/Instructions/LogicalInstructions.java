package xbox360emu.CPU.Instructions;

/**
 *
 * @author Slam
 */
import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;
import xbox360emu.UTILS.Utilities;

public class LogicalInstructions {

    // Registro a registro
    public static void and(int instr, Registers regs, RAM mem) {
        int rs = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        int rb = (instr >> 11) & 0x1F;
        regs.setGPR(ra, regs.getGPR(rs) & regs.getGPR(rb));
    }

    // Registro con inmediato (ORI) 24
    public static void ori(int instr, Registers regs, RAM mem) {
        int rs = (instr >> 21) & 0x1F; // Registro fuente
        int ra = (instr >> 16) & 0x1F; // Registro destino
        int imm = instr & 0xFFFF; // Inmediato (zero-extended)

        int result = regs.getGPR(rs) | imm;
        regs.setGPR(ra, result);

        // Depuración
        System.out.printf("ori: rs=%d, ra=%d, imm=0x%X, result=0x%X\n", rs, ra, imm, result);
    }

    // Opcional: instrucción OR clásica (registro a registro)
    public static void orInstr(int instr, Registers regs, RAM mem) {
        int rs = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        int rb = (instr >> 11) & 0x1F;
        regs.setGPR(ra, regs.getGPR(rs) | regs.getGPR(rb));
    }

    public static void oris(int instr, Registers regs, RAM mem) { // oris 25
        int rs = Utilities.ExtractBits(instr, 6, 10), ra = Utilities.ExtractBits(instr, 11, 15);
        int imm = instr & 0xFFFF;
        regs.setGPR(ra, regs.getGPR(rs) | (int) (imm << 16));
    }

    public static void xori(int instr, Registers regs, RAM mem) { // xori 26
        int rs = Utilities.ExtractBits(instr, 6, 10), ra = Utilities.ExtractBits(instr, 11, 15);
        int imm = instr & 0xFFFF;
        regs.setGPR(ra, regs.getGPR(rs) ^ imm);
    }

    public static void xoris(int instr, Registers regs, RAM mem) { // xoris 27
        int rs = Utilities.ExtractBits(instr, 6, 10), ra = Utilities.ExtractBits(instr, 11, 15);
        int imm = instr & 0xFFFF;
        regs.setGPR(ra, regs.getGPR(rs) ^ (int) imm << 16);

    }

    public static void andix(int instr, Registers regs, RAM mem) { // andix 28
        int rs = Utilities.ExtractBits(instr, 6, 10), ra = Utilities.ExtractBits(instr, 11, 15);
        int imm = instr & 0xFFFF;
        regs.setGPR(ra, regs.getGPR(ra) & ~imm);
    }

    public static void andisx(int instr, Registers regs, RAM mem) { // andisx 29
        int rs = Utilities.ExtractBits(instr, 6, 10), ra = Utilities.ExtractBits(instr, 11, 15);
        int imm = instr & 0xFFFF;
        regs.setGPR(ra, regs.getGPR(ra) & ~(int) (imm << 16));
    }
}
