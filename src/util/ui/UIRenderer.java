package util.ui;

import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class UIRenderer {
    private UIShader shader;
    private int vao, vbo;
    private Matrix4f projection;

    public UIRenderer(int windowWidth, int windowHeight) {
        shader = UIShader.getInstance();
        projection = new Matrix4f().ortho(0, windowWidth, windowHeight, 0, -1, 1);
        initGeometry();
    }

    private void initGeometry() {
        // Create VAO
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        // Create VBO with unit quad
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        // Unit quad with texture coordinates
        float[] vertices = {
                // Positions  // Texture Coords
                0.0f, 0.0f,   0.0f, 0.0f,
                1.0f, 0.0f,   1.0f, 0.0f,
                1.0f, 1.0f,   1.0f, 1.0f,
                1.0f, 1.0f,   1.0f, 1.0f,
                0.0f, 1.0f,   0.0f, 1.0f,
                0.0f, 0.0f,   0.0f, 0.0f
        };

        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Position attribute
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        // Texture coord attribute
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindVertexArray(0);
    }

    public void begin() {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        shader.use();
        shader.setProjection(projection);
        glBindVertexArray(vao);
    }

    public void end() {
        glBindVertexArray(0);
        shader.unuse();
        glDisable(GL_BLEND);
    }

    public void drawRect(float x, float y, float width, float height,
                         float r, float g, float b, float a) {
        shader.setPosition(x, y);
        shader.setSize(width, height);
        shader.setColor(r, g, b, a);
        shader.setUseTexture(false);
        shader.setRadius(0);
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

    public void drawRoundedRect(float x, float y, float width, float height,
                                float radius, float r, float g, float b, float a) {
        shader.setPosition(x, y);
        shader.setSize(width, height);
        shader.setColor(r, g, b, a);
        shader.setUseTexture(false);
        shader.setRadius(radius);
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

    public void drawTexturedRect(float x, float y, float width, float height,
                                 int textureId, float r, float g, float b, float a) {
        glBindTexture(GL_TEXTURE_2D, textureId);
        shader.setPosition(x, y);
        shader.setSize(width, height);
        shader.setColor(r, g, b, a);
        shader.setUseTexture(true);
        shader.setRadius(0);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void updateProjection(int windowWidth, int windowHeight) {
        projection = new Matrix4f().ortho(0, windowWidth, windowHeight, 0, -1, 1);
    }

    public void cleanup() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        shader.cleanup();
    }
}