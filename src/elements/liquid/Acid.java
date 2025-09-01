package elements.liquid;

import org.joml.Vector3f;
import matrix.CellularMatrix;
import elements.Element;
import elements.ElementType;

public class Acid extends Liquid {

    public int corrosionCount = 3;
    public Acid(int x, int y) {
        super(x, y);
        vel = new Vector3f(0,-124f,0);
        inertialResistance = 0;
        mass = 50;
        frictionFactor = 1f;
        density = 2;
        dispersionRate = 2;
    }

    @Override
    public boolean actOnOther(Element other, CellularMatrix matrix) {
        other.stain(-1, 1, -1, 0);
        if (!isReactionFrame() || other == null) return false;
        boolean corroded = other.corrode(matrix);
        if (corroded) corrosionCount -= 1;
        if (corrosionCount <= 0) {
            dieAndReplace(matrix, ElementType.FLAMMABLEGAS);
            return true;
        }
        return false;
    }

    @Override
    public boolean corrode(CellularMatrix matrix) {
        return false;
    }

    @Override
    public boolean receiveHeat(CellularMatrix matrix, int heat) {
        return false;
    }
}
