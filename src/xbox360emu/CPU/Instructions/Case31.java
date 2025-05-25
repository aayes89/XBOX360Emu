package xbox360emu.CPU.Instructions;

import xbox360emu.CPU.Registers;
import xbox360emu.Memory.RAM;
import xbox360emu.PPCEmuConfig;
import xbox360emu.UTILS.Utilities;

/**
 *
 * @author Slam
 */
public class Case31 {

    public static void case_31(int instr, Registers regs, RAM mem) {
        int sub21_29 = Utilities.ExtractBits(instr, 21, 29); // Para algunas instrucciones
        int sub21_30 = Utilities.ExtractBits(instr, 21, 30); // Para la mayoría        
        int rt = Utilities.ExtractBits(instr, 6, 10);
        int ra = Utilities.ExtractBits(instr, 11, 15);
        int rb = Utilities.ExtractBits(instr, 16, 20);
        int addr = 0;
        System.out.printf("Instr: 0x%08X, Extracted sub21_30: 0x%X\n", instr, Utilities.ExtractBits(instr, 21, 30));
        //System.out.printf("sub21_30 = 0x%X at PC=0x%08X\n", sub21_30, (regs.getPC()));
        // ————— Grupo 1: sub21_29 —————
        switch (sub21_29) {
            case 413: // sradi: arithmetic right shift immediate
                int sh = Utilities.ExtractBits(instr, 16, 20);
                int va  = regs.getGPR(ra);
                regs.setGPR(rt, va  >> sh);
                // Opcional: Actualizar XER si es necesario según la especificación
                return;
        }

        // ————— Grupo 2: sub21_30 —————
        switch (sub21_30) {
            case 0: // cmp
                int diff = regs.getGPR(ra) - regs.getGPR(rb);
                regs.setCRField(0, (diff < 0 ? 8 : 0) | (diff > 0 ? 4 : 0) | (diff == 0 ? 2 : 0));
                break;
            case 4: // tw (trap word)
                int tocr = Utilities.ExtractBits(instr, 21, 25);
                diff = regs.getGPR(ra) - regs.getGPR(rb);
                boolean trap
                        = ((tocr & 4) != 0 && diff == 0)
                        || ((tocr & 8) != 0 && diff > 0)
                        || ((tocr & 2) != 0 && diff < 0);
                if (trap) {
                    Utilities.TriggerException(tocr, regs);/* esca. vector data */
                }
                break;
            case 6:  // lvsl
                //int rt2 = rt;
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                // Utilidad que devuelve un vector de 16 enteros
                int[] vector = Utilities.loadVectorShiftLeft(mem, addr);
                // Guardar en VPR[rt]
                regs.setVPR(rt, vector);
                break;
            case 7: // lvebx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                int b = Byte.toUnsignedInt(mem.readByte(addr));
                int byteIndex = addr % 16; // Posición en el vector
                int[] mvector = regs.getVPR(rt); // Obtener vector existente
                if (mvector == null) {
                    mvector = new int[16]; // Inicializar si es nulo
                }
                mvector[byteIndex] = b;
                regs.setVPR(rt, mvector);
                break;
            case 19: // mfcr
                regs.setGPR(rt, regs.getCRValue());
                break;

            case 20: // lwarx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                if (addr % 4 != 0) {
                    throw new RuntimeException("Unaligned lwarx");
                }
                regs.setGPR(rt, mem.readWord(addr));
                regs.setReservationAddr(addr);
                break;
            case 21: // ldx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setGPR(rt, mem.readWord(addr));
                break;
            case 23: // lwzx
                //addr = regs.getGPR(ra) + regs.getGPR(rb);
                //regs.setGPR(rt, mem.readWord(addr));         
                MemoryInstructions.lwz(instr, regs, mem);
                break;

            case 24: // slwx
                int shift = regs.getGPR(rb) & 0x1F;
                regs.setGPR(rt, regs.getGPR(ra) << shift);
                break;

            case 26: // cntlzwx
                regs.setGPR(rt, Integer.numberOfLeadingZeros(regs.getGPR(ra)));
                break;

            case 27: // sldx (rotate left)
                shift = regs.getGPR(rb) & 0x1F;
                int v = regs.getGPR(ra);
                regs.setGPR(rt, (v << shift) | (v >>> (32 - shift)));
                break;

            case 28: // andx                
                regs.setGPR(rt, regs.getGPR(ra) & regs.getGPR(rb));
                break;

            case 32: // cmpl (unsigned compare)
                long ua = Integer.toUnsignedLong(regs.getGPR(ra));
                long ub = Integer.toUnsignedLong(regs.getGPR(rb));
                regs.setCRField(0, (ua < ub ? 8 : 0) | (ua > ub ? 4 : 0) | (ua == ub ? 2 : 0));
                break;

            case 38: // lvsr
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setVPR(rt, Utilities.loadVectorShiftRight(mem, addr));
                break;
            case 39:  // lvehx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                int h = mem.readHalfWord(addr);// mmu->Read16(addr);			
                int[] R = regs.getVPR(rt);
                R[0] = (h >> 8);
                R[1] = (h);
                // resto de bytes indefinidos (según especificación VMX)
                break;
            case 40:  // subf RT,RA,RB → RT ← RB – RA			
                regs.setGPR(rt, regs.getGPR(rb) - regs.getGPR(ra));
                break;
            case 53:  // ldux
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setGPR(rt, mem.readWord(addr));
                break;
            case 54:  // dcbst
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                Utilities.DCACHE_Store(addr);
                break;
            case 55: // lwzux
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setGPR(rt, mem.readWord(addr));
                regs.setGPR(ra, addr);
                break;
            case 58: // cntlzdx                
                rt = Utilities.ExtractBits(instr, 6, 10);
                ra = Utilities.ExtractBits(instr, 11, 15);
                rb = Utilities.ExtractBits(instr, 16, 20);
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                // alignment not required for byte-granularity read
                int val = mem.readWord(addr);
                // count leading zeros in 32-bit value
                int lz = Integer.numberOfLeadingZeros(val);
                regs.setGPR(rt, lz);
                break;
            case 60:  // andcx
                regs.setGPR(rt, regs.getGPR(ra) & ~regs.getGPR(rb));
                break;
            case 68:  // td
                int mtocr = Utilities.ExtractBits(instr, 21, 25);
                int raVal = regs.getGPR(ra);
                int rbVal = regs.getGPR(rb);
                int mdiff = raVal - rbVal;
                int andOp2 = mtocr & 2;
                int andOp4 = mtocr & 4;
                int andOp8 = mtocr & 8;
                boolean mtrap = (andOp4 == 0 ? true : false && (mdiff == 0)) || (andOp8 > 0 ? true : false && (mdiff > 0)) || (andOp2 < 0 ? true : false && (mdiff < 0));
                if (mtrap) {
                    Utilities.TriggerException(0, regs);//PPU_EX_DATASTOR);
                }
                break;
            case 71: // lvewx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                int fw = mem.readWord(addr);
                int[] vec = new int[4];
                vec[0] = fw; // first 32-bit lane
                // remaining lanes undefined, set zero
                regs.setVPR(rt, vec);
                break;
            case 83:  // mfmsr
                regs.setGPR(rt, regs.getMSR());
                break;
            case 84:  // ldarx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setGPR(rt, mem.readWord(addr));
                regs.setReservationAddr(addr);
                break;
            case 86:  // dcbf
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                Utilities.DCACHE_Flush(addr);
                break;
            case 87:  // lbzx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setGPR(rt, mem.readHalfWord(addr));
                break;
            case 103:  // lvx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setVPR(rt, new int[]{mem.readWord(addr)});
                break;
            case 104:  // neg RT,RA → RT ← 0 – RA
                regs.setGPR(rt, -1 * regs.getGPR(ra));
                break;
            case 119:  // lbzux
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setGPR(rt, mem.readHalfWord(addr));
                regs.setGPR(ra, addr);
                break;
            case 124:  // norx
                regs.setGPR(rt, ~(regs.getGPR(ra) | regs.getGPR(rb)));
                break;
            case 135: // stvebx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                // extract lowest byte from first lane
                int lane0 = regs.getVPR(rt)[0];
                byte nb = (byte) (lane0 & 0xFF);
                mem.writeByte(addr, nb);
                break;
            case 144:  // mtcrf
                int mask = Utilities.ExtractBits(instr, 6, 10) << ((7 - Utilities.ExtractBits(instr, 11, 15)) * 4);
                regs.setCRValue((regs.getCRValue() & ~mask) | (regs.getGPR(rt) & mask));
                break;
            case 146:  // mtmsr
                regs.setMSR(regs.getGPR(rt));
                break;
            case 149:  // stdx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                mem.writeDoubleWord(addr, (regs.getGPR(rt + 1) << 32) | regs.getGPR(rt));
                break;
            case 150: // stwcx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                if (regs.isReservationValid() && regs.getReservationAddr() == addr) {
                    mem.writeWord(addr, regs.getGPR(rt));
                    regs.setCRField(0, 2); // EQ = 1 (éxito)
                } else {
                    regs.setCRField(0, 8); // LT = 1 (fallo)
                }
                regs.clearReservation();
                break;
            case 151:  // stwx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                mem.writeWord(addr, regs.getGPR(rt));
                break;
            case 167:  // stvehx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                int[] mR = regs.getVPR(rt);
                int mh = (mR[0] << 8) | mR[1];
                mem.writeHalfWord(addr, mh);
                break;
            case 178:  // mtmsrd
                regs.setMSR(regs.getGPR(rt));
                break;
            case 181:  // stdux
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                mem.writeWord(addr, regs.getGPR(rt));
                regs.setGPR(ra, addr);
                break;
            case 183:  // stwux
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                mem.writeWord(addr, regs.getGPR(rt));
                regs.setGPR(ra, addr);
                break;
            case 199:  // stvewx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                mem.writeDoubleWord(addr, regs.getVPR(rt)[0]);
                break;
            case 202:  // subfze
                rt = Utilities.ExtractBits(instr, 6, 10);
                ra = Utilities.ExtractBits(instr, 11, 15);
                int results = 0 - (regs.getGPR(ra) - (1 - ((regs.getXER() >> 29) & 1)));
                regs.setGPR(rt, results);
                if (results > 0xFFFFFFFF || results < 0) {
                    regs.setXER(regs.getXER() | 0x20000000); // Set CA
                } else {
                    regs.setXER(regs.getXER() & ~0x20000000); // Clear CA
                }
                System.out.println("[INFO] Executing subfze: r" + rt + "=0x" + Integer.toHexString(regs.getGPR(rt)) + ", XER=0x" + regs.getXER() + "\n");
                break;
            case 214:  // stdcx
                if (regs.isReservationValid() && regs.getReservationAddr() == (regs.getGPR(ra) + regs.getGPR(rb))) {
                    mem.writeDoubleWord(regs.getReservationAddr(), (regs.getGPR(rt + 1) << 32) | regs.getGPR(rt));
                    regs.setXER(regs.getXER() | 0x200);
                } else {
                    regs.setXER(regs.getXER() & ~0x200);
                }
                regs.clearReservation();
                break;
            case 215:  // stbx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                int mval = regs.getGPR(rt) & 0xFF;
                mem.writeHalfWord(addr, mval);
                break;
            case 231:  // stvx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                mem.writeDoubleWord(addr, regs.getVPR(rt)[0]);
                break;
            case 246:  // dcbtst
                // no-op
                break;
            case 247:  // stbux
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                mem.writeHalfWord(addr, regs.getGPR(rt) & 0xFF);
                regs.setGPR(ra, addr);
                break;
            case 266: // add
                regs.setGPR(rt, regs.getGPR(ra) + regs.getGPR(rb));
                break;
            case 278:  // dcbt
                // no-op
                break;
            case 279:  // lhzx
                //addr = regs.getGPR(ra) + regs.getGPR(rb);
                //regs.setGPR(rt, mem.readHalfWord(addr));
                MemoryInstructions.lhz(instr, regs, mem);
                break;
            case 284:  // eqvx
                regs.setGPR(rt, ~(regs.getGPR(ra) ^ regs.getGPR(rb)));
                break;
            case 311:  // lhzux
                //addr = regs.getGPR(ra) + regs.getGPR(rb);
                //regs.setGPR(rt, mem.readHalfWord(addr));
                //regs.setGPR(ra, addr);
                MemoryInstructions.lhzu(instr, regs, mem);
                break;
            case 316: // xorx
                //regs.setGPR(rt, regs.getGPR(ra) ^ regs.getGPR(rb));
                LogicalInstructions.xori(instr, regs, mem);
                break;
            case 332:
                // RLWINM RT,RA,SH,MB,ME
                BitwiseInstructions.rlwinm(instr, regs, mem);
                /*int sh = Utilities.ExtractBits(instr, 16, 20);
                int mb = Utilities.ExtractBits(instr, 11, 15);
                int me = Utilities.ExtractBits(instr, 6, 10);
                // rotate left
                int mv = (regs.getGPR(ra) << sh) | (regs.getGPR(ra) >> (32 - sh));
                // build mask from mb..me
                int nmask = 0;
                for (int i = 0; i < 32; i++) {
                    boolean bit = (mb <= me)
                            ? (i >= mb && i <= me)
                            : (i >= mb || i <= me);
                    nmask |= (bit == true ? 1 : 0) << (31 - i);
                }
                regs.setGPR(rt, mv & nmask);*/
                break;
            case 339: // mfspr
                int spr = (Utilities.ExtractBits(instr, 11, 15) << 5) | Utilities.ExtractBits(instr, 16, 20);
                if (spr == 8) // LR register
                {
                    regs.setGPR(rt, regs.getLR());
                } else // other SPRs fall back to mfspr
                {
                    regs.setGPR(rt, regs.getSPR(spr));
                }
                break;
            case 341: // lwax
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setGPR(rt, mem.readWord(addr));
                regs.setGPR(ra, addr);
                break;
            case 343: // lhax
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setGPR(rt, mem.readHalfWord(addr));
                regs.setGPR(ra, addr);
                break;
            case 359: // lvxl
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setVPR(rt, new int[]{mem.readWord(addr)});
                regs.setGPR(ra, addr);
                break;
            case 371: // mftb
                regs.setGPR(rt, regs.getTBL());
                break;
            case 373: // lwaux
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setGPR(rt, mem.readWord(addr));
                regs.setGPR(ra, addr + 4);
                break;
            case 375: // lhaux
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setGPR(rt, mem.readHalfWord(addr));
                regs.setGPR(ra, addr + 2);
                break;
            case 407: // sthx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                mem.writeHalfWord(addr, regs.getGPR(rt) & 0xFFFF);
                break;
            case 412: // orcx
                regs.setGPR(rt, regs.getGPR(ra) | ~regs.getGPR(rb));
                break;
            case 439: // sthux
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                mem.writeHalfWord(addr, regs.getGPR(rt) & 0xFFFF);
                regs.setGPR(ra, addr);
                break;
            case 444: // orx
                //regs.setGPR(rt, regs.getGPR(ra) | regs.getGPR(rb));
                if (ra == rb) {
                    regs.setGPR(rt, regs.getGPR(ra)); // Comportamiento de mr
                } else {
                    regs.setGPR(rt, regs.getGPR(ra) | regs.getGPR(rb)); // Comportamiento de orx
                }
                break;
            case 467: // mtspr
                int rtt = Utilities.ExtractBits(instr, 6, 10);
                int nspr = (Utilities.ExtractBits(instr, 16, 20) << 5) | Utilities.ExtractBits(instr, 11, 15);
                int nval = regs.getGPR(rtt);
                switch (nspr) {
                    case 9: // CTR
                        regs.setCTR(nval);
                        System.out.printf("[INFO] mtctr: CTR=0x%08X\n", regs.getCTR());
                        break;
                    case 8: // LR
                        regs.setLR(nval);
                        System.out.printf("[INFO] mtspr: LR=0x%08X\n", regs.getLR());
                        break;
                    default:
                        regs.putSPR(nspr, nval);
                        System.out.printf("[INFO] mtspr: SPR[%d]=0x%08X\n", nspr, nval);
                        break;
                }
                break;
            case 470: // dcbi
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                Utilities.DCACHE_CleanInvalidate(addr);
                break;
            case 476: // nandx
                regs.setGPR(rt, ~(regs.getGPR(ra) & regs.getGPR(rb)));
                break;
            case 487: // stvxl
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                mem.writeDoubleWord(addr, regs.getVPR(rt)[0]);
                regs.setGPR(ra, addr);
                break;
            case 512: // mcrxr
                int crm = Utilities.ExtractBits(instr, 11, 15);
                regs.setCRField(0, (regs.getCRValue() & ~(0xF << ((7 - crm) * 4))) | ((regs.getXER() & 0xF) << ((7 - crm) * 4)));
                break;
            case 519: // lvlx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setVPR(rt, Utilities.loadVectorShiftLeft(mem, addr));
                break;
            case 528:  // bctr
                regs.setPC(regs.getCTR());
                break;
            case 532: // ldbrx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                int mw = mem.readWord(addr);
                regs.setGPR(rt, Integer.reverseBytes(mw));
                break;
            case 533: // lswx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setGPR(rt, mem.readHalfWord(addr) | (mem.readHalfWord(addr + 2) << 16));
                break;
            case 534: // lwbrx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                int nw = mem.readWord(addr);
                regs.setGPR(rt, Integer.reverseBytes(nw));
                break;
            case 535: // lfsx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                int wrd = mem.readWord(addr);
                float f = Float.intBitsToFloat(wrd);
                regs.putFPR(rt, f);
                break;
            case 536: // srwx
                int nsh = regs.getGPR(rb) & 0x1F;
                regs.setGPR(rt, regs.getGPR(ra) >> nsh);
                break;
            case 539: // srdx
                int msh = regs.getGPR(rb) & 0x1F;
                regs.setGPR(rt, regs.getGPR(ra) >> msh);
                break;
            case 551: // lvrx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setVPR(rt, Utilities.loadVectorShiftRight(mem, addr));
                break;
            case 567: // lfsux
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                int wi = mem.readWord(addr);
                double nf = wi;// memcpy(&f, &w, 4);
                regs.putFPR(rt, nf);
                regs.setGPR(ra, addr);
                break;
            case 597: // lswi
                int byteCount = Utilities.ExtractBits(instr, 16, 21);
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                for (int i = 0; i < byteCount; i += 4) {
                    regs.setGPR(rt + i / 4, mem.readWord(addr + i));
                }
                break;
            case 598: // sync
                //__sync_synchronize();
                break;
            case 599: // lfdx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                long lw = mem.readDoubleWord(addr);
                double d = Double.longBitsToDouble(lw);
                regs.putFPR(rt, d);
                break;
            case 631: // lfdux
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                long dw = mem.readDoubleWord(addr);
                double dd = Double.longBitsToDouble(dw);
                //memcpy( & d,  & w, 8);
                regs.putFPR(rt, dd);
                regs.setGPR(ra, addr);
                break;
            case 647: // stvlx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                mem.writeDoubleWord(addr, regs.getVPR(rt)[0]);
                regs.setGPR(ra, addr);
                break;
            case 660: // stdbrx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                long lnw = mem.readDoubleWord(addr);
                mem.writeDoubleWord(addr, Long.reverseBytes(lnw));
                break;
            case 661: // stswx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                mem.writeHalfWord(addr, regs.getGPR(rt) & 0xFFFF);
                mem.writeHalfWord(addr + 2, (regs.getGPR(rt) >> 16) & 0xFFFF);
                break;
            case 662: // stwbrx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                int tw = regs.getGPR(rt);
                mem.writeWord(addr, Integer.reverseBytes(tw));
                break;
            case 663: // stfsx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                float ff = (float) regs.getFPR(rt);
                mem.writeWord(addr, Float.floatToIntBits(ff));
                break;
            case 679: // stvrx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                mem.writeDoubleWord(addr, regs.getVPR(rt)[0]);
                break;
            case 695: // stfsux
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                double tf = regs.getFPR(rt);
                //memcpy( & w,  & f, 4);
                mem.writeWord(addr, (int) tf);
                regs.setGPR(ra, addr);
                break;
            case 725: // stswi
                int byteCounts = Utilities.ExtractBits(instr, 16, 21);
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                for (int i = 0; i < byteCounts; i += 4) {
                    mem.writeWord(addr + i, regs.getGPR(rt + i / 4));
                }
                break;
            case 727: // stfdx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                double dx = regs.getFPR(rt);
                //memcpy( & w,  & d, 8);
                mem.writeDoubleWord(addr, Double.doubleToLongBits(dx));
                break;
            case 759: // stfdux
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                double dz = regs.getFPR(rt);
                //memcpy( & w,  & d, 8);
                mem.writeDoubleWord(addr, Double.doubleToLongBits(dz));
                regs.setGPR(ra, addr);
                break;
            case 775: // lvlxl
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setVPR(rt, Utilities.loadVectorShiftLeft(mem, addr));
                break;
            case 790: // lhbrx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                int bh = mem.readHalfWord(addr);
                regs.setGPR(rt, Integer.reverse(bh));//_byteswap_ulong(h); //swap16
                break;
            case 792: // srawx
                int bsh = regs.getGPR(rb) & 0x1F;
                regs.setGPR(rt, regs.getGPR(ra) >> bsh);
                break;
            case 794: // sradx
                int vsh = regs.getGPR(rb) & 0x1F;
                int va  = regs.getGPR(ra);
                regs.setGPR(rt, va  >> vsh);
                break;
            case 807: // lvrxl
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                regs.setVPR(rt, Utilities.loadVectorShiftRight(mem, addr));
                break;
            case 824: // srawix
                int ksh = Utilities.ExtractBits(instr, 16, 20) & 0x1F;
                int nva = regs.getGPR(ra);
                regs.setGPR(rt, nva >> ksh);
                break;
            case 854: // eieio
                //__sync_synchronize();
                break;
            case 903: // stvlxl
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                mem.writeDoubleWord(addr, regs.getVPR(rt)[0]);
                regs.setGPR(ra, addr);
                break;
            case 918: // sthbrx
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                int kh = regs.getGPR(rt) & 0xFFFF;
                mem.writeHalfWord(addr, Integer.reverseBytes(kh));//__builtin_bswap16(h));
                break;
            case 922: // extshx
                short signedHalf = (short) (regs.getGPR(ra) & 0xFFFF);
                regs.setGPR(rt, (int) signedHalf);
                break;
            case 935: // stvrxl
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                mem.writeDoubleWord(addr, regs.getVPR(rt)[0]);
                regs.setGPR(ra, addr);
                break;
            case 954: // extsbx
                byte signedByte = (byte) (regs.getGPR(ra) & 0xFF);
                regs.setGPR(rt, (int) signedByte);
                break;
            case 982: // icbi
                addr = regs.getGPR(ra) + regs.getGPR(rb);
                Utilities.ICACHE_Invalidate(addr);
                break;
            case 983: // stfiwx
                int ba = Utilities.ExtractBits(instr, 11, 15);
                int offset = Utilities.ExtractBits(instr, 16, 20);
                addr = regs.getGPR(ba) + offset;
                double mf = regs.getFPR(rt);
                //memcpy( & w,  & f, 4);
                mem.writeWord(addr, (int) mf);
                break;
            case 986: // extswx
                int xw = mem.readHalfWord(regs.getGPR(ra) + regs.getGPR(rb));
                regs.setGPR(rt, xw);
                break;
            case 992: // divwx (divide word)
                int dividendo = regs.getGPR(ra);
                int divisor = regs.getGPR(rb);
                if (divisor != 0) {
                    regs.setGPR(rt, dividendo / divisor);
                } else {
                    System.out.println("Division by zero not possible!");
                }
                break;
            case 995: // divwux (divide word unsigned)
                long uaa = Integer.toUnsignedLong(regs.getGPR(ra));
                long ubb = Integer.toUnsignedLong(regs.getGPR(rb));
                if (ubb != 0) {
                    regs.setGPR(rt, (int) (uaa / ubb));
                } else {
                    System.out.println("Division by zero not possible!");
                }
                break;
            case 996: // stwbrx
                int rs = (instr >> 21) & 0x1F;
                ra = (instr >> 16) & 0x1F;
                rb = (instr >> 11) & 0x1F;

                int kval = regs.getGPR(rs);
                int base = (ra == 0) ? 0 : regs.getGPR(ra);
                offset = regs.getGPR(rb);
                addr = base + offset;

                // Byte-reverse: almacena val en orden inverso
                mem.writeByte(addr, (byte) ((kval >> 0) & 0xFF));  // byte más bajo
                mem.writeByte(addr + 1, (byte) ((kval >> 8) & 0xFF));
                mem.writeByte(addr + 2, (byte) ((kval >> 16) & 0xFF));
                mem.writeByte(addr + 3, (byte) ((kval >> 24) & 0xFF));  // byte más alto
                if (PPCEmuConfig.verbose_logging_) {
                    System.out.printf("Executing [stwbrx] mem[0x%08X] = byte-reverse of r%d (0x%08X)%n", addr, rs, kval);
                }
                break;
            case 997: // divdx (divide doubleword)
                long la = regs.getGPR(ra);
                long lb = regs.getGPR(rb);
                regs.setGPR(rt, (int) (la / lb));
                break;
            case 998: // divdux (divide doubleword unsigned)
                long ula = Long.divideUnsigned(regs.getGPR(ra), regs.getGPR(rb));
                regs.setGPR(rt, (int) ula);
                break;
            case 1001: // mulhwx (multiply high word)
                long prod = (long) regs.getGPR(ra) * (long) regs.getGPR(rb);
                regs.setGPR(rt, (int) (prod >> 32));
                break;
            case 1023: // isync (instruction synchronize)
                // No-op en este emulador (sincronización de pipeline)
                break;
            default:
                throw new UnsupportedOperationException(
                        String.format("Instrucción no implementada: opcode 31, sub21_30=0x%X", sub21_30));
        }
    }
}
