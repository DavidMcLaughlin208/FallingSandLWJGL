package box2dfs.marchingsquares;

import org.joml.Vector2f;
import elements.Element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MooreNeighborTracing {

    private MooreNeighborTracing() { throw new IllegalStateException("Cannot instantiate MooreNeighborTracing"); }

    private static final Map<Integer, Vector2f> neighborRelativeLocationMap = new HashMap<>();
    private static final Map<IncomingDirection, Integer> directionIndexOffsetMap = new HashMap<>();
    private static final Map<Integer, IncomingDirection> newRelativeDirectionMap = new HashMap<>();

    static {
        neighborRelativeLocationMap.put(1, new Vector2f(0, -1));
        neighborRelativeLocationMap.put(2, new Vector2f(1, -1));
        neighborRelativeLocationMap.put(3, new Vector2f(1, 0));
        neighborRelativeLocationMap.put(4, new Vector2f(1, 1));
        neighborRelativeLocationMap.put(5, new Vector2f(0, 1));
        neighborRelativeLocationMap.put(6, new Vector2f(-1, 1));
        neighborRelativeLocationMap.put(7, new Vector2f(-1, 0));
        neighborRelativeLocationMap.put(8, new Vector2f(-1, -1));

        directionIndexOffsetMap.put(IncomingDirection.North, 0);
        directionIndexOffsetMap.put(IncomingDirection.West, 2);
        directionIndexOffsetMap.put(IncomingDirection.South, 4);
        directionIndexOffsetMap.put(IncomingDirection.East, 6);

        newRelativeDirectionMap.put(1, IncomingDirection.East);
        newRelativeDirectionMap.put(2, IncomingDirection.East);
        newRelativeDirectionMap.put(3, IncomingDirection.North);
        newRelativeDirectionMap.put(4, IncomingDirection.North);
        newRelativeDirectionMap.put(5, IncomingDirection.West);
        newRelativeDirectionMap.put(6, IncomingDirection.West);
        newRelativeDirectionMap.put(7, IncomingDirection.South);
        newRelativeDirectionMap.put(8, IncomingDirection.South);

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
//        List<Element> elementList = new ArrayList<>();
        Element currentElement;
        Vector2f currentLocation = new Vector2f(startingVector);
        IncomingDirection currentIncomingDirection = IncomingDirection.East;
        boolean endConditionMet = false;
        IncomingDirection incomingDirectionToFinalPoint = null;
        int neighborsVisited = 0;
        while (!endConditionMet) {
            if (neighborsVisited >= 10000) {
                System.out.println("Stuck in infinite loop");
                return outliningVerts;
            }
            neighborsVisited++;
            for (int i = 1; i <= 8; i++) {
                Vector2f neighborLocation = getNeighboringElementLocationForIndexAndDirection(i, currentIncomingDirection, currentLocation, elements);
                Element neighbor = getNeighboringElement(neighborLocation, elements);
                if (neighbor == null) {
                    if (i == 8) {
                        // Element is an island
                        endConditionMet = true;
                        break;
                    }
                    continue;
                }
                currentElement = neighbor;
                currentLocation = new Vector2f(neighborLocation);
                currentIncomingDirection = getNewIncomingDirection(i, currentIncomingDirection);
                if (currentElement == startingPoint) {
                    endConditionMet = true;
                    break;
                }
                int indexOfCurrentLocation = outliningVerts.indexOf(currentLocation);
                if (indexOfCurrentLocation == -1) {
                    outliningVerts.add(new Vector2f(neighborLocation));
//                    elementList.add(currentElement);
                } else {
                    outliningVerts.remove(indexOfCurrentLocation);
                }
                break;
            }
        }
        return outliningVerts;
    }

    private static IncomingDirection getNewIncomingDirection(int i, IncomingDirection currentIncomingDirection) {
        int offsetIndex = getOffsetIndex(i, currentIncomingDirection);
        return newRelativeDirectionMap.get(offsetIndex);
    }

    private static Vector2f getNeighboringElementLocationForIndexAndDirection(int i, IncomingDirection currentIncomingDirection, Vector2f currentLocation, Array<Array<Element>> elements) {
        int offsetIndex = getOffsetIndex(i, currentIncomingDirection);
        return getNeighborLocation(offsetIndex, currentLocation);
    }

    private static Element getNeighboringElement(Vector2f neighborLocation, List<List<Element>> elements) {
        if (neighborLocation.y >= elements.size() || neighborLocation.y < 0 || neighborLocation.x >= elements.get((int) neighborLocation.y).size() || neighborLocation.x < 0) {
            return null;
        } else {
            return elements.get((int) neighborLocation.y).get((int) neighborLocation.x);
        }
    }

    private static Vector2f getNeighborLocation(int offsetIndex, Vector2f currentLocation) {
        Vector2f offsetVector = neighborRelativeLocationMap.get(offsetIndex);
        return new Vector2f(currentLocation.x + offsetVector.x, currentLocation.y + offsetVector.y);
    }

    private static int getOffsetIndex(int i, IncomingDirection currentIncomingDirection) {
        int newIndex = i + directionIndexOffsetMap.get(currentIncomingDirection);
        if (newIndex > 8) {
            return newIndex - 8;
        } else {
            return newIndex;
        }
    }

    private enum IncomingDirection {
        North,
        South,
        East,
        West
    }

}
