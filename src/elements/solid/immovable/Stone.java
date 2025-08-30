package elements.solid.immovable;

import org.joml.Vector3f;
import matrix.CellularMatrix;
import elements.solid.immovable.ImmovableSolid;

public class Stone extends ImmovableSolid {

    public Stone(int x, int y) {
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
