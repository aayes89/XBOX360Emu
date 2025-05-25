package xbox360emu.CPU.Instructions;

/**
 *
 * @author Slam
 */
import xbox360emu.Interfaces.InstructionHandler;
import java.util.HashMap;
import java.util.Map;
import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;

public class InstructionsHandler {

    /**
     * Mapa opcode → handler
     */
    private static final Map<Integer, InstructionHandler> handlers = new HashMap<>();
    /**
     * Mapa opcode → mnemonic
     */
    private static final Map<Integer, String> mnemonics = new HashMap<>();
    
    static {
        // Magic and CopyRights
        register(0, "nop", CommonsInstructions::magics_nop);
        register(1, "nop", CommonsInstructions::magics_nop);

        // Traps
        register(2, "tdi", TrapInstructions::tdi);
        register(3, "twi", TrapInstructions::twi);

        // Compare
        register(10, "cmpli", IntegerCompare::cmpli);
        register(11, "cmpwi", IntegerCompare::cmpwi);

        // Aritméticas
        register(7, "mulli", ArithmeticInstructions::mulli);
        register(8, "subficx", ArithmeticInstructions::subficx);
        register(12, "addic", ArithmeticInstructions::addic);
        register(13, "addicx", ArithmeticInstructions::addicx);
        register(14, "addi", ArithmeticInstructions::addi);
        register(40, "subf", ArithmeticInstructions::subf);

        // Lógicas
        register(28, "and", LogicalInstructions::and);
        register(25, "oris", LogicalInstructions::oris);
        register(26, "xori", LogicalInstructions::xori);
        register(27, "xoris", LogicalInstructions::xoris);
        register(29, "andisx", LogicalInstructions::andisx);
        register(24, "ori", LogicalInstructions::ori);
        register(31, "or", LogicalInstructions::orInstr);

        // Memoria
        register(15, "lis", CommonsInstructions::lis);
        register(32, "lwz", MemoryInstructions::lwz);
        register(9, "stw", MemoryInstructions::stw);
        register(36, "stw", MemoryInstructions::stw);
        register(17, "sc", MemoryInstructions::sc);
        register(22, "lhz", MemoryInstructions::lhz);
        register(33, "lwzu", MemoryInstructions::lwzu);
        register(34, "lbz", MemoryInstructions::lbz);
        register(35, "lbzu", MemoryInstructions::lbzu);
        register(37, "stwu", MemoryInstructions::stwu);
        register(38, "stb", MemoryInstructions::stb);
        register(39, "stbu", MemoryInstructions::stbu);
        register(41, "lhzu", MemoryInstructions::lhzu);
        register(42, "lha", MemoryInstructions::lha);
        register(43, "lhau", MemoryInstructions::lhau);
        register(44, "sth", MemoryInstructions::sth);
        register(45, "sthu", MemoryInstructions::sthu);
        register(46, "lmw", MemoryInstructions::lmw);
        register(47, "stmw", MemoryInstructions::stmw);
        register(48, "lfs", MemoryInstructions::lfs);
        register(49, "lfsu", MemoryInstructions::lfsu);
        register(50, "lfd", MemoryInstructions::lfd);
        register(51, "lfdu", MemoryInstructions::lfdu);
        register(52, "stfs", MemoryInstructions::stfs);
        register(53, "stfsu", MemoryInstructions::stfsu);
        register(54, "stfd", MemoryInstructions::stfd);
        register(55, "stfdu", MemoryInstructions::stfdu);

        // Branch
        register(16, "bc", BranchInstructions::bc);
        register(18, "b", BranchInstructions::b);
        register(19, "bcl", BranchInstructions::bcl);

        // Bitwise
        register(20, "rlwimi", BitwiseInstructions::rlwimi);
        register(21, "rlwinm", BitwiseInstructions::rlwinm);
        register(23, "rlwnm", BitwiseInstructions::rlwnm);

        // Casos extendidos (Case 4, 5, 30, 31, 58, 59, 62, 63)
        register(4, "case4", Case4::case_4);
        register(5, "case5", CommonsInstructions::handleCase5);
        register(30, "case30", Case30::rl_case30);
        register(31, "case31", Case31::case_31);
        register(58, "case58", Case58::case_58);
        register(59, "case59", Case59::case_59);
        register(62, "case62", Case62::case_62);
        register(63, "case63", Case63::case_63);
    }
    
    private static void register(int opcode, String mnem, InstructionHandler h) {
        handlers.put(opcode, h);
        mnemonics.put(opcode, mnem);
    }
    
    public static void execute(int instr, Registers regs, RAM memory) {
        int opcode = (instr >>> 26) & 0x3F;
        InstructionHandler handler = handlers.get(opcode);
        String mnem = mnemonics.get(opcode);
        
        if (handler != null) {
            System.out.printf("Executing [%s] (opcode=0x%02X) at PC=0x%08X%n",
                    mnem, opcode, regs.getPC());
            handler.execute(instr, regs, memory);
        } else {
            System.out.printf("Unsupported instruction: opcode=0x%02X PC=0x%08X%n",
                    opcode, regs.getPC());
        }
    }
}
