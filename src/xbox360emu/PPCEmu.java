package xbox360emu;

import javax.media.opengl.GLProfile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import javax.swing.SwingUtilities;
import xbox360emu.CPU.CPU;
import xbox360emu.Exceptions.HaltException;
import xbox360emu.FILES.ELFLoader;
import xbox360emu.GPU.Display;
import xbox360emu.Memory.RAM;

/**
 *
 * @author Slam
 */
public class PPCEmu {

    Display display;
    PPCEmuConfig config;
    CPU mCPU;
    RAM mRAM;
    boolean keep;

    public PPCEmu(PPCEmuConfig config) {
        keep = true;
        this.config = config;
        mRAM = new RAM("RAM", config.XBOX360_RAM_SIZE, 0x00000000);
        mCPU = new CPU(mRAM);
        display = new Display(this.config.fbWidth, this.config.fbHeight);
    }

    public PPCEmu() {
        keep = true;
        config = new PPCEmuConfig();
        mRAM = new RAM("RAM", config.XBOX360_RAM_SIZE, 0x00000000);
        mCPU = new CPU(mRAM);
        display = new Display(this.config.fbWidth, this.config.fbHeight);
    }

    public void AutoLoad(String path) {
        byte[] data = ReadFileToVector(path);
        PPCEmuConfig.BinaryType fmt = DetectFormat(data);
        switch (fmt) {
            case RAW:
                LoadRAW(data, config.userBase);
                System.out.println("AutoLoad RAW: " + path + ", " + data.length + " bytes\n");
                break;
            case ELF32_BE:
                ELFLoader.loadELF32(data, mRAM, mCPU);
                System.out.println("ELF32 loaded!");
                break;
            case ELF64_BE:
                ELFLoader.loadELF64(data, mRAM, mCPU);
                System.out.println("ELF64 loaded!");
                break;
            default:
                System.out.println("Unknow binary format");
        }
        System.out.println("Entry PC: 0x" + Integer.toHexString(mCPU.getRegisters().getPC()) + "\n");
    }

    public void Run(int fps) {
        long frameTime = 1000 / fps;
        int fbSize = config.fbWidth * config.fbHeight * 4;
        int[] fbData = new int[config.fbWidth * config.fbHeight];

        System.out.println("Running emulation!");
        GLProfile.initSingleton();

        SwingUtilities.invokeLater(() -> {
            display.initDisplay();
            display.animate();
        });

        try {
            // Ejecutar n instrucciones
            while (keep) {
                mCPU.step();
                if (PPCEmuConfig.verbose_logging_) {
                    System.out.printf("PC: 0x%08X, r3: 0x%08X, r5: 0x%08X, r6: 0x%08X\n",
                            mCPU.getRegisters().getPC(), mCPU.getRegisters().getGPR(3),
                            mCPU.getRegisters().getGPR(5), mCPU.getRegisters().getGPR(6));
                }
                //}

                copyFramebufferFromRAM();

                if (PPCEmuConfig.verbose_logging_) {
                    StringBuilder regState = new StringBuilder();
                    regState.append(String.format("PC: 0x%08X\n", mCPU.getRegisters().getPC()));
                    for (int i = 0; i < 32; i++) {
                        regState.append(String.format("r%d: 0x%08X ", i, mCPU.getRegisters().getGPR(i)));
                        if ((i + 1) % 4 == 0) {
                            regState.append("\n");
                        }
                    }
                    display.printText(10, 45, regState.toString(), 0xFFFFFF);
                }

                SwingUtilities.invokeLater(() -> display.updateScreen());
            }
            Thread.sleep(frameTime);
        } catch (HaltException e) {
            System.out.println("CPU halted.");
            keep = false;
            mCPU.dumpState();
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            keep = false;
        }
        System.out.println("Emulation ended!");
    }

    private void copyFramebufferFromRAM() {
        int fbSize = config.fbWidth * config.fbHeight * 4;
        byte[] regData = mRAM.readRegion(0x00000000, fbSize);
        int[] fbData = new int[config.fbWidth * config.fbHeight];

        // Inicializar a negro
        Arrays.fill(fbData, 0x00000000);

        System.out.print("First 32 bytes of framebuffer: ");
        for (int j = 0; j < 32; j++) {
            System.out.printf("%02X ", regData[j] & 0xFF);
        }
        System.out.println();

        for (int i = 0, j = 0; i < fbData.length; i++, j += 4) {
            fbData[i] = ((regData[j + 3] & 0xFF) << 24)
                    | // A
                    ((regData[j] & 0xFF) << 16)
                    | // B
                    ((regData[j + 1] & 0xFF) << 8)
                    | // G
                    (regData[j + 2] & 0xFF);          // R
        }
        System.out.printf("First 4 pixels: %08X %08X %08X %08X\n", fbData[0], fbData[1], fbData[2], fbData[3]);
        display.getFb().fillFramebuffer(fbData);
    }

    private void copyFramebufferFromRAM0() {
        int w = config.fbWidth;
        int h = config.fbHeight;
        int fbBytes = w * h * 4;
        byte[] mem = mRAM.readRegion(0x00000000, fbBytes);
        int[] fb = new int[w * h];

        for (int i = 0, j = 0; i < fb.length; i++, j += 4) {
            // memoria: [B][G][R][A]
            int b = mem[j + 1] & 0xFF;
            int g = mem[j + 2] & 0xFF;
            int r = mem[j + 3] & 0xFF;
            int a = mem[j] & 0xFF;
            fb[i] = (r << 24) | (g << 16) | (b << 8) | a;
        }
        System.out.printf("First 4 pixels: %08X %08X %08X %08X\n", fb[0], fb[1], fb[2], fb[3]);
        display.getFb().fillFramebuffer(fb);
    }

    // ELF/RAW loaders
    private PPCEmuConfig.BinaryType DetectFormat(byte[] data) {
        if (data.length >= 4 && data[0] == 0x7F && data[1] == 'E' && data[2] == 'L' && data[3] == 'F') {
            boolean is64 = data[4] == 2, be = data[5] == 2;
            if (be && !is64) {
                return PPCEmuConfig.BinaryType.ELF32_BE;
            }
            if (be && is64) {
                return PPCEmuConfig.BinaryType.ELF64_BE;
            }
        }
        return PPCEmuConfig.BinaryType.RAW;
    }

    private void LoadRAW(byte[] data, long loadAddr) {
        mRAM.writeRegion(data, (int) loadAddr, data.length);
        System.out.println("Region writed!");
        mCPU.getRegisters().setPC((int) config.userBase);
    }

    private byte[] ReadFileToVector(String path) {
        try {
            return Files.readAllBytes(Paths.get(path));
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        return null;
    }

    /*Getters and Setters*/
    public Display getDisplay() {
        return display;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public PPCEmuConfig getConfig() {
        return config;
    }

    public void setConfig(PPCEmuConfig config) {
        this.config = config;
    }

    public CPU getmCPU() {
        return mCPU;
    }

    public void setmCPU(CPU mCPU) {
        this.mCPU = mCPU;
    }

    public RAM getmRAM() {
        return mRAM;
    }

    public void setmRAM(RAM mRAM) {
        this.mRAM = mRAM;
    }

    public boolean isKeep() {
        return keep;
    }

    public void setKeep(boolean keep) {
        this.keep = keep;
    }

}
