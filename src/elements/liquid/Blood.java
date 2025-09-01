package elements.liquid;

import util.Color;
import org.joml.Vector3f;
import matrix.CellularMatrix;
import elements.Element;

public class Blood extends Liquid {

    public Blood(int x, int y) {
        super(x, y);
        vel = new Vector3f(0,-124f,0);
        inertialResistance = 0;
        mass = 100;
        frictionFactor = 1f;
        density = 6;
        dispersionRate = 5;
        coolingFactor = 5;
    }

    @Override
    public boolean receiveHeat(CellularMatrix matrix, int heat) {
        return false;
    }

    @Override
    public boolean actOnOther(Element other, CellularMatrix matrix) {
        other.stain(new Color(0.5f, 0, 0, 1));
        if (other.shouldApplyHeat()) {
            other.receiveCooling(matrix, coolingFactor);
            coolingFactor--;
            if (coolingFactor <= 0) {
                die(matrix);
                return true;
            }
            return false;
        }
        return false;
    }

}
