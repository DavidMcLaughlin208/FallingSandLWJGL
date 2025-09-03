package input.menu;

import elements.ElementType;
import input.InputManager;
import input.MouseMode;
import org.joml.Vector2f;
import java.util.ArrayList;
import java.util.List;
import input.InputManager.BRUSHTYPE;
import org.joml.Vector3f;
import util.Color;
import util.ui.RectRenderer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_EQUAL;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

public class CreatorMenu {
    private boolean visible = false;
    private Vector2f position = new Vector2f();
    private MenuItem rootMenu;
    private MenuItem activeSubmenu = null;
    private float itemHeight = 25;
    private float itemWidth = 150;
    private float padding = 5;
    InputManager inputManager;

    // Menu structure
    class MenuItem {
        String label;
        List<MenuItem> children = new ArrayList<>();
        Runnable action;
        boolean expanded = false;
        Vector2f pos = new Vector2f();
        Vector2f size = new Vector2f();

        MenuItem(String label) {
            this.label = label;
        }

        MenuItem(String label, Runnable action) {
            this.label = label;
            this.action = action;
        }

        void addChild(MenuItem item) {
            children.add(item);
        }

        boolean isHovered(float mouseX, float mouseY) {
            return mouseX >= pos.x && mouseX <= pos.x + size.x &&
                    mouseY >= pos.y && mouseY <= pos.y + size.y;
        }
    }

    public CreatorMenu(InputManager inputManager) {
        this.inputManager = inputManager;
        buildMenu();
    }

    private void buildMenu() {
        rootMenu = new MenuItem("Root");

        // Element submenu
        MenuItem elementMenu = new MenuItem("Element >");
        elementMenu.addChild(new MenuItem("Stone", () -> setElement(ElementType.STONE)));
        elementMenu.addChild(new MenuItem("Sand", () -> setElement(ElementType.SAND)));
        elementMenu.addChild(new MenuItem("Water", () -> setElement(ElementType.WATER)));
        elementMenu.addChild(new MenuItem("Wood", () -> setElement(ElementType.WOOD)));
        elementMenu.addChild(new MenuItem("Lava", () -> setElement(ElementType.LAVA)));
        elementMenu.addChild(new MenuItem("Acid", () -> setElement(ElementType.ACID)));
        elementMenu.addChild(new MenuItem("Oil", () -> setElement(ElementType.OIL)));

        // Mouse Mode submenu
        MenuItem mouseModeMenu = new MenuItem("Mouse Mode >");
        mouseModeMenu.addChild(new MenuItem("Draw", () -> inputManager.setMouseMode(MouseMode.SPAWN)));
        mouseModeMenu.addChild(new MenuItem("Explosion", () -> inputManager.setMouseMode(MouseMode.EXPLOSION)));
        mouseModeMenu.addChild(new MenuItem("HEAT", () -> inputManager.setMouseMode(MouseMode.HEAT)));
        mouseModeMenu.addChild(new MenuItem("Particle", () -> inputManager.setMouseMode(MouseMode.PARTICLE)));
        mouseModeMenu.addChild(new MenuItem("Particalize", () -> inputManager.setMouseMode(MouseMode.PARTICALIZE)));
        mouseModeMenu.addChild(new MenuItem("PhysicsObj", () -> inputManager.setMouseMode(MouseMode.PHYSICSOBJ)));
        mouseModeMenu.addChild(new MenuItem("Rectangle", () -> inputManager.setMouseMode(MouseMode.RECTANGLE)));

        // Brush Type submenu
        MenuItem brushMenu = new MenuItem("Brush Type >");
        brushMenu.addChild(new MenuItem("Circle", () -> inputManager.setBrushType(BRUSHTYPE.CIRCLE)));
        brushMenu.addChild(new MenuItem("Square", () -> inputManager.setBrushType(BRUSHTYPE.SQUARE)));
        brushMenu.addChild(new MenuItem("Line", () -> inputManager.setBrushType(BRUSHTYPE.RECTANGLE)));

        // Add to root
        rootMenu.addChild(elementMenu);
        rootMenu.addChild(mouseModeMenu);
        rootMenu.addChild(brushMenu);
        rootMenu.addChild(new MenuItem("Clear All", () -> clearAll()));
    }

    public void show(float x, float y) {
        position.set(x, y);
        visible = true;
        activeSubmenu = null;

        // Calculate positions for menu items
        updateMenuPositions(rootMenu, x, y);
    }

    public void hide() {
        visible = false;
        activeSubmenu = null;
        // Collapse all submenus
        collapseAll(rootMenu);
    }

    private void collapseAll(MenuItem menu) {
        menu.expanded = false;
        for (MenuItem child : menu.children) {
            collapseAll(child);
        }
    }

    private void updateMenuPositions(MenuItem menu, float startX, float startY) {
        float y = startY;
        for (MenuItem item : menu.children) {
            item.pos.set(startX, y);
            item.size.set(itemWidth, itemHeight);
            y += itemHeight;
        }
    }

    public boolean handleMouseMove(Vector3f touchPos) {
        if (!visible) return false;

        int mouseX = (int) touchPos.x;
        int mouseY = (int) touchPos.y;

        // Check root menu items
        for (MenuItem item : rootMenu.children) {
            if (item.isHovered(mouseX, mouseY)) {
                // If has children, expand submenu
                if (!item.children.isEmpty() && !item.expanded) {
                    collapseAll(rootMenu);
                    item.expanded = true;
                    activeSubmenu = item;
                    // Position submenu to the right
                    updateMenuPositions(item, item.pos.x + itemWidth, item.pos.y);
                }
            }
        }

        // Check submenu items if active
        if (activeSubmenu != null && activeSubmenu.expanded) {
            boolean hoveringSubmenu = false;
            for (MenuItem subItem : activeSubmenu.children) {
                if (subItem.isHovered(mouseX, mouseY)) {
                    hoveringSubmenu = true;
                    break;
                }
            }

            // Check if still hovering parent or submenu
            if (!activeSubmenu.isHovered(mouseX, mouseY) && !hoveringSubmenu) {
                // Check if hovering another main menu item
                boolean hoveringOther = false;
                for (MenuItem item : rootMenu.children) {
                    if (item != activeSubmenu && item.isHovered(mouseX, mouseY)) {
                        hoveringOther = true;
                        break;
                    }
                }

                if (hoveringOther) {
                    activeSubmenu.expanded = false;
                    activeSubmenu = null;
                }
            }
        }

        return true;
    }

    public boolean handleMouseClick(Vector3f touchPos, long window) {
        if (!visible) return false;

        int mouseX = (int) touchPos.x();
        int mouseY = (int) touchPos.y();

        // Right click outside menu closes it
        System.err.println("About to check right click");
        if (glfwGetKey(window, GLFW_MOUSE_BUTTON_RIGHT) == GLFW_PRESS) {
            System.err.println("Right clicked");
            if (visible) {
                hide();
                return true;
            } else {
                show(mouseX, mouseY);
                return true;
            }
        }

        // Left click
        if (glfwGetKey(window, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
            // Check root menu items
            for (MenuItem item : rootMenu.children) {
                if (item.isHovered(mouseX, mouseY)) {
                    if (item.action != null) {
                        item.action.run();
                        hide();
                        return true;
                    }
                }
            }

            // Check submenu items
            if (activeSubmenu != null && activeSubmenu.expanded) {
                for (MenuItem subItem : activeSubmenu.children) {
                    if (subItem.isHovered(mouseX, mouseY)) {
                        if (subItem.action != null) {
                            subItem.action.run();
                            hide();
                            return true;
                        }
                    }
                }
            }

            // Click outside closes menu
            hide();
            return true;
        }

        return false;
    }

    public void render(Vector3f touchPos) {
//        if (!visible) return;

        int mouseX = (int) touchPos.x;
        int mouseY = (int) touchPos.y;

        inputManager.uiRenderer.begin();

        // Draw main menu background
        float menuHeight = rootMenu.children.size() * itemHeight;
        inputManager.uiRenderer.drawRect(position.x, position.y, itemWidth, menuHeight, 0.2f, 0.2f, 0.2f, 0.9f);

        // Draw main menu items
        for (int i = 0; i < rootMenu.children.size(); i++) {
            MenuItem item = rootMenu.children.get(i);
            float y = position.y + i * itemHeight;

            // Highlight if hovered
            if (item.isHovered(mouseX, mouseY)) {
                inputManager.uiRenderer.drawRect(position.x, y, itemWidth, itemHeight, 0.3f, 0.3f, 0.5f, 1f);
            }

            // Draw text
//            drawText(batch, item.label, position.x + padding, y + padding, Color.WHITE);

            // Draw arrow if has children
            if (!item.children.isEmpty()) {
//                drawText(batch, ">", position.x + itemWidth - 20, y + padding, Color.WHITE);
            }
        }

        // Draw submenu if expanded
        if (activeSubmenu != null && activeSubmenu.expanded) {
            float submenuHeight = activeSubmenu.children.size() * itemHeight;
            float submenuX = activeSubmenu.pos.x + itemWidth;
            float submenuY = activeSubmenu.pos.y;

            // Draw submenu background
            inputManager.uiRenderer.drawRect(submenuX, submenuY, itemWidth, submenuHeight, 0.2f, 0.2f, 0.2f, 0.9f);

            // Draw submenu items
            for (int i = 0; i < activeSubmenu.children.size(); i++) {
                MenuItem subItem = activeSubmenu.children.get(i);
                float y = submenuY + i * itemHeight;

                // Highlight if hovered
                if (subItem.isHovered(mouseX, mouseY)) {
                    inputManager.uiRenderer.drawRect(submenuX, y, itemWidth, itemHeight, 0.3f, 0.3f, 0.5f, 1f);
                }

                // Draw text
//                drawText(batch, subItem.label, submenuX + padding, y + padding, Color.WHITE);
            }
        }
        inputManager.uiRenderer.end();
    }

    // Placeholder methods - implement these based on your system
    private void setElement(ElementType type) {
        // Your implementation
        inputManager.setCurrentlySelectedElement(type);
    }

    private void clearAll() {
        // Your implementation
        System.out.println("Clear all");
        inputManager.clearMatrix();
    }

    public boolean isVisible() {
        return visible;
    }
}