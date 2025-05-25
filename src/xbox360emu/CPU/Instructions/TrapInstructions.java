package xbox360emu.CPU.Instructions;

import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;

/**
 * autor: Slam
 * Instrucciones de trap (excepciones) para la CPU.
 * Estas instrucciones son usadas para manejar condiciones especiales en el procesador.
 */
public class TrapInstructions {
    
    /**
     * Extrae los bits de una instrucción.
     * @param instr La instrucción de la que se extraen los bits.
     * @param start El índice de inicio del rango de bits.
     * @param end El índice de fin del rango de bits.
     * @return El valor extraído de los bits.
     */
    public static int ExtractBits(int instr, int start, int end) {
        return (instr >>> start) & ((1 << (end - start + 1)) - 1);
    }

    /**
     * Maneja la instrucción tdi (Trap Immediate).
     * Realiza una comparación con el valor del registro y el valor inmediato para determinar si se lanza una excepción.
     * @param instr La instrucción tdi.
     * @param regs Los registros de la CPU.
     * @param mem La memoria de la CPU.
     */
    public static void tdi(int instr, Registers regs, RAM mem) {
        int tocr = (instr >> 21) & 0x1F;  // Extrae el valor del TOCR (Test Operation Control Register)
        int tbr = (instr >> 16) & 0x1F;   // Extrae el valor del TBR (Test Base Register)
        int simm = instr & 0xFFFF;        // Extrae el valor inmediato (simm)
        
        int ra_val = regs.getGPR(tbr);   // Obtiene el valor del registro tbr
        boolean trap = false;

        // Comparaciones basadas en el valor del TOCR
        if ((tocr & 0x10) != 0) {
            trap |= (ra_val < simm);
        }
        if ((tocr & 0x08) != 0) {
            trap |= (ra_val > simm);
        }
        if ((tocr & 0x04) != 0) {
            trap |= (ra_val == simm);
        }
        if ((tocr & 0x02) != 0) {
            trap |= (ra_val < 0);
        }
        if ((tocr & 0x01) != 0) {
            trap |= (ra_val > 0);
        }

        if (trap) {
            // Llama a una función para manejar la excepción
            TriggerException(0x100);  // PPU_EX_PROG = 0x100 es un vector de excepción hipotético
            return;  // Salir sin avanzar el PC adicional
        }
    }

    /**
     * Maneja la instrucción twi (Trap Word Immediate).
     * Similar a tdi pero con la comparación de un valor inmediato de palabra.
     * @param instr La instrucción twi.
     * @param regs Los registros de la CPU.
     * @param mem La memoria de la CPU.
     */
    public static void twi(int instr, Registers regs, RAM mem) {
        // Extrae los valores de los campos de la instrucción
        int tocr = ExtractBits(instr, 21, 25);  // Test Operation Control Register
        int ra = ExtractBits(instr, 16, 20);    // Registro para comparar
        int simm = ExtractBits(instr, 0, 15);   // Valor inmediato (simm)

        int ra_val = regs.getGPR(ra);  // Obtiene el valor del registro ra
        boolean trap = false;

        // Realiza las comparaciones según el TOCR
        if ((tocr & 0x10) != 0) {
            trap |= (ra_val < simm);
        }
        if ((tocr & 0x08) != 0) {
            trap |= (ra_val > simm);
        }
        if ((tocr & 0x04) != 0) {
            trap |= (ra_val == simm);
        }
        if ((tocr & 0x02) != 0) {
            trap |= (ra_val < 0);
        }
        if ((tocr & 0x01) != 0) {
            trap |= (ra_val > 0);
        }

        if (trap) {
            // Llama a una función para manejar el trap
            TriggerTrap();  // Función que maneja el trap
        }
    }

    // Asume que existe una función TriggerException para manejar excepciones
    private static void TriggerException(int exceptionCode) {
        // Aquí debería ir el código para manejar una excepción, como cambiar el PC a la dirección de la excepción
        System.out.println("Excepción activada con código: " + exceptionCode);
    }

    // Asume que existe una función TriggerTrap para manejar traps
    private static void TriggerTrap() {
        // Aquí debería ir el código para manejar un trap, como interrumpir la ejecución
        System.out.println("Trap activado.");
    }
}
