package elements.solid.immovable;

import matrix.CellularMatrix;
import elements.Element;
import elements.solid.Solid;
import org.joml.Vector3f;

public abstract class ImmovableSolid extends Solid {

    public ImmovableSolid(int x, int y) {
        super(x, y);
        isFreeFalling = false;
    }

    @Override
    public void step(CellularMatrix matrix) {
        applyHeatToNeighborsIfIgnited(matrix);
        takeEffectsDamage(matrix);
        spawnSparkIfIgnited(matrix);
        modifyColor();
        customElementFunctions(matrix);
    }

    @Override
    protected boolean actOnNeighboringElement(Element neighbor, int modifiedMatrixX, int modifiedMatrixY, CellularMatrix matrix, boolean isFinal, boolean isFirst, Vector3f lastValidLocation, int depth) {
        return true;
    }
}
