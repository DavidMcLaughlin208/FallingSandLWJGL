package box2dfs.douglaspeucker;

import org.joml.Vector2f;

/**
 * Represents a point on a plane. A point consists of 2 coordinates - x and y.
 */
public interface Point {

    double getX();

    double getY();

    Vector2f getPosition();
}
