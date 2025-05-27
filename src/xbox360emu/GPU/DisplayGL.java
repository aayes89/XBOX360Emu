package xbox360emu.GPU;

import com.jogamp.opengl.util.FPSAnimator;
import javax.swing.*;
import java.util.Arrays;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
/*
* @autor Slam
*/

public class Display {

    private final int width;
    private final int height;
    private final int[] framebuffer;
    private final Framebuffer fb;
    private JFrame frame;
    private GLJPanel glPanel;

    public Display(int width, int height) {
        this.width = width;
        this.height = height;
        this.framebuffer = new int[width * height];
        this.fb = new Framebuffer(this.width, this.height, framebuffer);
    }

    public void initDisplay() {
        GLProfile profile = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(profile);
        glPanel = new GLJPanel(caps);
        glPanel.setSize(width - 10, height - 10);
        glPanel.addGLEventListener(fb);

        frame = new JFrame("Xbox 360 Emulator Framebuffer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(glPanel);
        frame.setSize(width, height);
        frame.setVisible(true);
    }

    public void animate() {
        new FPSAnimator(glPanel, 60).start();
    }

    public Framebuffer getFb() {
        return fb;
    }

    public void clear(int color) {
        Arrays.fill(framebuffer, color);
    }

    public void setPixel(int x, int y, int color) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            framebuffer[y * width + x] = color;
        }
    }

    // === Primitivas ===
    public void drawLine(int x0, int y0, int x1, int y1, int color) {
        int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int err = dx + dy;

        while (true) {
            setPixel(x0, y0, color);
            if (x0 == x1 && y0 == y1) {
                break;
            }
            int e2 = 2 * err;
            if (e2 >= dy) {
                err += dy;
                x0 += sx;
            }
            if (e2 <= dx) {
                err += dx;
                y0 += sy;
            }
        }
    }

    public void drawRect(int x, int y, int w, int h, int color) {
        drawLine(x, y, x + w, y, color);
        drawLine(x + w, y, x + w, y + h, color);
        drawLine(x + w, y + h, x, y + h, color);
        drawLine(x, y + h, x, y, color);
    }

    public void fillRect(int x, int y, int w, int h, int color) {
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                setPixel(x + j, y + i, color);
            }
        }
    }

    public void fillSquare(int x, int y, int size, int color) {
        fillRect(x, y, size, size, color);
    }

    public void drawCircle(int cx, int cy, int radius, int color) {
        int x = radius, y = 0, err = 0;
        while (x >= y) {
            plotCirclePoints(cx, cy, x, y, color);
            y++;
            err += 2 * y + 1;
            if (err > 2 * x) {
                x--;
                err -= 2 * x + 1;
            }
        }
    }

    private void plotCirclePoints(int cx, int cy, int x, int y, int color) {
        setPixel(cx + x, cy + y, color);
        setPixel(cx + y, cy + x, color);
        setPixel(cx - y, cy + x, color);
        setPixel(cx - x, cy + y, color);
        setPixel(cx - x, cy - y, color);
        setPixel(cx - y, cy - x, color);
        setPixel(cx + y, cy - x, color);
        setPixel(cx + x, cy - y, color);
    }

    public void fillCircle(int cx, int cy, int radius, int color) {
        for (int y = -radius; y <= radius; y++) {
            for (int x = -radius; x <= radius; x++) {
                if (x * x + y * y <= radius * radius) {
                    setPixel(cx + x, cy + y, color);
                }
            }
        }
    }

    public void fillTriangle(int x0, int y0, int x1, int y1, int x2, int y2, int color) {
        int minX = Math.min(x0, Math.min(x1, x2));
        int maxX = Math.max(x0, Math.max(x1, x2));
        int minY = Math.min(y0, Math.min(y1, y2));
        int maxY = Math.max(y0, Math.max(y1, y2));

        int dx01 = x1 - x0, dy01 = y1 - y0;
        int dx12 = x2 - x1, dy12 = y2 - y1;
        int dx20 = x0 - x2, dy20 = y0 - y2;

        float area2 = dx01 * dy20 - dy01 * dx20;

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                int dx0 = x - x0, dy0 = y - y0;
                int dx1 = x - x1, dy1 = y - y1;
                int dx2 = x - x2, dy2 = y - y2;

                float w0 = (dx1 * dy12 - dy1 * dx12) / area2;
                float w1 = (dx2 * dy20 - dy2 * dx20) / area2;
                float w2 = (dx0 * dy01 - dy0 * dx01) / area2;

                if (w0 >= 0 && w1 >= 0 && w2 >= 0) {
                    setPixel(x, y, color);
                }
            }
        }
    }

    // === Texto ===
    public void printChar(int x, int y, char c, int color) {
        int idx = c - 0x20;
        if (idx < 0 || idx >= BitmapFont.font8x8_basic.length) {
            return;
        }

        byte[] bits = BitmapFont.font8x8_basic[idx];
        for (int row = 0; row < 8; row++) {
            byte line = bits[row];
            for (int col = 0; col < 8; col++) {
                if ((line & (1 << col)) != 0) {
                    int px = x + col;
                    int py = (height - 1) - (y + row);
                    setPixel(px, py, color);
                }
            }
        }
    }

    public void printText(int x, int y, String text, int color) {
        int offsetX = 0;
        for (char c : text.toCharArray()) {
            printChar(x + offsetX, y, c, color);
            offsetX += 8;
        }
    }

    // === Framebuffer visual ===
    public void updateScreen() {
        glPanel.repaint(); // solo trigger, GLEventListener.display() se encarga
    }

    private int[] convertARGBtoRGBA(int[] input) {
        int[] output = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            int argb = input[i];
            int a = (argb >> 24) & 0xFF;
            int r = (argb >> 16) & 0xFF;
            int g = (argb >> 8) & 0xFF;
            int b = argb & 0xFF;
            output[i] = (r << 24) | (g << 16) | (b << 8) | a;
        }
        return output;
    }
}
