package util.ui;

import org.joml.Matrix4f;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.*;

public class UIShader {
    private static UIShader instance;
    private int shaderProgram;
    private int projectionLoc, positionLoc, sizeLoc, colorLoc, useTextureLoc, radiusLoc;

    // Private constructor for singleton
    private UIShader() {
        init();
    }

    public static UIShader getInstance() {
        if (instance == null) {
            instance = new UIShader();
        }
        return instance;
    }

    private void init() {
        String vertexShader =
                "#version 330 core\n" +
                        "layout (location = 0) in vec2 aPos;\n" +
                        "layout (location = 1) in vec2 aTexCoord;\n" +
                        "\n" +
                        "out vec2 TexCoord;\n" +
                        "out vec2 FragPos;\n" +
                        "\n" +
                        "uniform mat4 projection;\n" +
                        "uniform vec2 position;\n" +
                        "uniform vec2 size;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    FragPos = aPos;\n" +
                        "    TexCoord = aTexCoord;\n" +
                        "    vec2 vertexPosition = aPos * size + position;\n" +
                        "    gl_Position = projection * vec4(vertexPosition, 0.0, 1.0);\n" +
                        "}";

        String fragmentShader =
                "#version 330 core\n" +
                        "in vec2 TexCoord;\n" +
                        "in vec2 FragPos;\n" +
                        "out vec4 FragColor;\n" +
                        "\n" +
                        "uniform vec4 color;\n" +
                        "uniform bool useTexture;\n" +
                        "uniform sampler2D texture0;\n" +
                        "uniform float radius;\n" +  // For rounded rectangles
                        "uniform vec2 size;\n" +
                        "\n" +
                        "void main() {\n" +
                        "    if (useTexture) {\n" +
                        "        FragColor = texture(texture0, TexCoord) * color;\n" +
                        "    } else {\n" +
                        "        // Optional: rounded corners\n" +
                        "        if (radius > 0.0) {\n" +
                        "            vec2 halfSize = size * 0.5;\n" +
                        "            vec2 centerPos = FragPos * size - halfSize;\n" +
                        "            \n" +
                        "            // Check if we're in a corner region\n" +
                        "            vec2 cornerDist = abs(centerPos) - (halfSize - radius);\n" +
                        "            if (cornerDist.x > 0.0 && cornerDist.y > 0.0) {\n" +
                        "                float dist = length(cornerDist);\n" +
                        "                if (dist > radius) {\n" +
                        "                    discard;\n" +
                        "                }\n" +
                        "                // Anti-aliasing for smooth edges\n" +
                        "                float alpha = 1.0 - smoothstep(radius - 1.0, radius, dist);\n" +
                        "                FragColor = vec4(color.rgb, color.a * alpha);\n" +
                        "            } else {\n" +
                        "                FragColor = color;\n" +
                        "            }\n" +
                        "        } else {\n" +
                        "            FragColor = color;\n" +
                        "        }\n" +
                        "    }\n" +
                        "}";

        // Compile vertex shader
        int vertShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertShader, vertexShader);
        glCompileShader(vertShader);

        // Check vertex shader compilation
        if (glGetShaderi(vertShader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Vertex shader compilation failed:");
            System.err.println(glGetShaderInfoLog(vertShader));
        }

        // Compile fragment shader
        int fragShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragShader, fragmentShader);
        glCompileShader(fragShader);

        // Check fragment shader compilation
        if (glGetShaderi(fragShader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Fragment shader compilation failed:");
            System.err.println(glGetShaderInfoLog(fragShader));
        }

        // Link shaders
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertShader);
        glAttachShader(shaderProgram, fragShader);
        glLinkProgram(shaderProgram);

        // Check linking
        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Shader linking failed:");
            System.err.println(glGetProgramInfoLog(shaderProgram));
        }

        // Clean up
        glDeleteShader(vertShader);
        glDeleteShader(fragShader);

        // Get uniform locations
        projectionLoc = glGetUniformLocation(shaderProgram, "projection");
        positionLoc = glGetUniformLocation(shaderProgram, "position");
        sizeLoc = glGetUniformLocation(shaderProgram, "size");
        colorLoc = glGetUniformLocation(shaderProgram, "color");
        useTextureLoc = glGetUniformLocation(shaderProgram, "useTexture");
        radiusLoc = glGetUniformLocation(shaderProgram, "radius");
    }

    public void use() {
        glUseProgram(shaderProgram);
    }

    public void unuse() {
        glUseProgram(0);
    }

    public void setProjection(Matrix4f projection) {
        float[] matrix = new float[16];
        projection.get(matrix);
        glUniformMatrix4fv(projectionLoc, false, matrix);
    }

    public void setPosition(float x, float y) {
        glUniform2f(positionLoc, x, y);
    }

    public void setSize(float width, float height) {
        glUniform2f(sizeLoc, width, height);
    }

    public void setColor(float r, float g, float b, float a) {
        glUniform4f(colorLoc, r, g, b, a);
    }

    public void setUseTexture(boolean useTexture) {
        glUniform1i(useTextureLoc, useTexture ? 1 : 0);
    }

    public void setRadius(float radius) {
        glUniform1f(radiusLoc, radius);
    }

    public int getProgram() {
        return shaderProgram;
    }

    public void cleanup() {
        glDeleteProgram(shaderProgram);
    }
}