
package xbox360emu.Interfaces;

/**
 *
 * @author Slam
 */

import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;

@FunctionalInterface
public interface InstructionHandler {
    void execute(int instr, Registers regs, RAM memory);
}

