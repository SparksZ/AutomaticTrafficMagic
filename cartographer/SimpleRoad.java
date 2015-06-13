/**
 * represents a basic straight-line, 2-way road
 */
public class SimpleRoad extends Road {
    public SimpleRoad(Grid aGrid, Coordinate origin, double direction, int roadID) {
        super(aGrid, origin, direction, roadID);
    }

    public Coordinate roadLocationAtTime(double t) {
        return null;
    }
}
