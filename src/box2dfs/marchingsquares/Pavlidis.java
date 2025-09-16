package box2dfs.marchingsquares;

import org.joml.Vector2f;
import elements.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pavlidis {

    private Pavlidis() { throw new IllegalStateException("Cannot instantiate Pavlidis"); }

    private static final Map<Direction, Map<DirectionalVector, Direction>> directionMap = new HashMap<>();
    private static final Map<Direction, Direction> rotateDirectionMap = new HashMap<>();

    static {
        // This is used to determine the next relative direction when moving to a found element
        directionMap.put(Direction.North, new HashMap<>());
        Map<DirectionalVector, Direction> northMap = directionMap.get(Direction.North);
        northMap.put(DirectionalVector.UpLeft, Direction.West);
        northMap.put(DirectionalVector.Up, Direction.North);
        northMap.put(DirectionalVector.UpRight, Direction.East);

        directionMap.put(Direction.East, new HashMap<>());
        Map<DirectionalVector, Direction> eastMap = directionMap.get(Direction.East);
        eastMap.put(DirectionalVector.UpLeft, Direction.North);
        eastMap.put(DirectionalVector.Up, Direction.East);
        eastMap.put(DirectionalVector.UpRight, Direction.South);
        directionMap.put(Direction.South, new HashMap<>());

        Map<DirectionalVector, Direction> southMap = directionMap.get(Direction.South);
        southMap.put(DirectionalVector.UpLeft, Direction.East);
        southMap.put(DirectionalVector.Up, Direction.South);
        southMap.put(DirectionalVector.UpRight, Direction.West);

        directionMap.put(Direction.West, new HashMap<>());
        Map<DirectionalVector, Direction> westMap = directionMap.get(Direction.West);
        westMap.put(DirectionalVector.UpLeft, Direction.South);
        westMap.put(DirectionalVector.Up, Direction.West);
        westMap.put(DirectionalVector.UpRight, Direction.North);

        // This is used to rotate direction if no element is found at upLeft, up, or upRight
        rotateDirectionMap.put(Direction.North, Direction.East);
        rotateDirectionMap.put(Direction.East, Direction.South);
        rotateDirectionMap.put(Direction.South, Direction.West);
        rotateDirectionMap.put(Direction.West, Direction.North);
    }


    public static List<Vector2f> getOutliningVerts(List<List<Element>> elements) {
        List<Vector2f> outliningVerts = new ArrayList<>();
        Element startingPoint = null;
        Vector2f startingVector = null;
        // Brute force from the bottom left of the matrix to find starting element
        for (int y = elements.size() - 1; y >= 0; y--) {
            if (startingVector != null) break;
            List<Element> row = elements.get(y);
            for (int x = 0; x < row.size(); x++) {
                Element element = row.get(x);
                if (element != null) {
                    startingPoint = element;
                    startingVector = new Vector2f(x, y);
                    outliningVerts.add(new Vector2f(startingVector));
                    break;
                }
            }
        }
        if (startingPoint == null) {
            return outliningVerts;
        }
        Element currentElement = null;
        Vector2f currentLocation = new Vector2f(startingVector);
        Direction currentDirection = Direction.North;
        Vector2f upLeftVector, upVector, upRightVector;
        Element upLeft, up, upRight;
        while (currentElement != startingPoint) {
            upLeftVector = getDirectionalVector(currentLocation, currentDirection, DirectionalVector.UpLeft);
            upLeft = getFromArray(upLeftVector, elements);
            if (upLeft != null) {
                currentElement = upLeft;
                currentLocation.set(upLeftVector);
                outliningVerts.add(new Vector2f(currentLocation));
                currentDirection = getNewRelativeDirection(currentDirection, DirectionalVector.UpLeft);
                continue;
            }
            upVector = getDirectionalVector(currentLocation, currentDirection, DirectionalVector.Up);
            up = getFromArray(upVector, elements);
            if (up != null) {
                currentElement = up;
                currentLocation.set(upVector);
                outliningVerts.add(new Vector2f(currentLocation));
                currentDirection = getNewRelativeDirection(currentDirection, DirectionalVector.Up);
                continue;
            }
            upRightVector = getDirectionalVector(currentLocation, currentDirection, DirectionalVector.UpRight);
            upRight = getFromArray(upRightVector, elements);
            if (upRight != null) {
                currentElement = upRight;
                currentLocation.set(upRightVector);
                outliningVerts.add(new Vector2f(currentLocation));
                currentDirection = getNewRelativeDirection(currentDirection, DirectionalVector.UpRight);
                continue;
            }
            currentDirection = rotateDirectionMap.get(currentDirection);

        }
        outliningVerts.remove(outliningVerts.size() - 1);
        return outliningVerts;
    }

    private static Direction getNewRelativeDirection(Direction currentDirection, DirectionalVector directionalVector) {
        return directionMap.get(currentDirection).get(directionalVector);
    }

    private static Vector2f getDirectionalVector(Vector2f current, Direction direction, DirectionalVector directionalVector) {
        switch (direction) {
            case North:
                switch (directionalVector) {
                    case UpLeft:
                        return new Vector2f(current.x - 1, current.y - 1);
                    case Up:
                        return new Vector2f(current.x, current.y - 1);
                    case UpRight:
                        return new Vector2f(current.x + 1, current.y - 1);
                }
            case East:
                switch (directionalVector) {
                    case UpLeft:
                        return new Vector2f(current.x + 1,  current.y - 1);
                    case Up:
                        return new Vector2f(current.x + 1,  current.y);
                    case UpRight:
                        return new Vector2f(current.x + 1,  current.y + 1);
                }
            case South:
                switch (directionalVector) {
                    case UpLeft:
                        return new Vector2f(current.x + 1, current.y + 1);
                    case Up:
                        return new Vector2f(current.x, current.y + 1);
                    case UpRight:
                        return new Vector2f(current.x - 1, current.y + 1);
                }
            case West:
                switch (directionalVector) {
                    case UpLeft:
                        return new Vector2f(current.x - 1, current.y + 1);
                    case Up:
                        return new Vector2f(current.x - 1, current.y);
                    case UpRight:
                        return new Vector2f(current.x - 1, current.y - 1);
                }
            default:
                throw new IllegalStateException("Impossible combination of Direction and DirectionalVector");
        }
    }

    private static Element getFromArray(Vector2f cur, List<List<Element>> elements) {
        if (cur.y >= elements.size() || cur.y < 0 || cur.x >= elements.get((int) cur.y).size() || cur.x < 0) {
            return null;
        }
        return elements.get((int) cur.y).get((int) cur.x);
    }

    private enum Direction {
        North,
        East,
        South,
        West
    }

    private enum DirectionalVector {
        UpLeft,
        Up,
        UpRight
    }
}
