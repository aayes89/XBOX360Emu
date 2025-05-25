package xbox360emu.CPU;

/**
 *
 * @author Slam CPU Registers for PowerPC emulation.
 */
public class Registers {

    // General registers
    private int PC;                        // Program Counter
    private int LR;                        // Link Register
    private int CTR;                       // Count Register
    private int XER;                       // Fixed-point Exception Register
    private int MSR;                       // Machine State Register        
    private int DEC;                       // Decrement    
    int TBL, TBU; // Time Base Lower/Upper
    int SRR0;     // Save/Restore Register 0 (for exceptions)
    int SRR1;     // Save/Restore Register 1 (for exceptions)
    int SPRG0, SPRG1, SPRG2, SPRG3; // Special Purpose Registers General

    // Floating-point
    private int FPSCR;                     // FP Status and Control
    private double[] FPR = new double[32]; // 32 double-prec regs

    // Condition Register (8 fields of 4 bits)
    private int CR;                        // 32-bit combined CR

    // Special Purpose Registers
    private int[] SPR = new int[1024];    // SPRs
    private int[] GQR = new int[8];       // Graphics Quantization

    // Vector registers (32 × 4 words = 128 bits)
    private int[][] VPR = new int[32][4];

    // Vector accumulators
    private long VACC;
    private long VPR_acc;

    // Special state
    private int reservation_addr;
    private boolean reservation_valid;
    private boolean running;
    private boolean trapFlag;

    public Registers() {
        // VPR already initialized to zeros
    }

    // PC, LR, CTR, XER, MSR
    public int getPC() {
        return PC;
    }

    public void setPC(int pc) {
        PC = pc;
    }

    public int getLR() {
        return LR;
    }

    public void setLR(int lr) {
        LR = lr;
    }

    public int getCTR() {
        return CTR;
    }

    public void setCTR(int ctr) {
        CTR = ctr;
    }

    public int getXER() {
        return XER;
    }

    public void setXER(int xer) {
        XER = xer;
    }

    public int getMSR() {
        return MSR;
    }

    public void setMSR(int msr) {
        MSR = msr;
    }

    // GPR
    private final int[] GPR = new int[32];

    public int getGPR(int reg) {
        if (reg < 0 || reg >= 32) {
            throw new IllegalArgumentException("Invalid GPR index");
        }
        return GPR[reg & 0x1F];
    }

    public void setGPR(int reg, int value) {
        if (reg < 0 || reg >= 32) {
            throw new IllegalArgumentException("Invalid GPR index");
        }
        GPR[reg & 0x1F] = value;
    }

    // Condition Register fields (4 bits per field)
    public int getCRValue() {
        return CR;
    }

    public void setCRValue(int value) {
        CR = value;
    }

    public int getCRField(int field) {
        int shift = (7 - (field & 0x7)) * 4;
        return (CR >> shift) & 0xF;
    }

    public void setCRField(int field, int flags4) {
        int shift = (7 - (field & 0x7)) * 4;
        CR = (CR & ~(0xF << shift)) | ((flags4 & 0xF) << shift);
    }

    // Floating-point regs
    public double getFPR(int idx) {
        return FPR[idx & 0x1F];
    }

    public void putFPR(int idx, double val) {
        FPR[idx & 0x1F] = val;
    }

    // Single-precision helpers
    public float getFPRFloat(int idx) {
        return (float) FPR[idx & 0x1F];
    }

    public void putFPRFloat(int idx, float val) {
        FPR[idx & 0x1F] = val;
    }

    // Double-precision helper
    public double getFPRDouble(int idx) {
        return FPR[idx & 0x1F];
    }

    public void putFPRDouble(int idx, double val) {
        FPR[idx & 0x1F] = val;
    }

    // SPR
    public int getSPR(int idx) {
        return SPR[idx];
    }

    public void putSPR(int idx, int val) {
        SPR[idx] = val;
    }

    // GQR
    public int getGQR(int idx) {
        return GQR[idx];
    }

    public void putGQR(int idx, int val) {
        GQR[idx] = val;
    }

    // Vector regs
    public int[] getVPR(int idx) {
        return VPR[idx & 0x1F];
    }

    public void setVPR(int idx, int[] val) {
        System.arraycopy(val, 0, VPR[idx & 0x1F], 0, VPR[idx & 0x1F].length);
    }

    // Vector accumulators
    public long getVACC() {
        return VACC;
    }

    public void setVACC(long vacc) {
        VACC = vacc;
    }

    public long getVPRAcc() {
        return VPR_acc;
    }

    public void setVPRAcc(long acc) {
        VPR_acc = acc;
    }

    // Reservation
    public int getReservationAddr() {
        return reservation_addr;
    }

    public void setReservationAddr(int addr) {
        reservation_addr = addr;
        reservation_valid = true;
    }

    public void clearReservation() {
        reservation_valid = false;
    }

    public boolean isReservationValid() {
        return reservation_valid;
    }

    // Flags
    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean r) {
        running = r;
    }

    public boolean isTrapFlag() {
        return trapFlag;
    }

    public void setTrapFlag(boolean f) {
        trapFlag = f;
    }

    // Time base
    public int getTBL() {
        return TBL;
    }

    public void setTBL(int TBL) {
        this.TBL = TBL;
    }

    public int getTBU() {
        return TBU;
    }

    public void setTBU(int TBU) {
        this.TBU = TBU;
    }

    // Registers
    public int getSRR0() {
        return SRR0;
    }

    public void setSRR0(int SRR0) {
        this.SRR0 = SRR0;
    }

    public int getSRR1() {
        return SRR1;
    }

    public void setSRR1(int SRR1) {
        this.SRR1 = SRR1;
    }

    public int getSPRG0() {
        return SPRG0;
    }

    public void setSPRG0(int SPRG0) {
        this.SPRG0 = SPRG0;
    }

    public int getSPRG1() {
        return SPRG1;
    }

    public void setSPRG1(int SPRG1) {
        this.SPRG1 = SPRG1;
    }

    public int getSPRG2() {
        return SPRG2;
    }

    public void setSPRG2(int SPRG2) {
        this.SPRG2 = SPRG2;
    }

    public int getSPRG3() {
        return SPRG3;
    }

    public void setSPRG3(int SPRG3) {
        this.SPRG3 = SPRG3;
    }

    public int getFPSCR() {
        return FPSCR;
    }

    public void setFPSCR(int FPSCR) {
        this.FPSCR = FPSCR;
    }

    public int getDEC() {
        return DEC;
    }

    public void setDEC(int DEC) {
        this.DEC = DEC;
    }

    public int getCR() {
        return CR;
    }

    public void setCR(int CR) {
        this.CR = CR;
    }
    
}
