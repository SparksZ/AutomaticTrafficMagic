

/**
 * @author Kendall Merritt
 */
import java.util.concurrent.CopyOnWriteArrayList;

public class Road {
    private double speedLimit; // m/s
    private boolean nS; // north-south or not (e/w)
    private boolean positiveFlow; // Cars travel in positive coordinates (s/e) or not (n/w)
    private CopyOnWriteArrayList<Moveable> cars;
    private double roadLength;
    private double xWestPos, yNorthPos, xEastPos, ySouthPos;
    private Intersection intersection; // Intersection at the end of the road

    /**
     * Constructs a new Road
     * @param speedLimit the speed limit of the road in m/s
     * @param roadLength the length of the road (not including the intersection)
     * @param i the intersection at the end of the road segment
     * @param xWestPos the x position where the segment starts
     * @param yNorthPos the y position where the segment starts
     * @param nS whether the road travels north/south or not (east/west)
     * @param pF whether the cars are going in a positive direction (e/s) or not (n/w)
     */
    public Road(double speedLimit, double roadLength, Intersection i,
                double xWestPos, double yNorthPos, boolean nS, boolean pF) {
        this.speedLimit = speedLimit;
        cars = new CopyOnWriteArrayList<>();
        this.roadLength = roadLength;
        intersection = i;
        this.xWestPos = xWestPos;
        this.yNorthPos = yNorthPos;
        this.nS = nS;
        positiveFlow = pF;

        // North-South road
        if (nS) {
            xEastPos = xWestPos;
            ySouthPos = yNorthPos + roadLength;
        } else { // East-West Road
            xEastPos = xWestPos + roadLength;
            ySouthPos = yNorthPos;
        }
    }

    /**
     * Updates the positions of the cars on the road. If they get to the end,
     * they are added to the appropriate queue of the intersection at the end.
     */
    public void update() {
        if (!cars.isEmpty()) {
            for (Moveable car : cars) {
                car.update();

                if (nS) {
                    if (positiveFlow) { // travelling south
                        if (car.getYPosition() > ySouthPos) {
                            intersection.addCar(0, removeCar(0)); // add car to north in queue
                        }
                    } else { // travelling north
                        if (car.getYPosition() < yNorthPos) {
                            intersection.addCar(2, removeCar(2)); // add car to south in queue
                        }
                    }
                } else {
                    if (positiveFlow) { // travelling east
                        if (car.getXPosition() > xEastPos) {
                            intersection.addCar(1, removeCar(1)); // add car to east in queue
                        }
                    } else { // travelling west
                        if (car.getXPosition() < xWestPos) {
                            intersection.addCar(3, removeCar(3)); // add car to west in queue
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates the speed limit
     * @param speedLimit The road's speed limit
     */
    public void setSpeedLimit(double speedLimit) {
        this.speedLimit = speedLimit;
    }

    public double getSpeedLimit() {
        return speedLimit;
    }

    /**
     * Adds a car to the road's linked list of cars
     * @param car The car to add to the road
     */
    public boolean addCar(Moveable car) {
        cars.add(car);
        return true;
    }

    /**
     * Removes the lead car on the road and hooks the new lead car to the last
     * car in the intersections queue
     * @param i the specific queue in the intersection that the lead car is in
     * @return returns the lead car removed from the road
     */
    public Moveable removeCar(int i) {
        if (cars.size() > 1) {
            cars.get(1).setLeadingCar(intersection.getLast(i));
        }
        return cars.remove(0); /* the first car (0) is a dummy to correct
                                  behavior of the cars approaching traffic
                                  lights/intersections */
    }

    /**
     * @return a boolean of whether the road is North-South or not
     */
    public boolean isNS() {
        return nS;
    }

    /**
     * @return whether the cars flow positive (s/e) or not (n/w)
     */
    public boolean isPositiveFlow() {
        return positiveFlow;
    }

    /**
     * @return the last car on the road
     */
    public Moveable getLast() {
        return cars.get(cars.size() - 1);
    }

    /**
     * @return the first car on the road
     */
    public Moveable getFirst() {
        return cars.get(1);
    }

    /**
     * @return the length of the road
     */
    public double getRoadLength() {
        return roadLength;
    }

    /**
     * @return the LL of cars
     */
    public CopyOnWriteArrayList<Moveable> getCars() {
        return cars;
    }

    /**
     * @return the intersection at the end of this road segment
     */
    public Intersection getIntersection() {
        return intersection;
    }

    /**
     * Sets the intersection to the passed object
     * @param int1 new intersection
     */
    public void setIntersection(Intersection int1) {
        intersection = int1;
    }

    /**
     * @return the farthest west position of the road
     */
    public double getXWestPos() {
        return xWestPos;
    }

    /**
     * @return the farthest north position of the road
     */
    public double getYNorthPos() {
        return yNorthPos;
    }

    /**
     * @return the farthest east position of the road
     */
    public double getXEastPos() {
        return xEastPos;
    }

    /**
     * @return the farthest south position of the road
     */
    public double getYSouthPos() {
        return ySouthPos;
    }
}
