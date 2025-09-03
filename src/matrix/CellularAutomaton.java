package matrix;

//import com.badlogic.gdx.physics.box2d.*;
//import com.gdx.cellular.box2d.ShapeFactory;
import input.InputManager;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import util.ElementColumnStepper;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.GL_PIXEL_UNPACK_BUFFER;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class CellularAutomaton {
    public static int screenWidth = 1280; // 480;
    public static int screenHeight = 800; //800;
    public static int pixelSizeModifier = 4;
    public static int box2dSizeModifier = 10;
    public static Vector3f gravity = new Vector3f(0f, -5f, 0f);
    public static BitSet stepped = new BitSet(1);
    public static int MATRIX_WIDTH = screenWidth / pixelSizeModifier;
    public static int MATRIX_HEIGHT = screenHeight / pixelSizeModifier;
    private static final int BUFFER_SIZE = MATRIX_WIDTH * MATRIX_HEIGHT * pixelSizeModifier;
    public static int vao, vbo, ebo;
    public static int shaderProgram;
    public static Matrix4f projection = new Matrix4f().ortho(0, screenWidth, screenHeight, 0, -1, 1);

    private long window;
    private long lastTime;
    private int[] pbos = new int[2];
    private int pboIndex = 0;
    private ByteBuffer mappedBuffer;

    private int textureId;
    private int numThreads = 12;
    private boolean useMultiThreading = true;
    public CellularMatrix matrix;

    private InputManager inputManager;

    public static int frameCount = 0;
//    public World b2dWorld;
//    public Box2DDebugRenderer debugRenderer;
//    public InputProcessors inputProcessors;
//    public GameManager gameManager;

    public CellularAutomaton create () {
        stepped.set(0, true);

        initWindow();

//        b2dWorld = new World(new Vector2(0, -100), true);

        matrix = new CellularMatrix(MATRIX_WIDTH, MATRIX_HEIGHT, pixelSizeModifier); //, b2dWorld);
        matrix.generateShuffledIndexesForThreads(numThreads);
        inputManager = new InputManager(matrix, window);

//        ShapeFactory.initialize(b2dWorld);
//        debugRenderer = new Box2DDebugRenderer();

//        setUpBasicBodies();

//        this.gameManager = new GameManager(this);
//        gameManager.createPlayer(matrix.innerArraySize/2, matrix.outerArraySize/2);
//        inputProcessors = new InputProcessors(inputManager, matrix, camera, gameManager);
        return this;
    }

    private void initWindow() {
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
        window = glfwCreateWindow(screenWidth, screenHeight, "Falling Sand", NULL, NULL);
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
        glfwSwapInterval(1);
        // Make the window visible
        glfwShowWindow(window);

        lastTime = System.nanoTime();

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

        // Bind texture to texture unit 0
        glActiveTexture(GL_TEXTURE0);
    }

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        System.out.println("Using PBO for efficient texture streaming");

        loop();

        inputManager.uiRenderer.cleanup();

        // Cleanup
        glDeleteTextures(textureId);
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteBuffers(pbos[0]);
        glDeleteBuffers(pbos[1]);
        glDeleteProgram(shaderProgram);

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
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
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, MATRIX_WIDTH, MATRIX_HEIGHT,
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

    public void loop () {
        float time = 0.0f;
        int timeLocation = glGetUniformLocation(shaderProgram, "time");
        while (!glfwWindowShouldClose(window)) {
            // Update FPS counter
            updateFPS();

            stepped.flip(0);
            incrementFrameCount();

            matrix.resetChunks();

            // Detect and act on input
//        numThreads = inputManager.adjustThreadCount(numThreads);
//        inputManager.save(matrix);
//        inputManager.load(matrix);

            matrix.reshuffleXIndexes();
            matrix.reshuffleThreadXIndexes(numThreads);
            matrix.calculateAndSetThreadedXIndexOffset();

//        boolean isPaused = inputManager.getIsPaused();
//        if (isPaused) {
//            matrix.useChunks = false;
//            useChunks = false;
//            matrixStage.draw();
//            matrix.drawPhysicsElementActors(shapeRenderer);
//            Array<Body> bodies = new Array<>();
//            b2dWorld.getBodies(bodies);
//            matrix.drawBox2d(shapeRenderer, bodies);
//            debugRenderer.render(b2dWorld, camera.combined);
//            return;
//        }

            matrix.spawnFromSpouts();
            inputManager.weatherSystem.enact(this.matrix);
            matrix.reshuffleThreadXIndexes(numThreads);
            List<Thread> threads = new ArrayList<>(numThreads);

            for (int t = 0; t < numThreads; t++) {
                Thread newThread = new Thread(new ElementColumnStepper(matrix, t));
                threads.add(newThread);
            }
            if (stepped.get(0)) {
                startAndWaitOnOddThreads(threads);
                startAndWaitOnEvenThreads(threads);
            } else {
                startAndWaitOnEvenThreads(threads);
                startAndWaitOnOddThreads(threads);
            }

            inputManager.process();
            matrix.executeExplosions();
//			matrix.drawAll(shapeRenderer);


//        matrix.executeExplosions();

//        b2dWorld.step(1/120f, 10, 6);
//        b2dWorld.step(1/120f, 10, 6);
//        matrix.stepPhysicsElementActors();

//        matrix.drawPhysicsElementActors(shapeRenderer);

//        Array<Body> bodies = new Array<>();
//        b2dWorld.getBodies(bodies);
//        matrix.drawBox2d(shapeRenderer, bodies);
//        debugRenderer.render(b2dWorld, camera.combined);

//        inputManager.drawMenu();
//        inputManager.drawCursor();

//        gameManager.stepPlayers(this.matrix);

            glUseProgram(shaderProgram);

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

            inputManager.renderUi();

            // Swap the color buffers
            glfwSwapBuffers(window);

            // Poll for window events
            glfwPollEvents();
        }
    }

//    @Override
//    public void resize (int width, int height) {
//        matrixStage.getViewport().update(width, height, true);
//        inputManager.cursorStage.getViewport().update(width, height, true);
//        inputManager.modeStage.getViewport().update(width, height, true);
//    }

    private void incrementFrameCount() {
        frameCount = frameCount == 3 ? 0 : frameCount + 1;
    }

//    private void setUpBasicBodies() {
//        BodyDef groundBodyDef = new BodyDef();
//
//        inputManager.spawnPhysicsRect(matrix, new Vector3((camera.viewportWidth/2/box2dSizeModifier/8) * 10, 150, 0),
//                new Vector3((camera.viewportWidth/2/box2dSizeModifier - camera.viewportWidth/2/box2dSizeModifier/8) * 20, 50, 0),
//                ElementType.STONE,
//                BodyDef.BodyType.StaticBody);
//    }

    private void startAndWaitOnEvenThreads(List<Thread> threads) {
        try {
            for (int t = 0; t < threads.size(); t++) {
                if (t % 2 == 0) {
                    threads.get(t).start();
                }
            }
            for (int t = 0; t < threads.size(); t++) {
                if (t % 2 == 0) {
                    threads.get(t).join();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void startAndWaitOnOddThreads(List<Thread> threads) {
        try {
            for (int t = 0; t < threads.size(); t++) {
                if (t % 2 != 0) {
                    threads.get(t).start();
                }
            }
            for (int t = 0; t < threads.size(); t++) {
                if (t % 2 == 0) {
                    threads.get(t).join();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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

    private void updateTextureWithPBO() {
        // Use double buffering with PBOs
        // Index 0: copy pixels from PBO to texture
        // Index 1: update pixel data in PBO

        // Bind the PBO for reading (texture update)
        glBindBuffer(GL_PIXEL_UNPACK_BUFFER, pbos[pboIndex]);

        // Copy pixels from PBO to texture object
        // This returns immediately and lets GPU do the transfer asynchronously
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, MATRIX_WIDTH, MATRIX_HEIGHT,
                GL_RGBA, GL_UNSIGNED_BYTE, 0);

        // Bind the next PBO for writing
        pboIndex = (pboIndex + 1) % 2;
        glBindBuffer(GL_PIXEL_UNPACK_BUFFER, pbos[pboIndex]);

        // Map the buffer to CPU memory for writing
        mappedBuffer = glMapBuffer(GL_PIXEL_UNPACK_BUFFER, GL_WRITE_ONLY);

        if (mappedBuffer != null) {
            // Fill with random colored pixels
            matrix.updatePixelData(mappedBuffer);

            // Unmap the buffer (upload to GPU)
            glUnmapBuffer(GL_PIXEL_UNPACK_BUFFER);
        }

        // Unbind PBO
        glBindBuffer(GL_PIXEL_UNPACK_BUFFER, 0);
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

}
