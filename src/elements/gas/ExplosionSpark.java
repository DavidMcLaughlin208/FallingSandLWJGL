package elements.gas;

import matrix.CellularMatrix;
//import effects.EffectColors;
import elements.Element;
import elements.EmptyCell;
import elements.liquid.Liquid;
import elements.solid.Solid;
import org.joml.Vector3f;


public class ExplosionSpark extends Gas {

    public ExplosionSpark(int x, int y) {
        super(x, y);
        vel = new Vector3f(0,64f,0);
        inertialResistance = 0;
        mass = 10;
        frictionFactor = 1f;
        density = 4;
        dispersionRate = 4;
        flammabilityResistance = 25;
        isIgnited = true;
        lifeSpan = getRandomInt(20);
        temperature = 3;
    }

    @Override
    public void step(CellularMatrix matrix) {
        super.step(matrix);
//        this.color = EffectColors.getRandomFireColor();
    }

    @Override
    protected boolean actOnNeighboringElement(Element neighbor, int modifiedMatrixX, int modifiedMatrixY, CellularMatrix matrix, boolean isFinal, boolean isFirst, Vector3f lastValidLocation, int depth) {
        boolean acted = actOnOther(neighbor, matrix);
        if (acted) return true;
        if (neighbor instanceof EmptyCell) {
            if (isFinal) {
                swapPositions(matrix, neighbor, modifiedMatrixX, modifiedMatrixY);
            } else {
                return false;
            }
        } else if (neighbor instanceof Spark) {
            return false;
        } else if (neighbor instanceof Smoke) {
            neighbor.die(matrix);
            return false;
        } else if (neighbor instanceof Liquid || neighbor instanceof Solid || neighbor instanceof Gas) {
            neighbor.receiveHeat(matrix, heatFactor);
            die(matrix);
            return true;
        }
        return false;
    }

    @Override
    public boolean receiveHeat(CellularMatrix matrix, int heat) {
        return false;
    }

    @Override
    public void modifyColor() { }

}
