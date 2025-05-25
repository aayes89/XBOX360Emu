package xbox360emu.CPU.Instructions;

import xbox360emu.Interfaces.SubCaseHandler;
import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;
import java.util.HashMap;
import java.util.Map;
import xbox360emu.UTILS.Utilities;

/**
 *
 * @author Slam
 */
public class Case5 {

    private static final Map<Integer, SubCaseHandler> subCaseHandlers = new HashMap<>();

    static {
        // Mapeo de subcasos con sus respectivas funciones
        subCaseHandlers.put(0, Case5::vperm128);
        subCaseHandlers.put(1, Case5::vaddfp128);
        subCaseHandlers.put(5, Case5::vsubfp128);
        subCaseHandlers.put(9, Case5::vmulfp128);
        subCaseHandlers.put(13, Case5::vmaddfp128);
        subCaseHandlers.put(17, Case5::vmaddcfp128);
        subCaseHandlers.put(21, Case5::vnmsubfp128);
        subCaseHandlers.put(25, Case5::vmsum3fp128);
        subCaseHandlers.put(29, Case5::vmsum4fp128);
        subCaseHandlers.put(32, Case5::vpkshss128);
        subCaseHandlers.put(33, Case5::vand128);
        subCaseHandlers.put(36, Case5::vpkshus128);
        subCaseHandlers.put(37, Case5::vandc128);
        subCaseHandlers.put(40, Case5::vpkswss128);
        subCaseHandlers.put(41, Case5::vnor128);
        subCaseHandlers.put(44, Case5::vpkswus128);
        subCaseHandlers.put(45, Case5::vor128);
        subCaseHandlers.put(48, Case5::vpkuhum128);
        subCaseHandlers.put(49, Case5::vxor128);
        subCaseHandlers.put(52, Case5::vpkuhus128);
        subCaseHandlers.put(53, Case5::vsel128);
        subCaseHandlers.put(56, Case5::vpkuwum128);
        subCaseHandlers.put(57, Case5::vslo128);
        subCaseHandlers.put(60, Case5::vpkuwus128);
        subCaseHandlers.put(61, Case5::vsro128);
    }

    // Método principal para manejar el Case 5
    public static void handleCase5(int instr, Registers regs, RAM memory) {
        int sub = (Utilities.ExtractBits(instr, 22, 22) << 5) | (Utilities.ExtractBits(instr, 27, 27) << 0);

        // Llamar al subcaso correspondiente
        SubCaseHandler subCaseHandler = subCaseHandlers.get(sub);
        if (subCaseHandler != null) {
            subCaseHandler.execute(instr, regs, memory);
        } else {
            System.out.printf("Subcaso no soportado: sub=0x%02X\n", sub);
        }
    }

    // Definir los subcasos
    public static void vperm128(int instr, Registers regs, RAM memory) {
        System.out.println("Ejecutando vperm128");
        // Lógica de vperm128: Permutar 128 bits
        // Asume que VPR[ra] y VPR[rb] son los registros de 128 bits

        // Obtener los índices de los registros involucrados
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro de entrada
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro de entrada
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Acceder a los vectores de 128 bits (4 floats cada uno)
        int[] A = regs.getVPR(ra); // Vector A (primer operando)
        int[] B = regs.getVPR(rb); // Vector B (segundo operando)
        int[] R = regs.getVPR(rt); // Vector de destino

        // Aquí es donde se realiza la permutación. 
        // Suponiendo que tenemos un patrón simple (por ejemplo, un valor extraído de la instrucción)
        // Se podría aplicar alguna lógica de reordenación o permutación a los elementos de los vectores.
        // Ejemplo simple de permutación:
        // Si la instrucción tiene un campo que determine cómo permutar los elementos,
        // podemos usar una lógica como esta.
        // Por ejemplo, si `instr` tiene un campo de 2 bits que indique el tipo de permutación:
        int permPattern = Utilities.ExtractBits(instr, 0, 1); // Extraemos un patrón de permutación (por ejemplo, los primeros 2 bits)

        // Según el patrón, permutamos los vectores A y B
        switch (permPattern) {
            case 0:
                // Sin permutar, los registros se copian tal como están
                for (int i = 0; i < 4; i++) {
                    R[i] = A[i]; // Copiar A a R
                }
                break;
            case 1:
                // Permutar A y B
                for (int i = 0; i < 4; i++) {
                    R[i] = B[i]; // Copiar B a R
                }
                break;
            case 2:
                // Otra permutación: intercalar A y B
                R[0] = A[0];
                R[1] = B[0];
                R[2] = A[1];
                R[3] = B[1];
                break;
            case 3:
                // Otra permutación: reverso de A y B
                R[0] = A[3];
                R[1] = A[2];
                R[2] = B[1];
                R[3] = B[0];
                break;
            default:
                System.out.println("Patrón de permutación no soportado");
                break;
        }

        // Guardar el resultado en el registro de destino
        regs.setVPR(rt, R); // Guardar el resultado en el registro de destino
    }

    public static void vaddfp128(int instr, Registers regs, RAM memory) {
        System.out.println("Ejecutando vaddfp128");
        // Lógica de vaddfp128: Sumar dos vectores de 128 bits (en formato float)
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int rb = Utilities.ExtractBits(instr, 16, 20);
        int rt = Utilities.ExtractBits(instr, 6, 10);

        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt);

        for (int i = 0; i < 4; i++) {
            R[i] = A[i] + B[i];
        }
    }

    public static void vsubfp128(int instr, Registers regs, RAM memory) {
        System.out.println("Ejecutando vsubfp128");
        // Lógica de vsubfp128: Restar dos vectores de 128 bits (en formato float)
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int rb = Utilities.ExtractBits(instr, 16, 20);
        int rt = Utilities.ExtractBits(instr, 6, 10);

        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt);

        for (int i = 0; i < 4; i++) {
            R[i] = A[i] - B[i];
        }
    }

    public static void vmulfp128(int instr, Registers regs, RAM memory) {
        System.out.println("Ejecutando vmulfp128");
        // Lógica de vmulfp128: Multiplicar dos vectores de 128 bits (en formato float)
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int rb = Utilities.ExtractBits(instr, 16, 20);
        int rt = Utilities.ExtractBits(instr, 6, 10);

        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt);

        for (int i = 0; i < 4; i++) {
            R[i] = A[i] * B[i];
        }
    }

    public static void vmaddfp128(int instr, Registers regs, RAM memory) {
        System.out.println("Ejecutando vmaddfp128");
        // Lógica de vmaddfp128: Multiplicar y sumar con acumulador
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int rb = Utilities.ExtractBits(instr, 16, 20);
        int rt = Utilities.ExtractBits(instr, 6, 10);

        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt);

        for (int i = 0; i < 4; i++) {
            R[i] += A[i] * B[i];
        }
    }

    public static void vmaddcfp128(int instr, Registers regs, RAM memory) {
        System.out.println("Ejecutando vmaddcfp128");
        // Lógica de vmaddcfp128: Multiplicación y suma con acumulador condicional
        // Ejemplo similar a vmaddfp128 pero con un acumulador distinto.
        // Aquí usarías alguna condición de control, como `VACC`
        vmaddfp128(instr, regs, memory); // Puedes reutilizar la función de vmaddfp128
    }

    public static void vnmsubfp128(int instr, Registers regs, RAM memory) {
        System.out.println("Ejecutando vnmsubfp128");
        // Lógica de vnmsubfp128: Sumar con multiplicación negativa
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int rb = Utilities.ExtractBits(instr, 16, 20);
        int rt = Utilities.ExtractBits(instr, 6, 10);

        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt);

        for (int i = 0; i < 4; i++) {
            R[i] = -A[i] * B[i];
        }
    }

    public static void vmsum3fp128(int instr, Registers regs, RAM memory) {
        // Realiza una suma de vectores de 128 bits con 3 elementos en los registros flotantes
        System.out.println("Ejecutando vmsum3fp128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rc = Utilities.ExtractBits(instr, 21, 25); // Tercer registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 floats cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] C = regs.getVPR(rc);
        int[] R = regs.getVPR(rt); // Resultado

        // Sumar los primeros tres vectores (solo 3 elementos de cada uno)
        for (int i = 0; i < 3; i++) {
            R[i] = A[i] + B[i] + C[i]; // Sumar A, B, C elemento a elemento
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

    public static void vmsum4fp128(int instr, Registers regs, RAM memory) {
        // Realiza una suma de vectores de 128 bits con 4 elementos en los registros flotantes
        System.out.println("Ejecutando vmsum4fp128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rc = Utilities.ExtractBits(instr, 21, 25); // Tercer registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 floats cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] C = regs.getVPR(rc);
        int[] R = regs.getVPR(rt); // Resultado

        // Sumar los primeros cuatro vectores (4 elementos de cada uno)
        for (int i = 0; i < 4; i++) {
            R[i] = A[i] + B[i] + C[i]; // Sumar A, B, C elemento a elemento
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

    public static void vpkshss128(int instr, Registers regs, RAM memory) {
        // Suma saturada de enteros con signo (signed)
        System.out.println("Ejecutando vpkshss128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 enteros con signo cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt); // Resultado

        // Sumar con saturación
        for (int i = 0; i < 4; i++) {
            int sum = A[i] + B[i];
            R[i] = (sum > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (sum < Integer.MIN_VALUE) ? Integer.MIN_VALUE : sum; // Saturación
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

    public static void vand128(int instr, Registers regs, RAM memory) {
        // Operación AND entre dos vectores de 128 bits
        System.out.println("Ejecutando vand128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 enteros con signo cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt); // Resultado

        // Operación AND entre los elementos de A y B
        for (int i = 0; i < 4; i++) {
            R[i] = A[i] & B[i]; // Operación AND
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

    public static void vpkshus128(int instr, Registers regs, RAM memory) {
        // Suma saturada de enteros sin signo (unsigned)
        System.out.println("Ejecutando vpkshus128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 enteros sin signo cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt); // Resultado

        // Sumar con saturación (sin signo)
        for (int i = 0; i < 4; i++) {
            int sum = A[i] + B[i];
            R[i] = (sum > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (sum < 0) ? 0 : sum; // Saturación sin signo
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

    public static void vandc128(int instr, Registers regs, RAM memory) {
        // Operación AND con complementos de vectores
        System.out.println("Ejecutando vandc128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 enteros con signo cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt); // Resultado

        // Operación AND entre A y el complemento de B
        for (int i = 0; i < 4; i++) {
            R[i] = A[i] & ~B[i]; // Operación AND con el complemento de B
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

    public static void vpkswss128(int instr, Registers regs, RAM memory) {
        // Selección y expansión de palabras (sign extension)
        System.out.println("Ejecutando vpkswss128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 enteros cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt); // Resultado

        // Expansión de palabras (sign extension) y combinación de A y B
        for (int i = 0; i < 4; i++) {
            // Expansión de las palabras (sign extension)
            R[i] = (short) A[i] << 16 | (short) B[i]; // Expande la palabra de 16 bits a 32
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

    public static void vnor128(int instr, Registers regs, RAM memory) {
        // Operación NOR entre dos vectores de 128 bits
        System.out.println("Ejecutando vnor128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 enteros cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt); // Resultado

        // Operación NOR entre A y B
        for (int i = 0; i < 4; i++) {
            R[i] = ~(A[i] | B[i]); // Operación NOR
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

    public static void vpkswus128(int instr, Registers regs, RAM memory) {
        // Expansión de palabras sin signo (unsigned) con saturación
        System.out.println("Ejecutando vpkswus128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 enteros cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt); // Resultado

        // Expansión de palabras sin signo (unsigned) y saturación
        for (int i = 0; i < 4; i++) {
            int expandedA = (short) A[i];  // Expansión de la palabra A[i]
            int expandedB = (short) B[i];  // Expansión de la palabra B[i]

            // Combinación de A y B y saturación
            R[i] = (expandedA & 0xFFFF) | (expandedB << 16); // Expansión sin signo
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

    public static void vor128(int instr, Registers regs, RAM memory) {
        // Operación OR entre dos vectores de 128 bits
        System.out.println("Ejecutando vor128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 enteros cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt); // Resultado

        // Operación OR entre los elementos de A y B
        for (int i = 0; i < 4; i++) {
            R[i] = A[i] | B[i]; // Operación OR
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

    public static void vpkuhum128(int instr, Registers regs, RAM memory) {
        // Convertir a enteros sin signo con saturación y expansión de valores
        System.out.println("Ejecutando vpkuhum128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 enteros sin signo cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt); // Resultado

        // Expansión de enteros sin signo con saturación
        for (int i = 0; i < 4; i++) {
            R[i] = Math.min(Integer.MAX_VALUE, A[i] + B[i]); // Expansión y saturación
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

    public static void vxor128(int instr, Registers regs, RAM memory) {
        // Operación XOR entre dos vectores de 128 bits
        System.out.println("Ejecutando vxor128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 enteros cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt); // Resultado

        // Operación XOR entre los elementos de A y B
        for (int i = 0; i < 4; i++) {
            R[i] = A[i] ^ B[i]; // Operación XOR
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

    public static void vpkuhus128(int instr, Registers regs, RAM memory) {
        // Expansión de enteros sin signo con saturación sin signo
        System.out.println("Ejecutando vpkuhus128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 enteros sin signo cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt); // Resultado

        // Expansión de enteros sin signo con saturación
        for (int i = 0; i < 4; i++) {
            R[i] = Math.min(Integer.MAX_VALUE, (A[i] + B[i])); // Expansión y saturación sin signo
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

    public static void vsel128(int instr, Registers regs, RAM memory) {
        // Selección condicional de elementos entre dos vectores
        System.out.println("Ejecutando vsel128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 enteros cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt); // Resultado

        // Realizar selección condicional entre los elementos de A y B
        for (int i = 0; i < 4; i++) {
            R[i] = (A[i] != 0) ? A[i] : B[i]; // Si A[i] es no nulo, seleccionamos A[i], sino B[i]
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

    public static void vpkuwum128(int instr, Registers regs, RAM memory) {
        // Operación de expansión sin signo de 16 bits a 32 bits
        System.out.println("Ejecutando vpkuwum128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 enteros sin signo cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt); // Resultado

        // Expandir los valores de 16 bits a 32 bits
        for (int i = 0; i < 4; i++) {
            R[i] = (A[i] & 0xFFFF) | (B[i] << 16); // Expansión
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

    public static void vslo128(int instr, Registers regs, RAM memory) {
        // Operación de desplazamiento lógico a la izquierda en un vector de 128 bits
        System.out.println("Ejecutando vslo128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 enteros cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt); // Resultado

        // Realizar el desplazamiento lógico a la izquierda
        for (int i = 0; i < 4; i++) {
            R[i] = A[i] << B[i]; // Desplazamiento lógico a la izquierda
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

    public static void vsro128(int instr, Registers regs, RAM memory) {
        // Operación de desplazamiento lógico a la derecha en un vector de 128 bits
        System.out.println("Ejecutando vsro128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 enteros cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt); // Resultado

        // Realizar el desplazamiento lógico a la derecha
        for (int i = 0; i < 4; i++) {
            R[i] = A[i] >>> B[i]; // Desplazamiento lógico a la derecha
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

    public static void vpkuwus128(int instr, Registers regs, RAM memory) {
        // Suma saturada de enteros sin signo con el valor máximo de 32 bits
        System.out.println("Ejecutando vpkuwus128");

        // Obtener los registros de entrada
        int ra = Utilities.ExtractBits(instr, 11, 15); // Primer registro
        int rb = Utilities.ExtractBits(instr, 16, 20); // Segundo registro
        int rt = Utilities.ExtractBits(instr, 6, 10);  // Registro de destino

        // Obtener los vectores de 128 bits (4 enteros sin signo cada uno)
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int[] R = regs.getVPR(rt); // Resultado

        // Realizar la operación de suma saturada
        for (int i = 0; i < 4; i++) {
            int v = A[i] + B[i];
            int sat = v > Integer.MAX_VALUE ? Integer.MAX_VALUE : Math.max(v, 0);
            R[i] = sat; // Asignación con saturación
        }

        regs.setVPR(rt, R); // Guardar el resultado
    }

}
