package box2dfs.linesimplification;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Visvalingam {

    private static final float DEFAULT_THRESHOLD = .4f;

    private Visvalingam() { throw new IllegalStateException("Should not instantiate Visvalingam"); }

    public static List<Vector2f> simplify(List<Vector2f> verts) {
        List<Vector2f> simplifiedOnce = simplify(verts, DEFAULT_THRESHOLD);
//        return simplifiedOnce;
//        List<Vector2f> simplifiedTwice = simplify(simplifiedOnce, DEFAULT_THRESHOLD);
        return simplify(simplifiedOnce, 1.8f);
//        return simplify(simplifiedTwice, DEFAULT_THRESHOLD);
    }

    public static List<Vector2f> simplify(List<Vector2f> verts, float threshold) {
        if (verts.size() <= 3) {
            return verts;
        }
        List<Vector2f> simplifiedVerts = new ArrayList<>();
        int skippedCount = 0;
        int vertsSize = verts.size();
        calculateTriangleAreaAndAddPointToVerts(verts.get(vertsSize - 1), verts.get(0), verts.get(1), simplifiedVerts, threshold);
        for (int i = 0; i < verts.size() - 2; i++) {
            Vector2f point1 = verts.get(i);
            Vector2f point2 = verts.get(i + 1);
            Vector2f point3 = verts.get(i + 2);
            boolean added = calculateTriangleAreaAndAddPointToVerts(point1, point2, point3, simplifiedVerts, threshold);
//            if (added) {
//                skippedCount = 0;
//            } else {
//                if (skippedCount > 11) {
//                    int index = simplifiedVerts.size();
//                        simplifiedVerts.add(index, verts.get(i - (2*(skippedCount / 3))));
//                        simplifiedVerts.add(index + 1, verts.get(i - (skippedCount / 3)));
//                        skippedCount = 0;
//
//                } else {
//                    skippedCount++;
//                }
//            }

        }
        calculateTriangleAreaAndAddPointToVerts(verts.get(vertsSize - 2), verts.get(vertsSize - 1), verts.get(0), simplifiedVerts, threshold);
        return simplifiedVerts;

    }

    private static boolean calculateTriangleAreaAndAddPointToVerts(Vector2f point1, Vector2f point2, Vector2f point3, List<Vector2f> simplifiedVerts, float threshold) {
        float area = Math.abs(((point1.x * (point2.y - point3.y)) + (point2.x * (point3.y - point1.y)) + (point3.x * (point1.y - point2.y))) / 2f);
        if (area > threshold) {
            simplifiedVerts.add(new Vector2f(point2));
            return true;
        } else {
            return false;
        }
    }

}
