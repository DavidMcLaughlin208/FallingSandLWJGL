package elements.gas;

import org.joml.Vector3f;
import matrix.CellularMatrix;
import elements.ElementType;

public class Steam extends Gas {

    public Steam(int x, int y) {
        super(x, y);
        vel = new Vector3f(0,124f,0);
        inertialResistance = 0;
        mass = 1;
        frictionFactor = 1f;
        density = 5;
        dispersionRate = 2;
        lifeSpan = getRandomInt(2000) + 1000;
    }

    @Override
    public void checkLifeSpan(CellularMatrix matrix) {
        if (lifeSpan != null) {
            lifeSpan--;
            if (lifeSpan <= 0) {
                if (Math.random() > 0.5) {
                    die(matrix);
                } else {
                    dieAndReplace(matrix, ElementType.WATER);
                }
            }
        }
    }

    @Override
    public boolean receiveHeat(CellularMatrix matrix, int heat) {
        return false;
    }
}
