package xbox360emu.CPU.Instructions;

import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;
import xbox360emu.UTILS.Utilities;

/**
 *
 * @author Slam
 */
public class Case4 {

    /* private static final Map<Integer, SubCaseHandler> subCaseHandlers = new HashMap<>();

    static {}
    // Método principal para manejar el Case 5
    public static void handleCase5(int instr, Registers regs, RAM memory) {
        int sub = (Utilities.Utilities.ExtractBits(instr, 22, 22) << 5) | (Utilities.Utilities.ExtractBits(instr, 27, 27) << 0);

        // Llamar al subcaso correspondiente
        SubCaseHandler subCaseHandler = subCaseHandlers.get(sub);
        if (subCaseHandler != null) {
            subCaseHandler.execute(instr, regs, memory);
        } else {
            System.out.printf("Subcaso no soportado: sub=0x%02X\n", sub);
        }
    }*/
    public static void case_4(int instr, Registers regs, RAM mem) {
        int rt = (instr >> 21) & 0x1F; // Bits 21–25
        int ra = (instr >> 16) & 0x1F; // Bits 16–20
        int rb = (instr >> 11) & 0x1F; // Bits 11–15
        int rc = (instr >> 6) & 0x1F;  // Bits 6–10

        int case2 = ((instr >> 4) & 0x7F8) | ((instr >> 1) & 0x7); // Bits 21-27 << 3 | 28-30
        int case3 = instr & 0x7FF; // Bits 21-31
        int case4 = (instr >> 1) & 0x3FF; // Bits 22-31
        int case5 = instr & 0x3F; // Bits 26-31

        int addr;
        int[] result = new int[4]; // Vector de 128 bits (4 words)

        switch (case2) {
            case 3: { // lvsl128 - Load Vector Shift Left
                addr = (ra == 0 ? 0 : regs.getGPR(ra)) + regs.getGPR(rb);
                regs.setVPR(rt, Utilities.loadVectorShiftLeft(mem, addr));
                break;
            }
            case 67: { // lvsr - Load Vector Shift Right
                addr = (ra == 0 ? 0 : regs.getGPR(ra)) + regs.getGPR(rb);
                regs.setVPR(rt, Utilities.loadVectorShiftRight(mem, addr));
                break;
            }
            case 131: { // lvewx128 - Load Vector Element Word Indexed
                addr = (ra == 0 ? 0 : regs.getGPR(ra)) + regs.getGPR(rb);
                int word = mem.readWord(addr);
                int[] vector = regs.getVPR(rt);
                if (vector == null) {
                    vector = new int[4];
                }
                int index = (addr & 0xF) >> 2; // Posición de la palabra (0-3)
                vector[index] = word;
                regs.setVPR(rt, vector);
                break;
            }
            case 195: { // lvx128 - Load Vector Indexed
                addr = (ra == 0 ? 0 : regs.getGPR(ra)) + regs.getGPR(rb);
                byte[] data = mem.readRegion(addr & ~0xF, 16); // Alineado a 16 bytes
                regs.setVPR(rt, byteArrayToIntArray(data));
                break;
            }
            case 387: { // stvewx128 - Store Vector Element Word Indexed
                addr = (ra == 0 ? 0 : regs.getGPR(ra)) + regs.getGPR(rb);
                int[] vector = regs.getVPR(rt);
                int index = (addr & 0xF) >> 2; // Posición de la palabra (0-3)
                mem.writeWord(addr, vector[index]);
                break;
            }
            case 451: { // stvx128 - Store Vector Indexed
                addr = (ra == 0 ? 0 : regs.getGPR(ra)) + regs.getGPR(rb);
                byte[] data = intArrayToByteArray(regs.getVPR(rt));
                mem.writeRegion(data, addr & ~0xF, (addr & ~0xF) + 16);
                break;
            }
            case 707: { // lvxl128 - Load Vector Indexed LRU
                addr = (ra == 0 ? 0 : regs.getGPR(ra)) + regs.getGPR(rb);
                byte[] data = mem.readRegion(addr & ~0xF, 16);
                regs.setVPR(rt, byteArrayToIntArray(data));
                break;
            }
            case 963: { // stvxl128 - Store Vector Indexed LRU
                addr = (ra == 0 ? 0 : regs.getGPR(ra)) + regs.getGPR(rb);
                byte[] data = intArrayToByteArray(regs.getVPR(rt));
                mem.writeRegion(data, addr & ~0xF, (addr & ~0xF) + 16);
                break;
            }
            case 1027: { // lvlx128 - Load Vector Left Indexed
                addr = (ra == 0 ? 0 : regs.getGPR(ra)) + regs.getGPR(rb);
                regs.setVPR(rt, Utilities.loadVectorShiftLeft(mem, addr));
                break;
            }
            case 1091: { // lvrx128 - Load Vector Right Indexed
                addr = (ra == 0 ? 0 : regs.getGPR(ra)) + regs.getGPR(rb);
                regs.setVPR(rt, Utilities.loadVectorShiftRight(mem, addr));
                break;
            }
            case 1283: { // stvlx128 - Store Vector Left Indexed
                addr = (ra == 0 ? 0 : regs.getGPR(ra)) + regs.getGPR(rb);
                byte[] data = intArrayToByteArray(regs.getVPR(rt));
                mem.writeRegion(data, addr, addr + 16);
                break;
            }
            case 1347: { // stvrx128 - Store Vector Right Indexed
                addr = (ra == 0 ? 0 : regs.getGPR(ra)) + regs.getGPR(rb);
                byte[] data = intArrayToByteArray(regs.getVPR(rt));
                mem.writeRegion(data, addr, addr + 16);
                break;
            }
            case 1539: { // lvlxl128 - Load Vector Left Indexed LRU
                addr = (ra == 0 ? 0 : regs.getGPR(ra)) + regs.getGPR(rb);
                regs.setVPR(rt, Utilities.loadVectorShiftLeft(mem, addr));
                break;
            }
            case 1603: { // lvrxl128 - Load Vector Right Indexed LRU
                addr = (ra == 0 ? 0 : regs.getGPR(ra)) + regs.getGPR(rb);
                regs.setVPR(rt, Utilities.loadVectorShiftRight(mem, addr));
                break;
            }
            case 1795: { // stvlxl128 - Store Vector Left Indexed LRU
                addr = (ra == 0 ? 0 : regs.getGPR(ra)) + regs.getGPR(rb);
                byte[] data = intArrayToByteArray(regs.getVPR(rt));
                mem.writeRegion(data, addr, addr + 16);
                break;
            }
            default:
                System.out.printf("Subcaso no soportado: case2=0x%X%n", case2);
        }

        switch (case3) {
            case 0: { // vaddubm
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) ((a[i] & 0xFF) + (b[i] & 0xFF));
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 2: { // vmaxub
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) Math.max(a[i] & 0xFF, b[i] & 0xFF);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 4: { // vrlb
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    int shift = b[i] & 0x7;
                    r[i] = (byte) ((a[i] >>> shift) | (a[i] << (8 - shift)));
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 8: { // vmuloub
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) (((a[i] & 0xFF) * (b[i] & 0xFF)) & 0xFF);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 10: { // vaddfp
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] b = intArrayToFloatArray(regs.getVPR(rb));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i] + b[i];
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 12: { // vmrghb
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 8; i++) {
                    r[2 * i] = b[i];
                    r[2 * i + 1] = a[i];
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 14: { // vpkuhum
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                byte[] r = new byte[16];
                for (int i = 0; i < 8; i++) {
                    r[i] = (byte) a[2 * i];
                    r[i + 8] = (byte) a[2 * i + 1];
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 64: { // vadduhm
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    r[i] = (short) (a[i] + b[i]);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 66: { // vmaxuh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    r[i] = (short) Math.max(a[i] & 0xFFFF, b[i] & 0xFFFF);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 68: { // vrlh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    int shift = b[i] & 0xF;
                    r[i] = (short) ((a[i] >>> shift) | (a[i] << (16 - shift)));
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 72: { // vmulouh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    r[i] = (short) (((a[i] & 0xFFFF) * (b[i] & 0xFFFF)) & 0xFFFF);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 74: { // vsubfp
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] b = intArrayToFloatArray(regs.getVPR(rb));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i] - b[i];
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 76: { // vmrghh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 4; i++) {
                    r[i] = b[i];
                    r[i + 4] = a[i];
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 78: { // vpkuwum
                int[] a = regs.getVPR(ra);
                short[] r = new short[8];
                for (int i = 0; i < 4; i++) {
                    r[i] = (short) (a[i] >>> 16);
                    r[i + 4] = (short) (a[i] & 0xFFFF);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 128: { // vadduwm
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i] + b[i];
                }
                regs.setVPR(rt, r);
                break;
            }
            case 130: { // vmaxuw
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = Math.max(a[i], b[i]);
                }
                regs.setVPR(rt, r);
                break;
            }
            case 132: { // vrlw
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    int shift = b[i] & 0x1F;
                    r[i] = (a[i] >>> shift) | (a[i] << (32 - shift));
                }
                regs.setVPR(rt, r);
                break;
            }
            case 140: { // vmrghw
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                r[0] = b[0];
                r[1] = a[0];
                r[2] = b[1];
                r[3] = a[1];
                regs.setVPR(rt, r);
                break;
            }
            case 142: { // vpkuhus
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                byte[] r = new byte[16];
                for (int i = 0; i < 8; i++) {
                    int val = Math.min(Math.max(a[i], 0), 255);
                    r[i] = (byte) val;
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 206: { // vpkuwus
                int[] a = regs.getVPR(ra);
                short[] r = new short[8];
                for (int i = 0; i < 4; i++) {
                    r[i] = (short) Math.min(Math.max(a[i] >>> 16, 0), 65535);
                    r[i + 4] = (short) Math.min(Math.max(a[i] & 0xFFFF, 0), 65535);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 258: { // vmaxsb
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) Math.max(a[i], b[i]);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 260: { // vslb
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    int shift = b[i] & 0x7;
                    r[i] = (byte) (a[i] << shift);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 264: { // vmulosb
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    int prod = (a[i] * b[i]) >> 8;
                    r[i] = (byte) prod;
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 266: { // vrefp
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i] != 0 ? 1.0f / a[i] : Float.POSITIVE_INFINITY;
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 268: { // vmrglb
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 8; i++) {
                    r[2 * i] = b[i + 8];
                    r[2 * i + 1] = a[i + 8];
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 270: { // vpkshus
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                byte[] r = new byte[16];
                for (int i = 0; i < 8; i++) {
                    r[i] = (byte) Math.min(Math.max(a[i], 0), 255);
                    r[i + 8] = (byte) Math.min(Math.max(a[i + 8], 0), 255);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 322: { // vmaxsh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    r[i] = (short) Math.max(a[i], b[i]);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 324: { // vslh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    int shift = b[i] & 0xF;
                    r[i] = (short) (a[i] << shift);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 328: { // vmulosh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    int prod = (a[i] * b[i]) >> 16;
                    r[i] = (short) prod;
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 330: { // vrsqrtefp
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i] != 0 ? (float) (1.0 / Math.sqrt(a[i])) : Float.POSITIVE_INFINITY;
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 332: { // vmrglh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 4; i++) {
                    r[i] = b[i + 4];
                    r[i + 4] = a[i + 4];
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 334: { // vpkswus
                int[] a = regs.getVPR(ra);
                short[] r = new short[8];
                for (int i = 0; i < 4; i++) {
                    r[i] = (short) Math.min(Math.max(a[i] >> 16, 0), 65535);
                    r[i + 4] = (short) Math.min(Math.max(a[i] & 0xFFFF, 0), 65535);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 384: { // vaddcuw
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    long sum = (a[i] & 0xFFFFFFFFL) + (b[i] & 0xFFFFFFFFL);
                    r[i] = (int) (sum >>> 32); // Carry
                }
                regs.setVPR(rt, r);
                break;
            }
            case 386: { // vmaxsw
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = Math.max(a[i], b[i]);
                }
                regs.setVPR(rt, r);
                break;
            }
            case 388: { // vslw
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    int shift = b[i] & 0x1F;
                    r[i] = a[i] << shift;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 394: { // vexptefp
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = (float) Math.pow(2.0, a[i]);
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 396: { // vmrglw
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                r[0] = b[2];
                r[1] = a[2];
                r[2] = b[3];
                r[3] = a[3];
                regs.setVPR(rt, r);
                break;
            }
            case 398: { // vpkshss
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                byte[] r = new byte[16];
                for (int i = 0; i < 8; i++) {
                    r[i] = (byte) Math.min(Math.max(a[i], -128), 127);
                    r[i + 8] = (byte) Math.min(Math.max(a[i + 8], -128), 127);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 452: { // vsl
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    int shift = b[i] & 0x7;
                    r[i] = (byte) (a[i] << shift);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 458: { // vlogefp
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = (float) Math.log(a[i]) / (float) Math.log(2.0);
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 462: { // vpkswss
                int[] a = regs.getVPR(ra);
                short[] r = new short[8];
                for (int i = 0; i < 4; i++) {
                    r[i] = (short) Math.min(Math.max(a[i] >> 16, -32768), 32767);
                    r[i + 4] = (short) Math.min(Math.max(a[i] & 0xFFFF, -32768), 32767);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 512: { // vaddubs
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    int sum = (a[i] & 0xFF) + (b[i] & 0xFF);
                    r[i] = (byte) Math.min(sum, 255);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 514: { // vminub
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) Math.min(a[i] & 0xFF, b[i] & 0xFF);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 516: { // vsrb
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    int shift = b[i] & 0x7;
                    r[i] = (byte) (a[i] >>> shift);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 520: { // vmuleub
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    int prod = (a[i] & 0xFF) * (b[i] & 0xFF);
                    r[i] = (byte) (prod & 0xFF);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 522: { // vrfin
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = (float) Math.round(a[i]);
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 524: { // vspltb
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] r = new byte[16];
                int idx = Utilities.ExtractBits(instr, 16, 20) & 0xF;
                byte val = a[idx];
                for (int i = 0; i < 16; i++) {
                    r[i] = val;
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 526: { // vupkhsb
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    r[i] = a[i];
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 576: { // vadduhs
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    int sum = (a[i] & 0xFFFF) + (b[i] & 0xFFFF);
                    r[i] = (short) Math.min(sum, 65535);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 578: { // vminuh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    r[i] = (short) Math.min(a[i] & 0xFFFF, b[i] & 0xFFFF);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 580: { // vsrh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    int shift = b[i] & 0xF;
                    r[i] = (short) (a[i] >>> shift);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 584: { // vmuleuh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    int prod = (a[i] & 0xFFFF) * (b[i] & 0xFFFF);
                    r[i] = (short) prod;
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 586: { // vrfiz
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = (float) Math.floor(a[i]);
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 588: { // vsplth
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] r = new short[8];
                int idx = Utilities.ExtractBits(instr, 16, 20) & 0x7;
                short val = a[idx];
                for (int i = 0; i < 8; i++) {
                    r[i] = val;
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 590: { // vupkhsh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i];
                }
                regs.setVPR(rt, r);
                break;
            }
            case 640: { // vadduws
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    long sum = (a[i] & 0xFFFFFFFFL) + (b[i] & 0xFFFFFFFFL);
                    r[i] = (int) Math.min(sum, 0xFFFFFFFFL);
                }
                regs.setVPR(rt, r);
                break;
            }
            case 642: { // vminuw
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = Math.min(a[i], b[i]);
                }
                regs.setVPR(rt, r);
                break;
            }
            case 644: { // vsrw
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    int shift = b[i] & 0x1F;
                    r[i] = a[i] >>> shift;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 650: { // vrfip
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = (float) Math.ceil(a[i]);
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 652: { // vspltw
                int[] a = regs.getVPR(ra);
                int[] r = new int[4];
                int idx = Utilities.ExtractBits(instr, 16, 20) & 0x3;
                int val = a[idx];
                for (int i = 0; i < 4; i++) {
                    r[i] = val;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 654: { // vupklsb
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    r[i] = a[i + 8];
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 708: { // vsr
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    int shift = b[i] & 0x7;
                    r[i] = (byte) (a[i] >>> shift);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 714: { // vrfim
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = (float) Math.floor(a[i]);
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 718: { // vupklsh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i + 4];
                }
                regs.setVPR(rt, r);
                break;
            }
            case 768: { // vaddsbs
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    int sum = a[i] + b[i];
                    r[i] = (byte) Math.min(Math.max(sum, -128), 127);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 770: { // vminsb
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) Math.min(a[i], b[i]);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 772: { // vsrab
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    int shift = b[i] & 0x7;
                    r[i] = (byte) (a[i] >> shift);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 776: { // vmulesb
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    int prod = a[i] * b[i];
                    r[i] = (byte) (prod & 0xFF);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 778: { // vcfux
                int[] a = regs.getVPR(ra);
                float[] r = new float[4];
                int scale = Utilities.ExtractBits(instr, 16, 20);
                for (int i = 0; i < 4; i++) {
                    r[i] = (a[i] & 0xFFFFFFFFL) / (float) Math.pow(2, scale);
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 780: { // vspltisb
                byte[] r = new byte[16];
                int val = Utilities.ExtractBits(instr, 16, 20);
                if ((val & 0x10) != 0) {
                    val |= 0xFFFFFFE0;
                }
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) val;
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 782: { // vpkpx
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                short[] r = new short[8];
                for (int i = 0; i < 4; i++) {
                    r[i] = (short) (((a[i] >>> 16) & 0xF800) | ((a[i] >>> 13) & 0x7C0) | ((a[i] >>> 10) & 0x3E));
                    r[i + 4] = (short) (((b[i] >>> 16) & 0xF800) | ((b[i] >>> 13) & 0x7C0) | ((b[i] >>> 10) & 0x3E));
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 832: { // vaddshs
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    int sum = a[i] + b[i];
                    r[i] = (short) Math.min(Math.max(sum, -32768), 32767);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 834: { // vminsh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    r[i] = (short) Math.min(a[i], b[i]);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 836: { // vsrah
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    int shift = b[i] & 0xF;
                    r[i] = (short) (a[i] >> shift);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 840: { // vmulesh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    int prod = a[i] * b[i];
                    r[i] = (short) prod;
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 842: { // vcfsx
                int[] a = regs.getVPR(ra);
                float[] r = new float[4];
                int scale = Utilities.ExtractBits(instr, 16, 20);
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i] / (float) Math.pow(2, scale);
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 844: { // vspltish
                short[] r = new short[8];
                int val = Utilities.ExtractBits(instr, 16, 20);
                if ((val & 0x10) != 0) {
                    val |= 0xFFFFFFE0;
                }
                for (int i = 0; i < 8; i++) {
                    r[i] = (short) val;
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 846: { // vupkhpx
                int[] a = regs.getVPR(ra);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = ((a[i] >>> 16) & 0xF800) | ((a[i] >>> 13) & 0x7C0) | ((a[i] >>> 10) & 0x3E) | ((a[i] >>> 8) & 0x1);
                }
                regs.setVPR(rt, r);
                break;
            }
            case 896: { // vaddsws
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    long sum = (long) a[i] + b[i];
                    r[i] = (int) Math.min(Math.max(sum, Integer.MIN_VALUE), Integer.MAX_VALUE);
                }
                regs.setVPR(rt, r);
                break;
            }
            case 898: { // vminsw
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = Math.min(a[i], b[i]);
                }
                regs.setVPR(rt, r);
                break;
            }
            case 900: { // vsraw
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    int shift = b[i] & 0x1F;
                    r[i] = a[i] >> shift;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 906: { // vctuxs
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                int[] r = new int[4];
                int scale = Utilities.ExtractBits(instr, 16, 20);
                for (int i = 0; i < 4; i++) {
                    r[i] = (int) (a[i] * Math.pow(2, scale));
                }
                regs.setVPR(rt, r);
                break;
            }
            case 908: { // vspltisw
                int[] r = new int[4];
                int val = Utilities.ExtractBits(instr, 16, 20);
                if ((val & 0x10) != 0) {
                    val |= 0xFFFFFFE0;
                }
                for (int i = 0; i < 4; i++) {
                    r[i] = val;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 970: { // vctsxs
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                int[] r = new int[4];
                int scale = Utilities.ExtractBits(instr, 16, 20);
                for (int i = 0; i < 4; i++) {
                    r[i] = (int) (a[i] * Math.pow(2, scale));
                }
                regs.setVPR(rt, r);
                break;
            }
            case 974: { // vupklpx
                int[] a = regs.getVPR(ra);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = ((a[i + 2] >>> 16) & 0xF800) | ((a[i + 2] >>> 13) & 0x7C0) | ((a[i + 2] >>> 10) & 0x3E) | ((a[i + 2] >>> 8) & 0x1);
                }
                regs.setVPR(rt, r);
                break;
            }
            case 1024: { // vsububm
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) ((a[i] & 0xFF) - (b[i] & 0xFF));
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 1026: { // vavgub
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) (((a[i] & 0xFF) + (b[i] & 0xFF) + 1) >> 1);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 1028: { // vand
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) (a[i] & b[i]);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 1034: { // vmaxfp
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] b = intArrayToFloatArray(regs.getVPR(rb));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = Math.max(a[i], b[i]);
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 1036: { // vslo
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                int shift = (b[15] & 0xFF) >> 3;
                for (int i = 0; i < 16 - shift; i++) {
                    r[i] = a[i + shift];
                }
                for (int i = 16 - shift; i < 16; i++) {
                    r[i] = 0;
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 1088: { // vsubuhm
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    r[i] = (short) (a[i] - b[i]);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 1090: { // vavguh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    r[i] = (short) (((a[i] & 0xFFFF) + (b[i] & 0xFFFF) + 1) >> 1);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 1092: { // vandc
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) (a[i] & ~b[i]);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 1098: { // vminfp
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] b = intArrayToFloatArray(regs.getVPR(rb));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = Math.min(a[i], b[i]);
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 1100: { // vsro
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                int shift = (b[15] & 0xFF) >> 3;
                for (int i = shift; i < 16; i++) {
                    r[i] = a[i - shift];
                }
                for (int i = 0; i < shift; i++) {
                    r[i] = 0;
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 1152: { // vsubuwm
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i] - b[i];
                }
                regs.setVPR(rt, r);
                break;
            }
            case 1154: { // vavguw
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    long avg = ((a[i] & 0xFFFFFFFFL) + (b[i] & 0xFFFFFFFFL) + 1) >> 1;
                    r[i] = (int) avg;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 1156: { // vor
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) (a[i] | b[i]);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 1220: { // vxor
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) (a[i] ^ b[i]);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 1282: { // vavgsb
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) ((a[i] + b[i]) >> 1);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 1284: { // vnor
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) ~(a[i] | b[i]);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 1346: { // vavgsh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    r[i] = (short) ((a[i] + b[i]) >> 1);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 1408: { // vsubcuw
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    long borrow = (a[i] & 0xFFFFFFFFL) >= (b[i] & 0xFFFFFFFFL) ? 1 : 0;
                    r[i] = (int) borrow;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 1410: { // vavgsw
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    long avg = ((long) a[i] + b[i]) >> 1;
                    r[i] = (int) avg;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 1536: { // vsububs
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    int diff = (a[i] & 0xFF) - (b[i] & 0xFF);
                    r[i] = (byte) Math.max(diff, 0);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 1540: { // mfvscr
                regs.setGPR(rt, regs.getFPSCR());
                break;
            }
            case 1544: { // vsum4ubs
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    long sum = b[i] & 0xFFFFFFFFL;
                    for (int j = 0; j < 4; j++) {
                        sum += a[4 * i + j] & 0xFF;
                    }
                    r[i] = (int) Math.min(sum, 0xFFFFFFFFL);
                }
                regs.setVPR(rt, r);
                break;
            }
            case 1600: { // vsubuhs
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    int diff = (a[i] & 0xFFFF) - (b[i] & 0xFFFF);
                    r[i] = (short) Math.max(diff, 0);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 1604: { // mtvscr
                regs.setFPSCR(regs.getVPR(rt)[0]);
                break;
            }
            case 1608: { // vsum4shs
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    int sum = b[i];
                    for (int j = 0; j < 2; j++) {
                        sum += a[2 * i + j];
                    }
                    r[i] = Math.min(Math.max(sum, -32768), 32767);
                }
                regs.setVPR(rt, r);
                break;
            }
            case 1664: { // vsubuws
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    long diff = (a[i] & 0xFFFFFFFFL) - (b[i] & 0xFFFFFFFFL);
                    r[i] = (int) Math.max(diff, 0);
                }
                regs.setVPR(rt, r);
                break;
            }
            case 1672: { // vsum2sws
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 2; i++) {
                    long sum = b[2 * i];
                    for (int j = 0; j < 2; j++) {
                        sum += a[2 * i + j];
                    }
                    r[2 * i] = (int) Math.min(Math.max(sum, Integer.MIN_VALUE), Integer.MAX_VALUE);
                    r[2 * i + 1] = 0;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 1792: { // vsubsbs
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    int diff = a[i] - b[i];
                    r[i] = (byte) Math.min(Math.max(diff, -128), 127);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 1800: { // vsum4sbs
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    int sum = b[i];
                    for (int j = 0; j < 4; j++) {
                        sum += a[4 * i + j];
                    }
                    r[i] = Math.min(Math.max(sum, Integer.MIN_VALUE), Integer.MAX_VALUE);
                }
                regs.setVPR(rt, r);
                break;
            }
            case 1856: { // vsubshs
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    int diff = a[i] - b[i];
                    r[i] = (short) Math.min(Math.max(diff, -32768), 32767);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 1920: { // vsubsws
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    long diff = (long) a[i] - b[i];
                    r[i] = (int) Math.min(Math.max(diff, Integer.MIN_VALUE), Integer.MAX_VALUE);
                }
                regs.setVPR(rt, r);
                break;
            }
            case 1928: { // vsumsws
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    long sum = b[i];
                    for (int j = 0; j < 4; j++) {
                        sum += a[i * 4 + j];
                    }
                    r[i] = (int) Math.min(Math.max(sum, Integer.MIN_VALUE), Integer.MAX_VALUE);
                }
                regs.setVPR(rt, r);
                break;
            }
            default:
                System.out.printf("Subcaso no soportado: case3=0x%X%n", case3);
        }

        switch (case4) {
            case 6: { // vcmpequb
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) (a[i] == b[i] ? 0xFF : 0x00);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 70: { // vcmpequh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    r[i] = (short) (a[i] == b[i] ? 0xFFFF : 0x0000);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 134: { // vcmpequw
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i] == b[i] ? 0xFFFFFFFF : 0x00000000;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 198: { // vcmpeqfp
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] b = intArrayToFloatArray(regs.getVPR(rb));
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i] == b[i] ? 0xFFFFFFFF : 0x00000000;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 454: { // vcmpgefp
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] b = intArrayToFloatArray(regs.getVPR(rb));
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i] >= b[i] ? 0xFFFFFFFF : 0x00000000;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 518: { // vcmpgtub
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) ((a[i] & 0xFF) > (b[i] & 0xFF) ? 0xFF : 0x00);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 582: { // vcmpgtuh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    r[i] = (short) ((a[i] & 0xFFFF) > (b[i] & 0xFFFF) ? 0xFFFF : 0x0000);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 646: { // vcmpgtuw
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = (a[i] & 0xFFFFFFFFL) > (b[i] & 0xFFFFFFFFL) ? 0xFFFFFFFF : 0x00000000;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 710: { // vcmpgtfp
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] b = intArrayToFloatArray(regs.getVPR(rb));
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i] > b[i] ? 0xFFFFFFFF : 0x00000000;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 774: { // vcmpgtsb
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) (a[i] > b[i] ? 0xFF : 0x00);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 838: { // vcmpgtsh
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                short[] r = new short[8];
                for (int i = 0; i < 8; i++) {
                    r[i] = (short) (a[i] > b[i] ? 0xFFFF : 0x0000);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 902: { // vcmpgtsw
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i] > b[i] ? 0xFFFFFFFF : 0x00000000;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 966: { // vcmpbfp
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] b = intArrayToFloatArray(regs.getVPR(rb));
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = (Math.abs(a[i]) > Math.abs(b[i]) ? 0x80000000 : 0) | (a[i] > b[i] ? 0x40000000 : 0);
                }
                regs.setVPR(rt, r);
                break;
            }
            default:
                System.out.printf("Subcaso no soportado: case4=0x%X%n", case4);
        }

        switch (case5) {
            case 0: { // vsel
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] c = intArrayToByteArray(regs.getVPR(rc));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    r[i] = (byte) ((c[i] & 0xFF) != 0 ? b[i] : a[i]);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 2: { // vmr
                regs.setVPR(rt, regs.getVPR(ra));
                break;
            }
            case 4: { // vsplt
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] r = new byte[16];
                int idx = Utilities.ExtractBits(instr, 16, 20) & 0xF;
                byte val = a[idx];
                for (int i = 0; i < 16; i++) {
                    r[i] = val;
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 6: { // vperm128
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] c = intArrayToByteArray(regs.getVPR(rc));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    int idx = c[i] & 0x1F;
                    r[i] = idx < 16 ? a[idx] : b[idx - 16];
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 10: { // vnmsubfp
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] b = intArrayToFloatArray(regs.getVPR(rb));
                float[] c = intArrayToFloatArray(regs.getVPR(rc));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = -(a[i] * b[i] - c[i]);
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 11: { // vmaddcfp128
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] b = intArrayToFloatArray(regs.getVPR(rb));
                float[] c = intArrayToFloatArray(regs.getVPR(rc));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i] * c[i] + b[i];
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 14: { // vmsumubm
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                int[] c = regs.getVPR(rc);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    int sum = c[i];
                    for (int j = 0; j < 4; j++) {
                        sum += (a[4 * i + j] & 0xFF) * (b[4 * i + j] & 0xFF);
                    }
                    r[i] = sum;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 15: { // vmsummbm
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                int[] c = regs.getVPR(rc);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    int sum = c[i];
                    for (int j = 0; j < 4; j++) {
                        sum += a[4 * i + j] * b[4 * i + j];
                    }
                    r[i] = sum;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 18: { // vmsumuhm
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                int[] c = regs.getVPR(rc);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    int sum = c[i];
                    for (int j = 0; j < 2; j++) {
                        sum += (a[2 * i + j] & 0xFFFF) * (b[2 * i + j] & 0xFFFF);
                    }
                    r[i] = sum;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 19: { // vmsumuhs
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                int[] c = regs.getVPR(rc);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    long sum = c[i] & 0xFFFFFFFFL;
                    for (int j = 0; j < 2; j++) {
                        sum += (a[2 * i + j] & 0xFFFF) * (b[2 * i + j] & 0xFFFF);
                    }
                    r[i] = (int) Math.min(sum, 0xFFFFFFFFL);
                }
                regs.setVPR(rt, r);
                break;
            }
            case 20: { // vmsumshm
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                int[] c = regs.getVPR(rc);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    int sum = c[i];
                    for (int j = 0; j < 2; j++) {
                        sum += a[2 * i + j] * b[2 * i + j];
                    }
                    r[i] = sum;
                }
                regs.setVPR(rt, r);
                break;
            }
            case 21: { // vmsumshs
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                short[] b = intArrayToShortArray(regs.getVPR(rb));
                int[] c = regs.getVPR(rc);
                int[] r = new int[4];
                for (int i = 0; i < 4; i++) {
                    long sum = c[i];
                    for (int j = 0; j < 2; j++) {
                        sum += a[2 * i + j] * b[2 * i + j];
                    }
                    r[i] = (int) Math.min(Math.max(sum, Integer.MIN_VALUE), Integer.MAX_VALUE);
                }
                regs.setVPR(rt, r);
                break;
            }
            case 23: { // vmaddfp
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] b = intArrayToFloatArray(regs.getVPR(rb));
                float[] c = intArrayToFloatArray(regs.getVPR(rc));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i] * b[i] + c[i];
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 24: { // vmsum3fp128
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] b = intArrayToFloatArray(regs.getVPR(rb));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i] * b[i];
                    for (int j = 0; j < 3; j++) {
                        r[i] += a[(i + j + 1) % 4] * b[(i + j + 1) % 4];
                    }
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 25: { // vmsum4fp128
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] b = intArrayToFloatArray(regs.getVPR(rb));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i] * b[i];
                    for (int j = 0; j < 4; j++) {
                        if (j != i) {
                            r[i] += a[j] * b[j];
                        }
                    }
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 26: { // vpkswss128
                int[] a = regs.getVPR(ra);
                short[] r = new short[8];
                for (int i = 0; i < 4; i++) {
                    r[i] = (short) Math.min(Math.max(a[i] >> 16, -32768), 32767);
                    r[i + 4] = (short) Math.min(Math.max(a[i] & 0xFFFF, -32768), 32767);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 27: { // vpkshss128
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                byte[] r = new byte[16];
                for (int i = 0; i < 8; i++) {
                    r[i] = (byte) Math.min(Math.max(a[i], -128), 127);
                    r[i + 8] = (byte) Math.min(Math.max(a[i + 8], -128), 127);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 28: { // vpkswus128
                int[] a = regs.getVPR(ra);
                short[] r = new short[8];
                for (int i = 0; i < 4; i++) {
                    r[i] = (short) Math.min(Math.max(a[i] >> 16, 0), 65535);
                    r[i + 4] = (short) Math.min(Math.max(a[i] & 0xFFFF, 0), 65535);
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 29: { // vpkshus128
                short[] a = intArrayToShortArray(regs.getVPR(ra));
                byte[] r = new byte[16];
                for (int i = 0; i < 8; i++) {
                    r[i] = (byte) Math.min(Math.max(a[i], 0), 255);
                    r[i + 8] = (byte) Math.min(Math.max(a[i + 8], 0), 255);
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 30: { // vpkpx128
                int[] a = regs.getVPR(ra);
                int[] b = regs.getVPR(rb);
                short[] r = new short[8];
                for (int i = 0; i < 4; i++) {
                    r[i] = (short) (((a[i] >>> 16) & 0xF800) | ((a[i] >>> 13) & 0x7C0) | ((a[i] >>> 10) & 0x3E));
                    r[i + 4] = (short) (((b[i] >>> 16) & 0xF800) | ((b[i] >>> 13) & 0x7C0) | ((b[i] >>> 10) & 0x3E));
                }
                regs.setVPR(rt, shortArrayToIntArray(r));
                break;
            }
            case 42: { // vpermwi128
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] r = new byte[16];
                int perm = Utilities.ExtractBits(instr, 6, 10);
                for (int i = 0; i < 4; i++) {
                    int idx = (perm >> (2 * (3 - i))) & 0x3;
                    for (int j = 0; j < 4; j++) {
                        r[4 * i + j] = a[4 * idx + j];
                    }
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 43: { // vperm
                byte[] a = intArrayToByteArray(regs.getVPR(ra));
                byte[] b = intArrayToByteArray(regs.getVPR(rb));
                byte[] c = intArrayToByteArray(regs.getVPR(rc));
                byte[] r = new byte[16];
                for (int i = 0; i < 16; i++) {
                    int idx = c[i] & 0x1F;
                    r[i] = idx < 16 ? a[idx] : b[idx - 16];
                }
                regs.setVPR(rt, byteArrayToIntArray(r));
                break;
            }
            case 46: { // vmaddfp
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] b = intArrayToFloatArray(regs.getVPR(rb));
                float[] c = intArrayToFloatArray(regs.getVPR(rc));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = a[i] * b[i] + c[i];
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            case 47: { // vnmsubfp
                float[] a = intArrayToFloatArray(regs.getVPR(ra));
                float[] b = intArrayToFloatArray(regs.getVPR(rb));
                float[] c = intArrayToFloatArray(regs.getVPR(rc));
                float[] r = new float[4];
                for (int i = 0; i < 4; i++) {
                    r[i] = -(a[i] * b[i] - c[i]);
                }
                regs.setVPR(rt, floatArrayToIntArray(r));
                break;
            }
            default:
                System.out.printf("Subcaso no soportado: case5=0x%X%n", case5);
        }
    }

    private static byte[] intArrayToByteArray(int[] intArray) {
        byte[] bytes = new byte[16];
        for (int i = 0; i < 4; i++) {
            bytes[i * 4] = (byte) (intArray[i] >> 24);
            bytes[i * 4 + 1] = (byte) (intArray[i] >> 16);
            bytes[i * 4 + 2] = (byte) (intArray[i] >> 8);
            bytes[i * 4 + 3] = (byte) intArray[i];
        }
        return bytes;
    }

    private static int[] byteArrayToIntArray(byte[] byteArray) {
        int[] ints = new int[4];
        for (int i = 0; i < 4; i++) {
            ints[i] = ((byteArray[i * 4] & 0xFF) << 24)
                    | ((byteArray[i * 4 + 1] & 0xFF) << 16)
                    | ((byteArray[i * 4 + 2] & 0xFF) << 8)
                    | (byteArray[i * 4 + 3] & 0xFF);
        }
        return ints;
    }

    private static float[] intArrayToFloatArray(int[] intArray) {
        float[] floats = new float[4];
        for (int i = 0; i < 4; i++) {
            floats[i] = Float.intBitsToFloat(intArray[i]);
        }
        return floats;
    }

    private static int[] floatArrayToIntArray(float[] floatArray) {
        int[] ints = new int[4];
        for (int i = 0; i < 4; i++) {
            ints[i] = Float.floatToIntBits(floatArray[i]);
        }
        return ints;
    }

    private static short[] intArrayToShortArray(int[] intArray) {
        short[] shorts = new short[8];
        for (int i = 0; i < 4; i++) {
            shorts[2 * i] = (short) (intArray[i] >> 16);
            shorts[2 * i + 1] = (short) intArray[i];
        }
        return shorts;
    }

    private static int[] shortArrayToIntArray(short[] shortArray) {
        int[] ints = new int[4];
        for (int i = 0; i < 4; i++) {
            ints[i] = ((shortArray[2 * i] & 0xFFFF) << 16) | (shortArray[2 * i + 1] & 0xFFFF);
        }
        return ints;
    }
}
