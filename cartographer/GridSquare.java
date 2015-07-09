import java.util.ArrayList;

public class GridSquare {
    private int numRoadsInSquare = 0;
    private ArrayList<Road> roadsInSquare;
    private ArrayList<RoadSegment> segments;
    private final int width, height;

    public GridSquare(int width, int height) {
        this.width = width;
        this.height = height;
        segments = new ArrayList<RoadSegment>();
    }

    public int getNumRoads() {
        return numRoadsInSquare;
    }

    public void addRoadSegment(int roadIndex, ArrayList<TimedCoordinate> waypoints) {
        RoadSegment currSegment = new RoadSegment(roadIndex, waypoints);
        segments.add(currSegment);
        // TODO: check if another segment of the same road is already in the square.
        // Then only increment numRoadsInSquare if this is a new road.
        numRoadsInSquare++;
    }
    
    public ArrayList<RoadSegment> getSegments() {
        return segments;
    }
}

