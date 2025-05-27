package xbox360emu.GPU;

/*
* @author Slam
 */
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Framebuffer extends JPanel {

    private final int width;
    private final int height;
    private final int[] framebuffer;
    private final BufferedImage image;

    public Framebuffer(int width, int height, int[] framebuffer) {
        this.width = width;
        this.height = height;
        this.framebuffer = framebuffer;

        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        setBackground(Color.BLACK); // Fondo negro

        // Sincroniza datos del framebuffer con la imagen
        int[] imgData = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(framebuffer, 0, imgData, 0, framebuffer.length);

        int panelW = getWidth();
        int panelH = getHeight();

        float scaleX = (float) panelW / width;
        float scaleY = (float) panelH / height;
        float scale = Math.min(scaleX, scaleY);

        int displayW = (int) (width * scale);
        int displayH = (int) (height * scale);
        int offsetX = (panelW - displayW) / 2;
        int offsetY = (panelH - displayH) / 2;

        g.drawImage(image, offsetX, offsetY, displayW, displayH, null);
    }

    public int[] getFb() {
        return framebuffer;
    }

    public void fillFramebuffer(int[] fbData) {
        System.arraycopy(fbData, 0, this.framebuffer, 0, fbData.length);

    }
}
