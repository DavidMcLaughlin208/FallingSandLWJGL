package util.ui;

import matrix.CellularAutomaton;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL15;
import org.lwjgl.system.MemoryUtil;
import util.Color;

import java.nio.FloatBuffer;

import static matrix.CellularAutomaton.shaderProgram;
import static matrix.CellularAutomaton.vao;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class RectRenderer {
    public RectRenderer() {
    }

    public static void drawRect(float x, float y, float width, float height,
                                Color color) {

        System.out.println("Should be drawing rect");
        glUseProgram(shaderProgram);
        glBindVertexArray(vao);

        // Set uniforms
        int projLoc = glGetUniformLocation(shaderProgram, "projection");
        float[] projMatrix = new float[16];
        CellularAutomaton.projection.get(projMatrix);
        glUniformMatrix4fv(projLoc, false, projMatrix);

        glUniform2f(glGetUniformLocation(shaderProgram, "position"), x, y);
        glUniform2f(glGetUniformLocation(shaderProgram, "size"), width, height);
        glUniform4f(glGetUniformLocation(shaderProgram, "color"), color.r, color.g, color.b, color.a);

        // Draw
        glDrawArrays(GL_TRIANGLES, 0, 6);

        glBindVertexArray(0);
        glUseProgram(0);
    }

}