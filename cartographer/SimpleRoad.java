import java.awt.Dimension;
import java.util.LinkedList;

/**
 * represents a basic straight-line, 2-way road
 */
public class SimpleRoad extends Road {
    public SimpleRoad(Grid aGrid, Coordinate origin, double direction, int roadID) {
        super(aGrid, origin, direction, roadID);
    }

    public Coordinate roadLocationAtTime(double t) {
        double xVal = origin.getX() + t*Math.cos(Math.toRadians(direction));
        double yVal = origin.getY() + t*Math.sin(Math.toRadians(direction));
        return new Coordinate(xVal, yVal);
    }

    /**
     * build representation of road as a set of line segments
     * The mapSize parameter is necessary so that the road can know
     * where it should start and end
     * @param mapSize the dimensions of the map that contains this road
     */
    public void generateWaypointsForMap(Dimension mapSize) {
        // SimpleRoad is a straight line, so only 2 waypoints are necessary

        /*
         * This problem is deceptively complex. The road in the map is
         * represented as a line segment in a rectangle. This line segment
         * can start and end in many places in the containing box.
         *
         * Specifically, there are 14 cases:
         * 1. start from left, end at top
         * 2. start from top, end at left
         * 3. start from left, end at right
         * 4. start from right, end at left
         * 5. start from left, end at bottom
         * 6. start from bottom, end at left
         * 7. start from top, end at right
         * 8. start from right, end at top
         * 9. start from bottom, end at right
         * 10. start from right, end at bottom
         * 11. start from bottom, end at top (moving right)
         * 12. start from top, end at bottom (moving left)
         * 13. start from top, end at bottom (moving right)
         * 14. start from bottom, edn at top (moving left)
         */
        waypoints = new LinkedList<TimedCoordinate>();
        double dirCos = Math.cos(Math.toRadians(direction));
        double dirSin = Math.sin(Math.toRadians(direction));
        // timeAtX0 and timeAtY0 are time at x = 0, and time at y = 0, respectively
        double timeAtX0 = -1*origin.getX()/dirCos;
        double timeAtXEnd = (((double)mapSize.getWidth()) - origin.getX())/dirCos;
        double timeAtY0 = -1*origin.getY()/dirSin;
        double timeAtYEnd = (((double)mapSize.getHeight()) - origin.getY())/dirSin;
        double startXCandidate, startYCandidate, endXCandidate, endYCandidate;
        // choose the first value to intersect the borders of the map
        // then trim the other value so they start at the same time
        if (timeAtX0 <= 0) {
            // line is going left to right (x increases with t)
            startXCandidate = origin.getX() + timeAtX0*dirCos;
            startYCandidate = origin.getY() + timeAtX0*dirSin;
            endXCandidate = origin.getX() + timeAtXEnd*dirCos;
            endYCandidate = origin.getY() + timeAtXEnd*dirSin; // left to right
            if (startYCandidate < 0) {
                startXCandidate = origin.getX() + timeAtY0*dirCos;
                startYCandidate = origin.getY() + timeAtY0*dirSin; // bottom to right
                //startYCandidate and endYCandidate will never both be < 0
                if (endYCandidate > 0) {
                    endXCandidate = origin.getX() + timeAtYEnd*dirCos;
                    endYCandidate = origin.getY() + timeAtYEnd*dirSin; // bottom to top (moving right)
                }
            }
            else if (startYCandidate > (double)mapSize.getHeight()) {
                startXCandidate = origin.getX() + timeAtYEnd*dirCos;
                startYCandidate = origin.getY() + timeAtYEnd*dirSin; // top to right
                if (endYCandidate < 0) {
                    endXCandidate = origin.getX() + timeAtY0*dirCos;
                    endYCandidate = origin.getY() + timeAtY0*dirSin; // top to bottom (moving right)
                }
            }
            else if (endYCandidate < 0) {
                endXCandidate = origin.getX() + timeAtY0*dirCos;
                endYCandidate = origin.getY() + timeAtY0*dirSin; // left to bottom
            }
            else if (endYCandidate > (double)mapSize.getHeight()) {
                endXCandidate = origin.getX() + timeAtYEnd*dirCos;
                endYCandidate = origin.getY() + timeAtYEnd*dirSin; //left to top
            }
        }
        else {
            // line is going right to left (x decreases as t increases)
            startXCandidate = origin.getX() + timeAtXEnd*dirCos;
            startYCandidate = origin.getY() + timeAtXEnd*dirSin;
            endXCandidate = origin.getX() + timeAtX0*dirCos;
            endYCandidate = origin.getY() + timeAtX0*dirSin; // right to left
            if (startYCandidate < 0) {
                startXCandidate = origin.getX() + timeAtY0*dirCos;
                startYCandidate = origin.getY() + timeAtY0*dirSin; // right to bottom
                if (endYCandidate > 0) {
                    endXCandidate = origin.getX() + timeAtYEnd*dirCos;
                    endYCandidate = origin.getY() + timeAtYEnd*dirSin; // top to bottom (moving left)
                }
            }
            else if (startYCandidate > (double)mapSize.getHeight()) {
                startXCandidate = origin.getX() + timeAtYEnd*dirCos;
                startYCandidate = origin.getY() + timeAtYEnd*dirSin; // right to top
                if (endYCandidate < 0) {
                    endXCandidate = origin.getX() + timeAtY0*dirCos;
                    endYCandidate = origin.getY() + timeAtY0*dirSin; // bottom to top (moving left)
                }
            }
            else if (endYCandidate < 0) {
                endXCandidate = origin.getX() + timeAtY0*dirCos;
                endYCandidate = origin.getY() + timeAtY0*dirSin; // bottom to left
            }
            else if (endYCandidate > 0) {
                endXCandidate = origin.getX() + timeAtYEnd*dirCos;
                endYCandidate = origin.getY() + timeAtYEnd*dirSin; // top to left
            }
        }

        waypoints.add(new TimedCoordinate(0, startXCandidate, startYCandidate));
        waypoints.add(new TimedCoordinate((double)mapSize.width, endXCandidate, endYCandidate));
    }
}
