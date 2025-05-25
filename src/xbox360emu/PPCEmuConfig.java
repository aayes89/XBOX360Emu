package xbox360emu;

/**
 *
 * @author Slam
 */
public class PPCEmuConfig {    
    // Supported binary formats
    enum BinaryType {
        ELF32_BE, ELF64_BE, RAW, UNKNOWN
    };
    
    public static int XBOX360_RAM_SIZE = 512 * 1024 * 1024;    

    // Memory regions
    long excBase = 0x00000000L;
    long excSize = 0x80000000L;
    long userBase = 0x10000L;    
    long userSize = 0x20000000L;    // 512MB user RAM

    // Framebuffer settings
    long fbBase = 0xC0000000L;
    long fbSize = 0x12C000;//0x004B0000ULL;    // width*height*bytes-per-pixel
    public int fbWidth = 640;//192-16; //320x240 640x480
    public int fbHeight = 480;//168-16;
    boolean textMode = true;
    public static boolean verbose_logging_ = true;
    // Profiling
    long cycle_count_ = 0;
    double cpu_frequency_Hz = 729000000.0; // 729 MHz
    //std::chrono::high_resolution_clock::time_point start_time_;
};
