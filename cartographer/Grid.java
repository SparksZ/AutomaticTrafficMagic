import java.util.LinkedList;
import java.util.ArrayList;
import java.awt.Dimension;

public class Grid {
    public final int numRows, numCols;
    private GridSquare[][] squares;
    private Dimension mapSize;
    private int[] rowEnds, colEnds;

    public Grid(int edgeLength, Dimension mapSize) {
        this(edgeLength, edgeLength, mapSize);
    }

    public Grid(int numRows, int numCols, Dimension mapSize) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.mapSize = mapSize;
        int currRowSize, currColSize;
        int rowStartLocation = 0, colStartLocation = 0;
        squares = new GridSquare[numRows][numCols];
        rowEnds = new int[numRows];
        colEnds = new int[numCols];
        for (int row = 0; row < numRows; row++) {
            // doing the math this way should ensure that the (truncated) parts add up to 100%
            currRowSize = mapSize.height*(row + 1)/numRows - rowStartLocation - 1;
            if (row == numRows - 1) {
                currRowSize ++; // it's admittedly a bit of a hack
            }
            rowEnds[row] = rowStartLocation + currRowSize;
            for (int col = 0; col < numCols; col++) {
                currColSize = mapSize.width*(col + 1)/numCols - colStartLocation - 1;
                if (col == numCols - 1) {
                    currColSize++;
                }
                squares[row][col] = new GridSquare(currColSize, currRowSize);
                colEnds[col] = colStartLocation + currColSize;
                colStartLocation += (currColSize + 1);
            }
            rowStartLocation += (currRowSize + 1);
        }
    }

    public void addRoadToGrid(Road r) {
        ArrayList<TimedCoordinate> waypoints = (ArrayList<TimedCoordinate>)r.getWaypoints().clone(); // a shallow copy is fine for this
        int wpIndex = 0;
        boolean allWaypointsAdded = false;
        TimedCoordinate currWP = null;
        TimedCoordinate nextWP = waypoints.get(wpIndex);
        TimedCoordinate finalWP = waypoints.get(waypoints.size() - 1);
        GridSquare currentSquare = null;
        ArrayList<TimedCoordinate> waypointsInSquare = new ArrayList<TimedCoordinate>();
        if (waypoints.size() < 2) {
            throw new IllegalArgumentException("Not enough waypoints in road.");
        }
        while (!allWaypointsAdded) {
            currWP = nextWP;
            currentSquare = getContainingSquare(currWP);
            waypointsInSquare.add(currWP);
            if (currWP == finalWP) {
                currentSquare.addRoadSegment(r.getRoadID(), (ArrayList<TimedCoordinate>)waypointsInSquare.clone());
                waypointsInSquare = null;
                allWaypointsAdded = true;
                continue;
            }
            nextWP = waypoints.get(wpIndex + 1);
            if (currentSquare != getContainingSquare(nextWP)) {
                // split the road segment into 2 segments
                // by inserting waypoint in between currWP and nextWP
                TimedCoordinate[] intersectionWaypoints = findGridEdgeIntersection(currWP, nextWP);
                TimedCoordinate gridEdgeWaypoint = intersectionWaypoints[0];
                waypoints.add(wpIndex + 1, intersectionWaypoints[1]);
                System.out.println("Inserting new waypoint at (" + gridEdgeWaypoint.getX() + ", " + gridEdgeWaypoint.getY() + ")...");
                waypointsInSquare.add(gridEdgeWaypoint);
                currentSquare.addRoadSegment(r.getRoadID(), (ArrayList<TimedCoordinate>)waypointsInSquare.clone());
                nextWP = intersectionWaypoints[1];
                currentSquare = getContainingSquare(nextWP);
                waypointsInSquare = new ArrayList<TimedCoordinate>();
            }
            wpIndex++;
        }
    }

    /**
     * gets the GridSquare surrounding a Coordinate
     * @param c the coordinate to check
     * @return the GridSquare containing the point c for this Grid object
     */
    public GridSquare getContainingSquare(Coordinate c) {
        return squares[getGridRow(c.getY())][getGridCol(c.getX())];
    }

    private int getGridRow(int y) {
        int row = 0;
        while (mapSize.height - y - 1 > rowEnds[row]) {
            row++;
        }
        return row;
    }

    private int getGridCol(int x) {
        int col = 0;
        while (x > colEnds[col]) {
            col++;
        }
        return col;
    }

    public Dimension getMapSize() {
        return mapSize;
    }

    public int[] getRowEnds() {
        return rowEnds;
    }

    public int[] getColEnds() {
        return colEnds;
    }
    
    public GridSquare[][] getSquares() {
        return squares;
    }

    /*public boolean addRoadToGridLocation(int row, int col, int roadID) {
        for (Integer currID : roads.get(row).get(col)) {
            if (currID == roadID) {
                return false;
            }
        }
        roads.get(row).get(col).addLast(roadID);
        return true;
    }*/

    /**
     * This method should be placed in Grid (rather than GridSquare),
     * since a GridSquare doesn't know whether it even contains the selected coordinates.
     * @param t1 the first waypoint (should be in the square)
     * @param t2 the second waypoint (should be outside the square)
     * @return a TimedCoordinate of the intersection time and location
     */
    public TimedCoordinate[] findGridEdgeIntersection(TimedCoordinate t1, TimedCoordinate t2) {
        int gridSquareRow = getGridRow(t1.getY());
        int gridSquareCol = getGridCol(t1.getX());
        GridSquare square = squares[gridSquareRow][gridSquareCol];
        // first check if line segment lies completely in the square
        if (getContainingSquare(t2) == square) {
            return null;
        }
        // assume delta-t of 1 time unit, since I only need relative speeds
        // to find where the road segment will intersect the edge of the square
        double xSpeed = t2.getX() - t1.getX();
        double ySpeed = t2.getY() - t1.getY();
        double horizIntersectionT, vertIntersectionT;
        double nextHorizIntersectionT, nextVertIntersectionT;
        // Find time when equation of line would intersect either side of the gridSquare
        if (xSpeed > 0) { // line is moving to the right
            // horizIntersectionT*xSpeed + t1.getX() = colEnds[gridSquareCol]
            horizIntersectionT = ((double)colEnds[gridSquareCol] - t1.getX())/xSpeed;
            nextHorizIntersectionT = ((double)(colEnds[gridSquareCol] + 1) - t1.getX())/xSpeed;
        }
        else if (xSpeed < 0){
            int leftBorderCoordinate = (gridSquareCol > 0 ? colEnds[gridSquareCol - 1] + 1 : 0);
            horizIntersectionT = ((double)leftBorderCoordinate - t1.getX())/xSpeed;
            nextHorizIntersectionT = ((double)leftBorderCoordinate - 1 - t1.getX())/xSpeed;
        }
        else {
            // line segment is vertical
            horizIntersectionT = Double.POSITIVE_INFINITY;
            nextHorizIntersectionT = Double.POSITIVE_INFINITY;
        }

        if (ySpeed > 0) { // line is moving upward
            int topBorderCoordinate = (gridSquareRow > 0 ? rowEnds[gridSquareRow - 1] + 1 : 0);
            vertIntersectionT = (double)((mapSize.height - topBorderCoordinate) - t1.getY())/ySpeed;
            nextVertIntersectionT = (double)((mapSize.height - topBorderCoordinate + 1) - t1.getY())/ySpeed;
        }
        else if (ySpeed < 0) {
            vertIntersectionT = (double)((mapSize.height - rowEnds[gridSquareRow]) - t1.getY() - 1)/ySpeed;
            nextVertIntersectionT = (double)((mapSize.height - rowEnds[gridSquareRow] - 1) - t1.getY() - 1)/ySpeed;
        }
        else {
            vertIntersectionT = Double.POSITIVE_INFINITY;
            nextVertIntersectionT = Double.POSITIVE_INFINITY;
        }
        double intersectionTime = Math.min(horizIntersectionT, vertIntersectionT);
        int intersectionX = (int)Math.round(t1.getX() + xSpeed*intersectionTime);
        int intersectionY = (int)Math.round(t1.getY() + ySpeed*intersectionTime);
        double transformedTime = t1.getTime() + intersectionTime*Math.hypot(xSpeed, ySpeed);
        TimedCoordinate intersectionWP = new TimedCoordinate(transformedTime, intersectionX, intersectionY);
        double postIntersectionTime = Math.min(nextHorizIntersectionT, nextVertIntersectionT);
        int postIntersectionX, postIntersectionY;
        if (horizIntersectionT < vertIntersectionT) { // intersecting horizontally-adjacent rectangle
            postIntersectionY = (int)Math.round(t1.getY() + ySpeed*postIntersectionTime);
            if (xSpeed > 0) {
                postIntersectionX = intersectionX + 1;
            }
            else {
                postIntersectionX = intersectionX - 1;
            }
        } else { // intersecting vertically-adjacent rectangle (or diagonally)
            postIntersectionX = (int)Math.round(t1.getX() + xSpeed*postIntersectionTime);
            if (ySpeed < 0) {
                postIntersectionY = intersectionY - 1;
            }
            else {
                postIntersectionY = intersectionY + 1;
            }
        }
        postIntersectionTime = t1.getTime() + postIntersectionTime*Math.hypot(xSpeed, ySpeed);
        TimedCoordinate postIntersectionWP = new TimedCoordinate(postIntersectionTime, postIntersectionX, postIntersectionY);
        // since the line segment does not end in the same square it started in,
        // it definitely has nonzero velocity in either one or both directions.

        return new TimedCoordinate[]{intersectionWP, postIntersectionWP};
    }
}
