package xbox360emu;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Slam
 */
public class XBOX360Emu {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {         
        JFileChooser jfc = new JFileChooser("./src/xbox360emu/TESTS");
        jfc.setFileFilter(new FileNameExtensionFilter("Xbox 360 file and binaries", "bin", "elf", "elf32"));
        jfc.showOpenDialog(null);
        if (jfc.getSelectedFile() != null) {
            PPCEmuConfig config = new PPCEmuConfig();
            PPCEmu emu = new PPCEmu(config);
            try {
                emu.AutoLoad(jfc.getSelectedFile().getAbsolutePath());
                System.out.println("Running emulation!");                                
                emu.Run(60); // FPS
            } catch (Exception e) {
                System.err.println("Main: " + e.getMessage());
            }
        } else {
            System.out.println("No binary selected!");
            System.exit(0);
        }

    }
}
