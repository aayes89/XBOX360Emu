package xbox360emu.CPU.Instructions;

import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;

/**
 *
 * @author Slam
 */
public class CommonsInstructions {

    // Manejo de Case 5
    public static void handleCase5(int instr, Registers regs, RAM memory) {
        Case5.handleCase5(instr, regs, memory);
    }

    // Instructions
    public static void magics_nop(int instr, Registers regs, RAM mem) {
        //System.out.println("MagicKeys here - NOP");        
    }

    public static void lis(int instr, Registers regs, RAM mem) {
        //15 lis
        int rD = (instr >> 21) & 0x1F;
        int imm = (int) (instr & 0xFFFF);
        regs.setGPR(rD, ((int) imm) << 16);
        //System.out.println("Executed lis: r" + rD + " = 0x" + Integer.toHexString(regs.getGPR(rD)));
    }

}

/*
case 0: { // MagicKey        	
		int op = instr & 0xFC0007FE; // mask: bits 0–1(always 0), 6–10(op), 21–30(XO)
		//LOG_WARNING("CPU", "Instruccion 0x%008X y OPCODE %d no implementadas", instr, op);		
		if (instr == 0) {
			break; // NOP
		}
		else
			if (op == 0) {
				// instrucción “null” real: PC += 4 (ya lo hace el Step)			
				break;
			}
			else if (op == 0x7C0004A6) { // formato de nop en PPC
				//PC += 4;
				break;
			}
			else if (ExtractBits(instr, 0, 15) == 0x7c00) {
				//PC += 4;
				break;
			}
			else if (instr == 0x3410583 || instr == 0x83054103) {
				std::cout << "[1BL]   MagicKey Stage 1 detected!" << std::endl;
				break;
			}
			else if (instr == 0x7c00) { // 32KB - 31744B
				LOG_INFO("", "[2BL]  1BL total size: %dB", instr);
				PC = (0xF8);
				break;
			}
			else if (instr == 0x100) { // 256B for Header and Copyrights
				LOG_INFO("", "[1BL]   Valid 1BL Stage Size: 0x%08X, EntryPoint at: 0x%08X", instr, (0xF8));
				break;
			}
			else if (instr == 0x00020712) {
				LOG_INFO("", "[CPU] Unknown Instruction 0x%008X with Opcode %d", instr, op);
				break;
			}
			else {
				LOG_INFO("", "[1BL]   Invalid 1BL Stage Size: 0x%08X", instr);
			}
		LOG_ERROR("[CPU]", "Invalid instruction 0x%08X", instr);
		TriggerException(PPU_EX_PROG);
		break;
	}
	case 1: { break; } // NOP
	
*/