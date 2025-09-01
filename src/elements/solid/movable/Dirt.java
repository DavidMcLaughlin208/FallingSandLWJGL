package elements.solid.movable;

import org.joml.Vector3f;
import matrix.CellularMatrix;

public class Dirt extends MovableSolid {

    public Dirt(int x, int y) {
        super(x, y);
        vel = new Vector3f(0f, -124f,0f);
        frictionFactor = .6f;
        inertialResistance = .8f;
        mass = 200;
    }

    @Override
    public boolean receiveHeat(CellularMatrix matrix, int heat) {
        return false;
    }

}
