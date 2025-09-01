package elements.solid.immovable;

import matrix.CellularMatrix;
import util.ColorConstants;
import elements.Element;
import elements.ElementType;
import org.joml.Vector3f;

public class SlimeMold extends ImmovableSolid {

    public SlimeMold(int x, int y) {
        super(x, y);
        vel = new Vector3f(0f, 0f,0f);
        frictionFactor = 0.5f;
        inertialResistance = 1.1f;
        mass = 500;
        flammabilityResistance = 10;
        resetFlammabilityResistance = 0;
        health = 40;
    }

    @Override
    public void step(CellularMatrix matrix) {
        super.step(matrix);
        infectNeighbors(matrix);
    }

    private boolean infectNeighbors(CellularMatrix matrix) {
        if (!isEffectsFrame() || isIgnited) return false;
        for (int x = getMatrixX() - 1; x <= getMatrixX() + 1; x++) {
            for (int y = getMatrixY() - 1; y <= getMatrixY() + 1; y++) {
                if (!(x == 0 && y == 0)) {
                    Element neighbor = matrix.get(x, y);
                    if (neighbor != null) {
                        neighbor.infect(matrix);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void takeFireDamage(CellularMatrix matrix) {
        health -= fireDamage;
    }

    public boolean infect(CellularMatrix matrix) {
        return false;
    }

    @Override
    public void modifyColor() {
        if (isIgnited) {
            color = ColorConstants.getRandomFireColor();
        } else {
            color = ColorConstants.getColorForElementType(ElementType.SLIMEMOLD, this.getMatrixX(), this.getMatrixY());
        }
    }


}
