package elements.solid.immovable;

import org.joml.Vector3f;
import matrix.CellularMatrix;

public class Brick extends ImmovableSolid {

    public Brick(int x, int y) {
        super(x, y);
        vel = new Vector3f(0f, 0f,0f);
        frictionFactor = 0.5f;
        inertialResistance = 1.1f;
        mass = 500;
        explosionResistance = 4;
    }

    @Override
    public boolean receiveHeat(CellularMatrix matrix, int heat) {
        return false;
    }
}
