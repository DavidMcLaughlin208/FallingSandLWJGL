package elements.solid.movable;

import org.joml.Vector3f;
import matrix.CellularMatrix;

public class Sand extends MovableSolid {

    public Sand(int x, int y) {
        super(x, y);
        vel = new Vector3f(Math.random() > 0.5 ? -1 : 1, -124f,0f);
        frictionFactor = 0.9f;
        inertialResistance = .1f;
        mass = 150;
    }

    @Override
    public boolean receiveHeat(CellularMatrix matrix, int heat) {
        return false;
    }

}
