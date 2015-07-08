

/**
 * @author Kendall Merritt
 */
import java.util.concurrent.CopyOnWriteArrayList;

public class Road implements Updateable {
    private final boolean endRoad;
    private double speedLimit; // m/s
    private boolean nS; // north-south or not (e/w)
    private boolean positiveFlow; // Cars travel in positive coordinates (s/e) or not (n/w)
    private CopyOnWriteArrayList<Moveable> cars;
    private double roadLength;
    private double xWestPos, yNorthPos, xEastPos, ySouthPos;
    private CarContainer carContainer; // sink/intersection at the end of the road

    /**
     * Constructs a new Road
     * @param speedLimit the speed limit of the road in m/s
     * @param roadLength the length of the road (not including the intersection)
     * @param i the intersection at the end of the road segment
     * @param xWestPos the x position where the segment starts
     * @param yNorthPos the y position where the segment starts
     * @param nS whether the road travels north/south or not (east/west)
     * @param pF whether the cars are going in a positive direction (e/s) or not (n/w)
     * @param endRoad whether it is connect to a sink or not
     */
    public Road(double speedLimit, double roadLength, CarContainer i,
                double xWestPos, double yNorthPos, boolean nS, boolean pF,
                boolean endRoad) {
        this.speedLimit = speedLimit;
        cars = new CopyOnWriteArrayList<>();
        this.roadLength = roadLength;
        carContainer = i;
        this.xWestPos = xWestPos;
        this.yNorthPos = yNorthPos;
        this.nS = nS;
        positiveFlow = pF;
        this.endRoad = endRoad;

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
                boolean wasRemoved = false; // Flag to set if was removed or not

                if (nS) {
                    if (positiveFlow) { // travelling south
                        if (car.getYPosition() > ySouthPos) {
                            carContainer.addCar(0, removeCar(0)); // add car to north in queue
                            wasRemoved = true;
                        }
                    } else { // travelling north
                        if (car.getYPosition() < yNorthPos) {
                            carContainer.addCar(2, removeCar(2)); // add car to south in queue
                            wasRemoved = true;
                        }
                    }
                } else {
                    if (positiveFlow) { // travelling east
                        if (car.getXPosition() > xEastPos) {
                            carContainer.addCar(1, removeCar(1)); // add car to east in queue
                            wasRemoved = true;
                        }
                    } else { // travelling west
                        if (car.getXPosition() < xWestPos) {
                            carContainer.addCar(3, removeCar(3)); // add car to west in queue
                            wasRemoved = true;
                        }
                    }
                }

                if (wasRemoved && carContainer instanceof CarSink) {
                    Car c = (Car) car;
                    c.setEndTime(Driver.getTimeElapsed());
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
     * Adds a car to the road's ArrayList of cars
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
        if (!endRoad && cars.size() > 1) {
            Intersection intersection = (Intersection) carContainer;
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
     * @return the carContainer at the end of this road segment
     */
    public CarContainer getIntersection() {
        return carContainer;
    }

    /**
     * Sets the carContainer to the passed object
     * @param int1 new carContainer
     */
    public void setIntersection(CarContainer int1) {
        carContainer = int1;
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

    /**
     * Used by CarFactory to know where to set the car and its direction
     * @return the (X, Y, direction) to start the car
     */
    public CopyOnWriteArrayList<Double> getStartState() {
        CopyOnWriteArrayList<Double> result = new CopyOnWriteArrayList<>();

        if (nS) {
            result.add(xEastPos);

            if (positiveFlow) { // travelling south
                result.add(yNorthPos);
                result.add(1.0);
            } else { // travelling north
                result.add(ySouthPos);
                result.add(-1.0);
            }

        } else {

            if (positiveFlow) { // travelling east
                result.add(xWestPos);
                result.add(yNorthPos);
                result.add(1.0);
            } else { // travelling west
                result.add(xEastPos);
                result.add(yNorthPos);
                result.add(-1.0);
            }
        }

        return result;
    }


}
