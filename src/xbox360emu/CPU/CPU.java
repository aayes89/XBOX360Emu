package xbox360emu.CPU;

/**
 *
 * @author Slam
 */
import xbox360emu.CPU.Instructions.InstructionsHandler;
import xbox360emu.Exceptions.HaltException;
import xbox360emu.Memory.RAM;
import xbox360emu.PPCEmuConfig;

public class CPU {

    private final RAM memory;
    private final Registers regs;

    public CPU(RAM memory) {
        this.memory = memory;
        this.regs = new Registers();
    }

    public void loadInstruction(int address, int instruction) {
        memory.writeWord(address, instruction);
    }

    public void step() throws HaltException {
        int pc = regs.getPC();
        System.out.printf("Before execute: PC=0x%08X\n", pc);
        int instr = memory.readWord(pc);
        int oldPC = regs.getPC();
        InstructionsHandler.execute(instr, regs, memory);
        if (regs.getPC() == oldPC) { // Solo incrementar si la instrucción no cambió el PC
            regs.setPC(pc + 4);
        }
        System.out.printf("After execute: PC=0x%08X\n", regs.getPC());
    }

    public void runWithSteps(int steps) {
        for (int i = 0; i < steps; i++) {
            step();
        }
    }

    public void dumpState() {
        System.out.println("==== Estado CPU ====");
        for (int i = 0; i < 32; i++) {
            System.out.printf("R%d: 0x%08X\n", i, regs.getGPR(i));
        }
        System.out.printf("PC:  0x%08X\n", regs.getPC());
        System.out.printf("LR:  0x%08X\n", regs.getLR());
    }

    public Registers getRegisters() {
        return regs;
    }

}
