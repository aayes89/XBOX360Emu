package xbox360emu.GPU;

import java.nio.IntBuffer;
import java.util.Arrays;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

/*
* @author Slam
 */
public class Framebuffer implements GLEventListener {

    private final int width;
    private final int height;
    private final int[] framebuffer;
    private int textureId;
    private boolean needsUpdate = true;

    public Framebuffer(int width, int height, int[] framebuffer) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Width and height must be positive");
        }
        if (framebuffer == null || framebuffer.length != width * height) {
            throw new IllegalArgumentException("Framebuffer size must be width * height");
        }
        this.width = width;
        this.height = height;
        this.framebuffer = framebuffer;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        // Configurar estados de OpenGL
        gl.glPixelStorei(GL2.GL_UNPACK_ALIGNMENT, 4); // Alineación de 4 bytes (int)
        gl.glEnable(GL2.GL_TEXTURE_2D);

        // Crear textura
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        textureId = textures[0];
        gl.glBindTexture(GL2.GL_TEXTURE_2D, textureId);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, width, height, 0,
                GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, null);

        // Configurar matriz de proyección
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, width, height, 0, -1, 1); // Coordenadas: (0,0) en esquina superior izquierda
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        System.out.println("OpenGL inicializado");
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        clearScreen(drawable);
        if (textureId != 0) {
            gl.glDeleteTextures(1, new int[]{textureId}, 0);
            textureId = 0;
        }
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        clearScreen(drawable);

        IntBuffer buff = IntBuffer.wrap(framebuffer);

        gl.glBindTexture(GL2.GL_TEXTURE_2D, textureId);
        //gl.glTexSubImage2D(GL2.GL_TEXTURE_2D, 0, 0, 0, width, height, GL2.GL_RGBA, GL2.GL_UNSIGNED_INT_8_8_8_8_REV, buff);
        gl.glTexSubImage2D(GL2.GL_TEXTURE_2D, 0, 0, 0, width, height, GL2.GL_BGRA, GL2.GL_UNSIGNED_BYTE, buff);

        // Obtener tamaño real del viewport
        int[] viewport = new int[4];
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        int screenWidth = viewport[2];
        int screenHeight = viewport[3];

        // Calcular escalado y centrado
        float scaleX = (float) screenWidth / width;
        float scaleY = (float) screenHeight / height;
        float scale = Math.min(scaleX, scaleY);

        float displayWidth = width * scale;
        float displayHeight = height * scale;

        float offsetX = (screenWidth - displayWidth) / 2f;
        float offsetY = (screenHeight - displayHeight) / 2f;

        // Configurar ortho en tamaño real de pantalla
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, screenWidth, screenHeight, 0, -1, 1);
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        // Render quad con escalado y centrado        
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0, 1);
        gl.glVertex2f(offsetX, offsetY);
        gl.glTexCoord2f(1, 1);
        gl.glVertex2f(offsetX + displayWidth, offsetY);
        gl.glTexCoord2f(1, 0);
        gl.glVertex2f(offsetX + displayWidth, offsetY + displayHeight);
        gl.glTexCoord2f(0, 0);
        gl.glVertex2f(offsetX, offsetY + displayHeight);
        gl.glEnd();
        /*gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0, 0); // Antes era (0, 1)
        gl.glVertex2f(offsetX, offsetY);

        gl.glTexCoord2f(1, 0); // Antes era (1, 1)
        gl.glVertex2f(offsetX + displayWidth, offsetY);

        gl.glTexCoord2f(1, 1); // Antes era (1, 0)
        gl.glVertex2f(offsetX + displayWidth, offsetY + displayHeight);

        gl.glTexCoord2f(0, 1); // Antes era (0, 0)
        gl.glVertex2f(offsetX, offsetY + displayHeight);
        gl.glEnd();*/
        
        // En tu método display()
        gl.glColor3f(0.2f, 0.2f, 0.2f);
        gl.glBegin(GL2.GL_LINES);
        for (int x = 0; x <= width; x += 8) {
            gl.glVertex2f(x, 0);
            gl.glVertex2f(x, height);
        }
        for (int y = 0; y <= height; y += 8) {
            gl.glVertex2f(0, y);
            gl.glVertex2f(width, y);
        }
        gl.glEnd();
        gl.glColor3f(1f, 1f, 1f); // Restaurar color

    }

    public void display0(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        clearScreen(drawable);

        // Cargar framebuffer en la textura
        IntBuffer buff = IntBuffer.wrap(framebuffer);
        if (needsUpdate) {
            gl.glBindTexture(GL2.GL_TEXTURE_2D, textureId);
            gl.glTexSubImage2D(GL2.GL_TEXTURE_2D, 0, 0, 0, width, height,
                    GL2.GL_RGBA, GL2.GL_UNSIGNED_INT_8_8_8_8_REV, buff);
            //gl.glTexSubImage2D(GL2.GL_TEXTURE_2D, 0, 0, 0, width, height, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, buff);
            needsUpdate = false;
        }

        // Renderizar quad con la textura, invirtiendo coordenadas de textura verticalmente
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2f(0, 1);
        gl.glVertex2f(0, 0);         // Superior izquierda
        gl.glTexCoord2f(1, 1);
        gl.glVertex2f(width, 0);     // Superior derecha
        gl.glTexCoord2f(1, 0);
        gl.glVertex2f(width, height); // Inferior derecha
        gl.glTexCoord2f(0, 0);
        gl.glVertex2f(0, height);     // Inferior izquierda
        gl.glEnd();
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0, width, height, 0, -1, 1); // Mantener proporciones del framebuffer
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public int[] getFramebuffer() {
        return framebuffer;
    }

    public void fillFramebuffer(int[] fbData) {
        if (fbData == null || fbData.length != width * height) {
            throw new IllegalArgumentException("fbData size must be width * height");
        }        
        System.arraycopy(fbData, 0, framebuffer, 0, fbData.length);
        needsUpdate = true;
    }

    private void clearScreen(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0, 0, 0, 1); // Negro opaco
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
    }
}
