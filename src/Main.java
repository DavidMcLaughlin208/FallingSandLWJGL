import matrix.CellularAutomaton;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.Random;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

    // The window handle
    private long window;

    // Texture and rendering resources
    private int textureId;
    private int vao, vbo, ebo;
    private int shaderProgram;

    // Pixel Buffer Objects for double buffering
    private int[] pbos = new int[2];
    private int pboIndex = 0;
    private ByteBuffer mappedBuffer;

    // Random number generator
    private Random random = new Random();

    // Texture dimensions
    private static final int TEXTURE_WIDTH = 512;
    private static final int TEXTURE_HEIGHT = 512;
    private static final int PIXEL_SIZE = 4; // RGBA
    private static final int BUFFER_SIZE = TEXTURE_WIDTH * TEXTURE_HEIGHT * PIXEL_SIZE;

    // FPS tracking
    private long lastTime;
    private int frameCount;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        System.out.println("Using PBO for efficient texture streaming");


        new CellularAutomaton().create().loop();

//        init();
//        loop();
//
//        // Cleanup
//        glDeleteTextures(textureId);
//        glDeleteVertexArrays(vao);
//        glDeleteBuffers(vbo);
//        glDeleteBuffers(ebo);
//        glDeleteBuffers(pbos[0]);
//        glDeleteBuffers(pbos[1]);
//        glDeleteProgram(shaderProgram);
//
//        // Free the window callbacks and destroy the window
//        glfwFreeCallbacks(window);
//        glfwDestroyWindow(window);
//
//        // Terminate GLFW and free the error callback
//        glfwTerminate();
//        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        // Create the window
        window = glfwCreateWindow(800, 600, "PBO Texture Streaming Demo", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup a key callback
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(window, true);
            }
        });

        // Center the window
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);
            glfwGetWindowSize(window, pWidth, pHeight);
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        }

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Disable v-sync for max performance testing
        glfwSwapInterval(0);
        // Make the window visible
        glfwShowWindow(window);

        lastTime = System.nanoTime();
    }

    private void setupShaders() {
        // Vertex shader
        String vertexShaderSource = "#version 330 core\n" +
                "layout (location = 0) in vec3 aPos;\n" +
                "layout (location = 1) in vec2 aTexCoord;\n" +
                "out vec2 TexCoord;\n" +
                "void main() {\n" +
                "    gl_Position = vec4(aPos, 1.0);\n" +
                "    TexCoord = aTexCoord;\n" +
                "}";

        // Fragment shader with some color processing
        String fragmentShaderSource = "#version 330 core\n" +
                "out vec4 FragColor;\n" +
                "in vec2 TexCoord;\n" +
                "uniform sampler2D texture1;\n" +
                "uniform float time;\n" +
                "void main() {\n" +
                "    vec4 texColor = texture(texture1, TexCoord);\n" +
                "    // Add slight color shift based on time for visual interest\n" +
                "    float shift = sin(time) * 0.1 + 0.9;\n" +
                "    FragColor = vec4(texColor.rgb * shift, texColor.a);\n" +
                "}";

        // Compile vertex shader
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);

        // Check for vertex shader compile errors
        int success = glGetShaderi(vertexShader, GL_COMPILE_STATUS);
        if (success == 0) {
            String log = glGetShaderInfoLog(vertexShader);
            System.err.println("Vertex shader compilation failed: " + log);
        }

        // Compile fragment shader
        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);

        // Check for fragment shader compile errors
        success = glGetShaderi(fragmentShader, GL_COMPILE_STATUS);
        if (success == 0) {
            String log = glGetShaderInfoLog(fragmentShader);
            System.err.println("Fragment shader compilation failed: " + log);
        }

        // Link shaders
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);
        glLinkProgram(shaderProgram);

        // Check for linking errors
        success = glGetProgrami(shaderProgram, GL_LINK_STATUS);
        if (success == 0) {
            String log = glGetProgramInfoLog(shaderProgram);
            System.err.println("Shader program linking failed: " + log);
        }

        // Delete the shaders as they're linked into our program now
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    private void setupGeometry() {
        // Vertices for a fullscreen quad with texture coordinates
        float[] vertices = {
                // positions          // texture coords
                1.0f,  1.0f, 0.0f,   1.0f, 1.0f,   // top right
                1.0f, -1.0f, 0.0f,   1.0f, 0.0f,   // bottom right
                -1.0f, -1.0f, 0.0f,   0.0f, 0.0f,   // bottom left
                -1.0f,  1.0f, 0.0f,   0.0f, 1.0f    // top left
        };

        int[] indices = {
                0, 1, 3,   // first triangle
                1, 2, 3    // second triangle
        };

        // Create and bind VAO
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        // Create and bind VBO
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        // Create and bind EBO
        ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Position attribute
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0L);
        glEnableVertexAttribArray(0);

        // Texture coordinate attribute
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
        glEnableVertexAttribArray(1);

        // Unbind
        glBindVertexArray(0);
    }

    private void setupTexture() {
        // Generate and bind texture
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Set texture parameters for pixelated look
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        // Allocate texture memory (no initial data)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, TEXTURE_WIDTH, TEXTURE_HEIGHT,
                0, GL_RGBA, GL_UNSIGNED_BYTE, (ByteBuffer)null);

        // Generate PBOs for double buffering
        pbos[0] = glGenBuffers();
        pbos[1] = glGenBuffers();

        // Initialize both PBOs
        for (int i = 0; i < 2; i++) {
            glBindBuffer(GL_PIXEL_UNPACK_BUFFER, pbos[i]);
            // Allocate buffer with STREAM_DRAW for frequent updates
            glBufferData(GL_PIXEL_UNPACK_BUFFER, BUFFER_SIZE, GL_STREAM_DRAW);
        }

        // Unbind PBO
        glBindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
    }

    private void updateTextureWithPBO() {
        // Use double buffering with PBOs
        // Index 0: copy pixels from PBO to texture
        // Index 1: update pixel data in PBO

        // Bind the PBO for reading (texture update)
        glBindBuffer(GL_PIXEL_UNPACK_BUFFER, pbos[pboIndex]);

        // Copy pixels from PBO to texture object
        // This returns immediately and lets GPU do the transfer asynchronously
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT,
                GL_RGBA, GL_UNSIGNED_BYTE, 0);

        // Bind the next PBO for writing
        pboIndex = (pboIndex + 1) % 2;
        glBindBuffer(GL_PIXEL_UNPACK_BUFFER, pbos[pboIndex]);

        // Map the buffer to CPU memory for writing
        mappedBuffer = glMapBuffer(GL_PIXEL_UNPACK_BUFFER, GL_WRITE_ONLY);

        if (mappedBuffer != null) {
            // Fill with random colored pixels
            updatePixelData(mappedBuffer);

            // Unmap the buffer (upload to GPU)
            glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER);
        }

        // Unbind PBO
        glBindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
    }

    private void updatePixelData(ByteBuffer buffer) {
        // Generate random pixel patterns
        buffer.clear();

        for (int y = 0; y < TEXTURE_HEIGHT; y++) {
            for (int x = 0; x < TEXTURE_WIDTH; x++) {
                // Create interesting patterns instead of pure noise
                float fx = (float)x / TEXTURE_WIDTH;
                float fy = (float)y / TEXTURE_HEIGHT;

                // Random base colors with gradients
                int r = (int)(random.nextFloat() * 128 + fx * 127);
                int g = (int)(random.nextFloat() * 128 + fy * 127);
                int b = (int)(random.nextFloat() * 200 + 55);

                // Add some structure - create random blocks
                if (random.nextFloat() > 0.98f) {
                    // Bright pixel clusters
                    r = 255;
                    g = random.nextInt(256);
                    b = random.nextInt(256);
                }

                buffer.put((byte)r);
                buffer.put((byte)g);
                buffer.put((byte)b);
                buffer.put((byte)255); // Alpha
            }
        }

        buffer.flip();
    }

    private void updateFPS() {
        frameCount++;
        long currentTime = System.nanoTime();
        long elapsed = currentTime - lastTime;

        if (elapsed >= 1_000_000_000L) { // 1 second
            double fps = frameCount * 1_000_000_000.0 / elapsed;
            glfwSetWindowTitle(window, String.format("PBO Texture Streaming - FPS: %.1f", fps));
            frameCount = 0;
            lastTime = currentTime;
        }
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        GL.createCapabilities();

        // Check PBO support
        if (!GL.getCapabilities().GL_ARB_pixel_buffer_object) {
            System.err.println("Warning: PBO not supported, falling back to regular texture updates");
        }

        // Setup rendering resources
        setupShaders();
        setupGeometry();
        setupTexture();

        // Set clear color
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Use shader program
        glUseProgram(shaderProgram);

        // Get uniform locations
        int timeLocation = glGetUniformLocation(shaderProgram, "time");

        // Bind texture to texture unit 0
        glActiveTexture(GL_TEXTURE0);

        float time = 0.0f;

        // Run the rendering loop
        while (!glfwWindowShouldClose(window)) {
            // Update FPS counter
            updateFPS();

            // Clear the framebuffer
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            // Update texture using PBO
            updateTextureWithPBO();

            // Update time uniform
            time += 0.016f; // ~60fps timing
            glUniform1f(timeLocation, time);

            // Draw the quad
            glBindVertexArray(vao);
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
            glBindVertexArray(0);

            // Swap the color buffers
            glfwSwapBuffers(window);

            // Poll for window events
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
}