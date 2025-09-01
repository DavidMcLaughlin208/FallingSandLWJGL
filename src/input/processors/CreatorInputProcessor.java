package input.processors;

//import com.badlogic.gdx.Input;
//import com.badlogic.gdx.InputProcessor;
//import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.math.Vector3;
//import com.gdx.cellular.CellularMatrix;
import elements.ElementType;
//import com.gdx.cellular.input.InputElement;
//import com.gdx.cellular.input.InputManager;
//import com.gdx.cellular.input.InputProcessors;


import input.InputElement;
import input.InputManager;
import input.InputProcessors;
import matrix.CellularMatrix;
import static org.lwjgl.glfw.GLFW.*;

public class CreatorInputProcessor implements InputProcessor {

    private final InputManager inputManager;
    private final CellularMatrix matrix;
    private final InputProcessors parent;
    private final long window;

    public CreatorInputProcessor(InputProcessors inputProcessors, InputManager inputManager, CellularMatrix matrix, long window) {
        this.parent = inputProcessors;
        this.inputManager = inputManager;
        this.matrix = matrix;
        this.window = window;
    }

    @Override
    public void process() {
        updateInputElement();
        updateBrushSize();
        checkClear();
        updatePause();
        checkCycleMouseMode();
        touchDown();
        touchDragged();
    }

    private void updateInputElement() {
        ElementType elementType = InputElement.checkElementSelection(window);
        if (elementType != null) {
            inputManager.setCurrentlySelectedElement(elementType);
        }
    }

    private void updateBrushSize() {
        if (glfwGetKey(window, GLFW_KEY_EQUAL) == GLFW_PRESS) {
            inputManager.calculateNewBrushSize(2);
        }
        if (glfwGetKey(window, GLFW_KEY_MINUS) == GLFW_PRESS) {
            inputManager.calculateNewBrushSize(-2);
        }
    }

    private void updatePause() {
        if (glfwGetKey(window, GLFW_KEY_P) == GLFW_PRESS) {
            inputManager.togglePause();
        }
    }

    private void checkClear() {
        if (glfwGetKey(window, GLFW_KEY_C) == GLFW_PRESS) {
            inputManager.clearMatrix(matrix);
//            inputManager.clearBox2dActors();
        }
    }

    private void checkCycleMouseMode() {
        if (glfwGetKey(window, GLFW_KEY_M) == GLFW_PRESS) {
            inputManager.cycleMouseModes();
//            inputManager.clearBox2dActors();
        }
    }

//    @Override
//    public boolean scrolled(int amount) {
//        inputManager.calculateNewBrushSize(amount * -2);
//        return true;
//    }

    public boolean touchDown() {
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            inputManager.spawnElementByInput(matrix);
        } else if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS) {
            inputManager.setTouchedLastFrame(false);
//            Vector3 pos = camera.unproject(new Vector3(screenX, screenY, 0));
//            inputManager.setDrawMenuAndLocation(pos.x, pos.y);
        }
        return false;
    }

    public boolean touchUp() {
        if (glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_RELEASE) {
            inputManager.setTouchedLastFrame(false);
        }
        return false;
    }

    public boolean touchDragged() {
        if (!inputManager.drawMenu) {
            inputManager.spawnElementByInput(matrix);
        }
        return false;
    }

}
