package input;

import elements.ElementType;

import java.util.HashMap;
import java.util.Map;
import static org.lwjgl.glfw.GLFW.*;


public class InputElement {

    Map<Integer, ElementType> elementMap;
    private static final InputElement inputElement;

    static {
        inputElement = new InputElement();
    }

    public ElementType getElementFromKey(int key) {
        switch (key) {
            case GLFW_KEY_1:
                return ElementType.STONE;
            case GLFW_KEY_2:
                return ElementType.SAND;
//            case GLFW_KEY_3: return ElementType.DIRT;
//            case GLFW_KEY_4: return ElementType.WATER;
//            case GLFW_KEY_5: return ElementType.OIL;
//            case GLFW_KEY_6: return ElementType.ACID;
//            case GLFW_KEY_7: return ElementType.WOOD;
//            case GLFW_KEY_8: return ElementType.TITANIUM;
            case GLFW_KEY_9:
                return ElementType.EMPTYCELL;
//            case GLFW_KEY_E: return ElementType.EMBER;
//            case GLFW_KEY_O: return ElementType.COAL;
//            case GLFW_KEY_L: return ElementType.LAVA;
//            case GLFW_KEY_B: return ElementType.BLOOD;
//            case GLFW_KEY_G: return ElementType.FLAMMABLEGAS;
//            case GLFW_KEY_F: return ElementType.SPARK;
//            case GLFW_KEY_N: return ElementType.SNOW;
//            case GLFW_KEY_COMMA: return ElementType.SLIMEMOLD;
            default:
                return null; // or current element type
        }
    }

    // Usage in update loop
    public static ElementType checkElementSelection(long window) {
        for (int key = GLFW_KEY_1; key <= GLFW_KEY_9; key++) {
            if (glfwGetKey(window, key) == GLFW_PRESS) {
                return inputElement.getElementFromKey(key);
            }
        }

        // Check letter keys
        int[] letterKeys = {GLFW_KEY_E, GLFW_KEY_O, GLFW_KEY_L, GLFW_KEY_B,
                GLFW_KEY_G, GLFW_KEY_F, GLFW_KEY_N, GLFW_KEY_COMMA};
        for (int key : letterKeys) {
            if (glfwGetKey(window, key) == GLFW_PRESS) {
                return inputElement.getElementFromKey(key);
            }
        }
        return null;
    }

}
