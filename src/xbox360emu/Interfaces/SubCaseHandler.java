
package xbox360emu.Interfaces;

import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;

/**
 *
 * @author Slam
 */
public interface SubCaseHandler {
    void execute(int instr, Registers regs, RAM memory);
}
