package elements.gas;

import matrix.CellularMatrix;
import org.joml.Vector3f;

public class Smoke extends Gas {

    public Smoke(int x, int y) {
        super(x, y);
        vel = new Vector3f(0,124f,0);
        inertialResistance = 0;
        mass = 1;
        frictionFactor = 1f;
        density = 3;
        dispersionRate = 2;
        lifeSpan = getRandomInt(250) + 450;
    }

    @Override
    public boolean receiveHeat(CellularMatrix matrix, int heat) {
        return false;
    }
}
