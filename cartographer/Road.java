/**
 * @author Kendall Merritt
 * @author Tyler Durkota
 */

import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Random;
import java.awt.Dimension;

public abstract class Road {
    protected int speedLimit;
    protected boolean oneWay;
    protected Coordinate origin;
    protected LinkedList<LinkedList<Car>> cars;
    protected Grid theGrid;
    protected double direction;
    protected int roadID;
    protected double startT, endT;
    protected ArrayList<TimedCoordinate> waypoints;

    public static final int DEFAULT_SPEED_LIMIT = 35;
    public static final int PERPENDICULAR_TENDENCY = 3;
    public static final int MIN_BRANCHING_ANGLE = 15;

    /**
     * Constructs a new Road object with minimal parameters
     * Assumes 2-way, default speed
     * @param aGrid the Grid object to associate with this Road
     * @param origin the location where this road starts (in the map's coordinate system)
     * @param direction the angle (in degrees) at which it branches off its parent road, relative to horizontal
     * @param roadID the integral identifier assigned to this road
     */
    public Road(Grid aGrid, Coordinate origin, double direction, int roadID) {
        this(DEFAULT_SPEED_LIMIT, false, aGrid, origin, direction, roadID);
    }

    /**
     * Constructs a new Road object
     * @param speedLimit The road's speed limit
     * @param oneWay True if one-way road, false if two-way road
     * @param aGrid the Grid object to associate with this Road
     * @param origin the location where this road starts (in the map's coordinate system)
     * @param direction the angle (in degrees) at which it branches off its parent road, relative to horizontal
     * @param roadID the integral identifier assigned to this road
     */
    public Road(int speedLimit, boolean oneWay, Grid aGrid, Coordinate origin, double direction, int roadID) {
        this.speedLimit = speedLimit;
        this.oneWay = oneWay;
        this.cars = new LinkedList<LinkedList<Car>>();
        this.theGrid = aGrid;
        this.origin = origin;
        this.direction = direction;
        this.roadID = roadID;
    }

    /**
     * Updates the speed limit
     * @param speedLimit The road's speed limit
     */
    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }

    /**
     * @return the road's speed limit
     */
    public int getSpeedLimit() {
        return speedLimit;
    }

    /**
     * @return whether the road is one way or not
     */
    public boolean isOneWay() {
        return this.oneWay;
    }

    /**
     * @return the road's origin as a Coordinate
     */
    public Coordinate getOrigin() {
        return origin;
    }

    /**
     * @return the road's ID number
     */
    public int getRoadID() {
        return roadID;
    }

    public ArrayList<TimedCoordinate> getWaypoints() {
        return waypoints;
    }

    /**
     * Called once when the road is created, and on demand
     * @param mapSize the size of the corresponding map
     */
    public abstract void generateWaypointsForMap(Dimension mapSize);

    /**
     * Each road will know how to generate and draw itself
     * @return the number of roads generated by this function
     */
    //public abstract int generateRoad();

    /**
     * @param t the input to a parametric function describing this road
     * @return the coordinate of where the road is at this time
     */
    public abstract Coordinate roadLocationAtTime(double t);


    /**
     * @param t the input to a parametric function describing this road
     * @return the number of roads that were made from this branch operation
     */
    /*
    public int branch(double t) {
        // select a road type
        // select an angle
        double random_num = Roads.prng.nextGaussian();
        boolean leftOrRight = (Roads.prng.nextBoolean() ? -1 : 1)
        // I combine a logistic function with a normally-distributed random value
        // to give a bounded function that tends toward the value 90
        double angle = leftOrRight*(MIN_BRANCHING_ANGLE + (180 - MIN_BRANCHING_ANGLE)/(1 + Math.exp(random_num/PERPENDICULAR_TENDENCY)));
        //create road of random type (requires reflection and/or switching over a random number)
        Road nextRoad = makeNewRoad(theGrid, roadLocationAtTime(t), direction + angle)
        // Road nextRoad = new SimpleRoad(theGrid, roadLocationAtTime(t), direction + angle, 1);
        return nextRoad.generateRoad(); // TODO: add 1?
    }
    */
}
