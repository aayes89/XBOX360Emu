package xbox360emu.CPU.Instructions;

import java.util.Arrays;
import xbox360emu.Interfaces.SubCaseHandler;
import java.util.HashMap;
import java.util.Map;
import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;
import xbox360emu.UTILS.Utilities;

/**
 *
 * @author Slam
 */
public class Case6 {

    private static final Map<Integer, SubCaseHandler> subCaseHandlers = new HashMap<>();
    static int[] R = new int[16];

    static {
        subCaseHandlers.put(0, Case6::vcmpeq_gefp128);
        subCaseHandlers.put(8, Case6::vcmpeq_gefp128);

        subCaseHandlers.put(33, Case6::vpermwi128);
        subCaseHandlers.put(39, Case6::vcfpuxws128);
        subCaseHandlers.put(35, Case6::vcfpsxws128);
        subCaseHandlers.put(43, Case6::vcsxw_cufp128);
        subCaseHandlers.put(47, Case6::vcsxw_cufp128);
        subCaseHandlers.put(51, Case6::vrfim_n_p_128);
        subCaseHandlers.put(55, Case6::vrfim_n_p_128);
        subCaseHandlers.put(59, Case6::vrfim_n_p_128);

        subCaseHandlers.put(63, Case6::instr63_99);
        subCaseHandlers.put(99, Case6::instr63_99);
        subCaseHandlers.put(103, Case6::instr63_99);
        subCaseHandlers.put(107, Case6::instr63_99);
        subCaseHandlers.put(111, Case6::instr63_99);
        subCaseHandlers.put(127, Case6::instr63_99);

        subCaseHandlers.put(97, Case6::vpkd3d128);
        subCaseHandlers.put(113, Case6::vrlimi128);
    }

    // Método principal para manejar el Case 5
    public static void handleCase6(int instr, Registers regs, RAM memory) {
        int sub = (Utilities.ExtractBits(instr, 22, 22) << 5) | (Utilities.ExtractBits(instr, 27, 27) << 0);

        // Llamar al subcaso correspondiente
        SubCaseHandler subCaseHandler = subCaseHandlers.get(sub);
        if (subCaseHandler != null) {
            subCaseHandler.execute(instr, regs, memory);
        } else {
            System.out.printf("Subcaso no soportado: sub=0x%02X\n", sub);
        }
    }

    public static void vpermwi128(int instr, Registers regs, RAM mem) {
        int[] A = regs.getVPR(Utilities.ExtractBits(instr, 11, 15));
        int[] R = regs.getVPR(Utilities.ExtractBits(instr, 6, 10));
        int sh = Utilities.ExtractBits(instr, 23, 28) & 3;

        for (int w = 0; w < 4; w++) {
            int srcIdx = 4 * ((w + sh) & 3);
            System.arraycopy(A, srcIdx, R, w * 4, 4);
        }

    }

    public static void vrfim_n_p_128(int instr, Registers regs, RAM mem) {
        //51, 55, 59 -> vrfim128, vrfin128, vrfip128
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int[] A = regs.getVPR(ra);
        for (int i = 0; i < 4; i++) {
            regs.putFPR(i, (float) Math.floor(A[i]));
        }

    }

    public static void vpkd3d128(int instr, Registers regs, RAM mem) {
        //case 97 -> { // vpkd3d128
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int rb = Utilities.ExtractBits(instr, 16, 20);
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        byte[] R = new byte[16];
        System.arraycopy(A, 8, R, 0, 8);
        System.arraycopy(B, 8, R, 8, 8);
        //    }
    }

    public static void vrlimi128(int instr, Registers regs, RAM mem) {
        //113 -> { // vrlimi128
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int[] A = regs.getVPR(ra);
        int sh = Utilities.ExtractBits(instr, 16, 20) & 0xF;
        for (int i = 0; i < 16; i++) {
            R[i] = (byte) A[(i + sh) & 0xF];
        }

    }

    public static void vcfpsxws128(int instr, Registers regs, RAM mem) {
        //ase 35 -> { // vcfpsxws128
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int[] A = regs.getVPR(ra);
        for (int i = 0; i < 4; i++) {
            int v = Math.round(A[i]);
            System.arraycopy(v, 0, R, i * 4, 4);
        }
    }

    public static void vcfpuxws128(int instr, Registers regs, RAM mem) {
        //case 39 -> { // vcfpuxws128
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int[] A = regs.getVPR(ra);
        for (int i = 0; i < 4; i++) {
            int v = (int) Math.floor(A[i]);
            System.arraycopy(v, 0, R, i * 4, 4);
        }
    }

    public static void vcsxw_cufp128(int instr, Registers regs, RAM mem) {
        //case 43, 47 -> { // vcsxwfp128 or vcuxwfp128
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int[] A = regs.getVPR(ra);
        for (int i = 0; i < 4; i++) {
            int val = (A[i * 4]);
            regs.putFPR(i, val);
        }

    }

    public static void instr63_99(int instr, Registers regs, RAM mem) {
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int rb = Utilities.ExtractBits(instr, 16, 20);
        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);
        int ext6_7bit = Utilities.ExtractBits(instr, 21, 27);
        switch (ext6_7bit) {
            case 63 -> { // vrfiz128
                for (int i = 0; i < 4; i++) {
                    regs.putFPR(i, Math.round(A[i]));
                }
            }
            case 99 -> { // vrefp128
                for (int i = 0; i < 4; i++) {
                    regs.putFPR(i, ((A[i] < 0) ? -B[i] : B[i]));
                }
            }
            case 103 -> { // vrsqrtefp128
                for (int i = 0; i < 4; i++) {
                    regs.putFPR(i, 1.0f / (float) Math.sqrt(A[i]));
                }
            }
            case 107 -> { // vexptefp128
                for (int i = 0; i < 4; i++) {
                    regs.putFPR(i, (float) Math.exp(A[i]));
                }
            }
            case 111 -> { // vlogefp128
                for (int i = 0; i < 4; i++) {
                    regs.putFPR(i, (float) Math.log(A[i]));
                }
            }
            case 127 -> {
                System.arraycopy(A, 0, R, 0, 8);
                System.arraycopy(B, 0, R, 8, 8);
            }
        }
    }

    public static void vcmpeq_gefp128(int instr, Registers regs, RAM memory) {
        int rt = Utilities.ExtractBits(instr, 6, 10);
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int rb = Utilities.ExtractBits(instr, 16, 20);

        int[] A = regs.getVPR(ra);
        int[] B = regs.getVPR(rb);

        int ext6_cmp = (Utilities.ExtractBits(instr, 22, 24) << 3) | (Utilities.ExtractBits(instr, 27, 27));

        switch (ext6_cmp) {
            case 0 -> { // vcmpeqfp128
                for (int i = 0; i < 2; i++) {
                    byte v = A[i] == B[i] ? (byte) 0xFF : 0x00;
                    Arrays.fill(R, i * 8, i * 8 + 8, v);
                }
            }
            case 8 -> { // vcmpgefp128
                for (int i = 0; i < 2; i++) {
                    byte v = A[i] >= B[i] ? (byte) 0xFF : 0x00;
                    Arrays.fill(R, i * 8, i * 8 + 8, v);
                }
            }
        }

        // Finalmente, escribir resultado
        regs.setVPR(rt, R);
    }

}