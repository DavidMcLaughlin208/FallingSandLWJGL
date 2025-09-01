package elements.solid.movable;

import org.joml.Vector3f;
import matrix.CellularMatrix;


public class Coal extends MovableSolid {

    public Coal(int x, int y) {
        super(x, y);
        vel = new Vector3f(0f, -124f,0f);
        frictionFactor = .4f;
        inertialResistance = .8f;
        mass = 200;
        flammabilityResistance = 100;
        resetFlammabilityResistance = 35;
    }

    @Override
    public void spawnSparkIfIgnited(CellularMatrix matrix) {
        if (getRandomInt(20) > 2) return;
        super.spawnSparkIfIgnited(matrix);
    }
}
