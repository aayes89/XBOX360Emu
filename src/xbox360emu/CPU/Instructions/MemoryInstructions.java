package xbox360emu.CPU.Instructions;

/**
 *
 * @author Slam
 */
import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;
import xbox360emu.UTILS.Utilities;

public class MemoryInstructions {

    // lwz rD, D(rA) → rD = MEM[rA + D]
    public static void lwz(int instr, Registers regs, RAM mem) {
        int rt = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        short d = (short) (instr & 0xFFFF); // sign-extend
        int base = (ra == 0) ? 0 : regs.getGPR(ra);
        int addr = base + d;

        if (addr < 0 || addr >= mem.getSize()) {
            System.err.printf("lwz: acceso inválido a dirección 0x%08X (r%d=0x%08X, d=%d)\n", addr, ra, base, d);
            throw new IndexOutOfBoundsException(String.format("Invalid address 0x%08X in lwz", addr));
        }

        if (addr % 4 != 0) {
            throw new RuntimeException(String.format("Unaligned lwz at address 0x%08X", addr));
        }

        regs.setGPR(rt, mem.readWord(addr));
    }

    // stw rS, D(rA) → MEM[rA + D] = rS
    public static void stw0(int instr, Registers regs, RAM mem) {
        int rs = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        short d = (short) (instr & 0xFFFF);
        int base = (ra == 0) ? 0 : regs.getGPR(ra);
        int addr = base + d;
        if (addr % 4 != 0) {
            throw new RuntimeException(String.format("Unaligned stw at address 0x%08X", addr));
        }
        mem.writeWord(addr, regs.getGPR(rs));
    }

    public static void stw(int instr, Registers regs, RAM mem) {
        int rt = (instr >> 21) & 0x1F; // Registro fuente
        int ra = (instr >> 16) & 0x1F; // Registro base
        int d = (short) (instr & 0xFFFF); // Desplazamiento inmediato

        int ea = (ra == 0) ? d : (regs.getGPR(ra) + d); // Effective address
        int value = regs.getGPR(rt) & 0xFFFFFF00; // Forzar alfa a 0x00

        // Depuración
        System.out.printf("stw: rt=%d, ra=%d, d=0x%X, ea=0x%X, value=0x%08X\n", rt, ra, d, ea, value);

        mem.writeWord(ea, value);
    }

    public static void sc(int instr, Registers regs, RAM mem) {
        //case 17: { // sc
        Utilities.TriggerException(0x200, regs);
    }

    public static void lhz(int instr, Registers regs, RAM mem) { // case 22 LHZ
        int rD = (instr >> 21) & 0x1F;
        int rA = (instr >> 16) & 0x1F;
        int d = instr & 0xFFFF;
        int ea = (rA == 0 ? 0 : regs.getGPR(rA)) + d;
        regs.setGPR(rD, mem.readHalfWord(ea));
    }

    public static void stwu(int instr, Registers regs, RAM mem) { // stwu 37
        int rs = Utilities.ExtractBits(instr, 6, 10), ra = Utilities.ExtractBits(instr, 11, 15);
        int D = instr & 0xFFFF;
        int addr = regs.getGPR(ra) + D;
        mem.writeWord(addr, regs.getGPR(rs));
        regs.setGPR(ra, addr);
    }

    public static void lhz1(int instr, Registers regs, RAM mem) { // lhz 40
        int rt = Utilities.ExtractBits(instr, 6, 10);
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int D = instr & 0xFFFF;
        regs.setGPR(rt, mem.readHalfWord(regs.getGPR(ra) + D));
    }

    public static void lhzu(int instr, Registers regs, RAM mem) { // lhzu 41
        int rt = Utilities.ExtractBits(instr, 6, 10), ra = Utilities.ExtractBits(instr, 11, 15);
        int D = instr & 0xFFFF;
        int addr = regs.getGPR(ra) + D;
        regs.setGPR(rt, mem.readWord(addr));
        regs.setGPR(ra, addr);

    }

    public static void lmw(int instr, Registers regs, RAM mem) { // lmw rt,..,d(ra) 46
        int rt = Utilities.ExtractBits(instr, 6, 10), ra = Utilities.ExtractBits(instr, 11, 15);
        int D = instr & 0xFFFF;
        int base = regs.getGPR(ra) + D;
        for (int i = rt; i < 32; i++) {
            regs.setGPR(i, mem.readWord(base + 4 * (i - rt)));
        }
    }

    public static void stmw(int instr, Registers regs, RAM mem) { // stmw rs,..,d(ra) 47
        int rs = Utilities.ExtractBits(instr, 6, 10), ra = Utilities.ExtractBits(instr, 11, 15);
        int D = instr & 0xFFFF;
        int base = regs.getGPR(ra) + D;
        for (int i = rs; i < 32; i++) {
            mem.writeWord(base + 4 * (i - rs), regs.getGPR(i));
        }
    }

    public static void lfsu(int instr, Registers regs, RAM mem) { // lfsu 49
        int ft = Utilities.ExtractBits(instr, 6, 10), ra = Utilities.ExtractBits(instr, 11, 15);
        int D = instr & 0xFFFF;
        int addr = regs.getGPR(ra) + D;
        int w = mem.readWord(addr);
        float f = w;
        //Utilities.memcpy( & f,  & w, 4);
        regs.putFPR(ft, f);
        regs.setGPR(ra, addr);

    }

    public static void stfsu(int instr, Registers regs, RAM mem) { // stfsu 53
        int ft = Utilities.ExtractBits(instr, 6, 10), ra = Utilities.ExtractBits(instr, 11, 15);
        int D = instr & 0xFFFF;
        int addr = regs.getGPR(ra) + D;
        double f = regs.getFPR(ft);
        int w = (int) f;
        //Utilities.memcpy( & w,  & f, 4);
        mem.writeWord(addr, w);
        regs.setGPR(ra, addr);

    }

    // lwzu rS, D(rA): load word and update (w/ alignment and rA update) 33
    public static void lwzu(int instr, Registers regs, RAM mem) {
        int rt = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        short d = (short) (instr & 0xFFFF);
        int base = (ra == 0 ? 0 : regs.getGPR(ra));
        int ea = base + d;
        if (ea % 4 != 0) {
            throw new RuntimeException(String.format("Unaligned lwzu at address 0x%08X", ea));
        }
        int value = mem.readWord(ea);
        regs.setGPR(rt, value);
        if (ra != 0) {
            regs.setGPR(ra, ea);
        }
    }

    // lbz rD, D(rA): load byte and zero extend 34
    public static void lbz(int instr, Registers regs, RAM mem) {
        int rt = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        short d = (short) (instr & 0xFFFF);
        int base = (ra == 0 ? 0 : regs.getGPR(ra));
        int ea = base + d;
        byte b = mem.readByte(ea);
        regs.setGPR(rt, Byte.toUnsignedInt(b));
    }

    // lbzu rD, D(rA): load byte, zero extend, update rA 35
    public static void lbzu(int instr, Registers regs, RAM mem) {
        int rt = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        short d = (short) (instr & 0xFFFF);
        int base = (ra == 0 ? 0 : regs.getGPR(ra));
        int ea = base + d;
        byte b = mem.readByte(ea);
        regs.setGPR(rt, Byte.toUnsignedInt(b));
        if (ra != 0) {
            regs.setGPR(ra, ea);
        }
    }

    // lha rD, D(rA): load halfword algebraic (sign-extend) 42
    public static void lha(int instr, Registers regs, RAM mem) {
        int rt = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        short d = (short) (instr & 0xFFFF);
        int base = (ra == 0 ? 0 : regs.getGPR(ra));
        int ea = base + d;
        short hw = (short) mem.readHalfWord(ea);  // readHalfWord returns 0-0xFFFF
        regs.setGPR(rt, hw); // sign-extended via Java short to int
    }

    // lhau rD, D(rA): load halfword algebraic, update rA 43
    public static void lhau(int instr, Registers regs, RAM mem) {
        int rt = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        short d = (short) (instr & 0xFFFF);
        int base = (ra == 0 ? 0 : regs.getGPR(ra));
        int ea = base + d;
        short hw = (short) mem.readHalfWord(ea);
        regs.setGPR(rt, hw);
        if (ra != 0) {
            regs.setGPR(ra, ea);
        }
    }

    // sth rS, D(rA): store halfword 44
    public static void sth(int instr, Registers regs, RAM mem) {
        int rs = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        short d = (short) (instr & 0xFFFF);
        int base = (ra == 0 ? 0 : regs.getGPR(ra));
        int ea = base + d;
        mem.writeHalfWord(ea, regs.getGPR(rs));
    }

    // sthu rS, D(rA): store halfword and update rA 45
    public static void sthu(int instr, Registers regs, RAM mem) {
        int rs = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        short d = (short) (instr & 0xFFFF);
        int base = (ra == 0 ? 0 : regs.getGPR(ra));
        int ea = base + d;
        mem.writeHalfWord(ea, regs.getGPR(rs));
        if (ra != 0) {
            regs.setGPR(ra, ea);
        }
    }

    // stb rS, D(rA): store byte 38
    public static void stb(int instr, Registers regs, RAM mem) {
        int rs = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        short d = (short) (instr & 0xFFFF);
        int base = (ra == 0 ? 0 : regs.getGPR(ra));
        int ea = base + d;
        mem.writeByte(ea, (byte) regs.getGPR(rs));
    }

    // stbu rS, D(rA): store byte and update rA 39
    public static void stbu(int instr, Registers regs, RAM mem) {
        int rs = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        short d = (short) (instr & 0xFFFF);
        int base = (ra == 0 ? 0 : regs.getGPR(ra));
        int ea = base + d;
        mem.writeByte(ea, (byte) regs.getGPR(rs));
        if (ra != 0) {
            regs.setGPR(ra, ea);
        }
    }

    // lfs: load float single 48
    public static void lfs(int instr, Registers regs, RAM mem) {
        int ft = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        short d = (short) (instr & 0xFFFF);
        int base = (ra == 0 ? 0 : regs.getGPR(ra));
        int ea = base + d;
        int bits = mem.readWord(ea);
        float f = Float.intBitsToFloat(bits);
        regs.putFPR(ft, f);
    }

    // stfs: store float single 52
    public static void stfs(int instr, Registers regs, RAM mem) {
        int ft = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        short d = (short) (instr & 0xFFFF);
        int base = (ra == 0 ? 0 : regs.getGPR(ra));
        int ea = base + d;
        float f = (float) regs.getFPR(ft);
        int bits = Float.floatToIntBits(f);
        mem.writeWord(ea, bits);
    }

    // lfd rD, D(rA): load doubleword and convert bits → double 50
    public static void lfd(int instr, Registers regs, RAM mem) {
        int ft = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        short d = (short) (instr & 0xFFFF);
        int base = (ra == 0 ? 0 : regs.getGPR(ra));
        int ea = base + d;
        // Alignment check: must be 8-byte aligned
        if (ea % 8 != 0) {
            throw new RuntimeException(String.format("Unaligned lfd at address 0x%08X", ea));
        }
        long bits = mem.readDoubleWord(ea);
        double val = Double.longBitsToDouble(bits);
        regs.putFPR(ft, val);
    }

// lfdu rD, D(rA): load doubleword, convert and update rA 51
    public static void lfdu(int instr, Registers regs, RAM mem) {
        int ft = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        short d = (short) (instr & 0xFFFF);
        int base = (ra == 0 ? 0 : regs.getGPR(ra));
        int ea = base + d;
        if (ea % 8 != 0) {
            throw new RuntimeException(String.format("Unaligned lfdu at address 0x%08X", ea));
        }
        long bits = mem.readDoubleWord(ea);
        double val = Double.longBitsToDouble(bits);
        regs.putFPR(ft, val);
        if (ra != 0) {
            regs.setGPR(ra, ea);
        }
    }

// stfd rS, D(rA): store doubleword (double → raw bits) 54
    public static void stfd(int instr, Registers regs, RAM mem) {
        int ft = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        short d = (short) (instr & 0xFFFF);
        int base = (ra == 0 ? 0 : regs.getGPR(ra));
        int ea = base + d;
        if (ea % 8 != 0) {
            throw new RuntimeException(String.format("Unaligned stfd at address 0x%08X", ea));
        }
        double val = regs.getFPR(ft);
        long bits = Double.doubleToRawLongBits(val);
        mem.writeDoubleWord(ea, bits);
    }

// stfdu rS, D(rA): store doubleword and update rA 55
    public static void stfdu(int instr, Registers regs, RAM mem) {
        int ft = (instr >> 21) & 0x1F;
        int ra = (instr >> 16) & 0x1F;
        short d = (short) (instr & 0xFFFF);
        int base = (ra == 0 ? 0 : regs.getGPR(ra));
        int ea = base + d;
        if (ea % 8 != 0) {
            throw new RuntimeException(String.format("Unaligned stfdu at address 0x%08X", ea));
        }
        double val = regs.getFPR(ft);
        long bits = Double.doubleToRawLongBits(val);
        mem.writeDoubleWord(ea, bits);
        if (ra != 0) {
            regs.setGPR(ra, ea);
        }
    }

}
