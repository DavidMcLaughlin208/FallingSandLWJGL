package elements.solid.immovable;

import util.Color;
import org.joml.Vector3f;
import matrix.CellularMatrix;
import util.ColorConstants;
import elements.Element;
import elements.EmptyCell;

public class Ground extends ImmovableSolid{

    public Ground(int x, int y) {
        super(x, y);
        vel = new Vector3f(0f, 0f,0f);
        frictionFactor = 0.5f;
        inertialResistance = 1.1f;
        mass = 200;
        health = 250;
    }

    @Override
    public boolean receiveHeat(CellularMatrix matrix, int heat) {
        return false;
    }

    @Override
    public void customElementFunctions(CellularMatrix matrix) {
//        Element above = matrix.get(matrixX, matrixY + 1);
//        if (above == null || above instanceof EmptyCell) {
//            this.color = ColorConstants.getColorByName("Grass");
//        } else {
//            this.color = ColorConstants.getColorForElementType(this.elementType);
//        }
    }

}
