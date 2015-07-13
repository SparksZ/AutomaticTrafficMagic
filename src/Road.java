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
    public final double roadStartX, roadEndX, roadStartY, roadEndY;

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
            roadStartX = xEastPos;
            roadEndX = xEastPos;
            if (pF) {
                roadStartY = yNorthPos;
                roadEndY = ySouthPos;
            }
            else {
                roadStartY = ySouthPos;
                roadEndY = yNorthPos;
            }
        } else { // East-West Road
            xEastPos = xWestPos + roadLength;
            ySouthPos = yNorthPos;
            roadStartY = yNorthPos;
            roadEndY = yNorthPos;
            if (pF) {
                roadStartX = xWestPos;
                roadEndX = xEastPos;
            }
            else {
                roadStartX = xEastPos;
                roadEndX = xWestPos;
            }
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
                            carContainer.addCar(0, car); // add car to north in queue
                            cars.remove(car);
                            wasRemoved = true;
                        }
                    } else { // travelling north
                        if (car.getYPosition() < yNorthPos) {
                            carContainer.addCar(2, car); //  removeCar(2)); // add car to south in queue
                            cars.remove(car);
                            wasRemoved = true;
                        }
                    }
                } else {
                    if (positiveFlow) { // travelling east
                        if (car.getXPosition() > xEastPos) {
                            carContainer.addCar(3, car); //  removeCar(1)); // add car to east in queue
                            cars.remove(car);
                            wasRemoved = true;
                        }
                    } else { // travelling west
                        if (car.getXPosition() < xWestPos) {
                            carContainer.addCar(1, car); //  removeCar(3)); // add car to west in queue
                            cars.remove(car);
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
        car.setRoad(this);
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
        return cars.remove(0);
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
        Moveable result;

        if (!cars.isEmpty()) {
            return cars.get(cars.size() - 1);
        } else {
            if (nS) {
                if (positiveFlow) { // Southbound
                    result = carContainer.getLast(0);
                } else { // Northbound
                    result = carContainer.getLast(2);
                }
            } else {
                if (positiveFlow) { // Eastbound
                    result = carContainer.getLast(3);
                } else { // Westbound
                    result = carContainer.getLast(1);
                }
            }
        }

        return result;
    }

    /**
     * @return the first car on the road
     */
    public Moveable getFirst() {
        return cars.get(0);
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
     * @return whether the road ends in a sink
     */
    public boolean getEndRoad() {
        return endRoad;
    }

    /**
     * @return whether the road runs north-south (true) or east-west (false)
     */
    public boolean getNS() {
        return nS;
    }

    /**
     * @return if the road travels in the "positive" direction (south or east)
     */
    public boolean getPF() {
        return positiveFlow;
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

    /**
     * @return the furthest x postion of the road for placing dummy
     */
    public double getXEndForDummy() {
        if (nS) { // X is the same for north south roads at begining and end
            return xEastPos;
        } else { // e/w
            if (positiveFlow) { // eastbound
                return xEastPos + 1000;
            } else { // Westbound
                return xWestPos - 1000;
            }
        }
    }

    /**
     * @return the furthest Y postion of the road for placing dummy
     */
    public double getYEndForDummy() {
        if (nS) { // N/S
            if (positiveFlow) { // Southbound
                return ySouthPos + 1000;
            } else {
                return yNorthPos - 1000;
            }
        } else { // Y is the same for north south roads at beginning and end
            return yNorthPos;
        }
    }

    /**
     * @return the beginning x postion of the road
     */
    public double getXStart() {
        if (nS) { // X is the same for north south roads at beginning and end
            return xEastPos;
        } else { // e/w
            if (positiveFlow) { // eastbound
                return xWestPos;
            } else { // Westbound
                return xEastPos;
            }
        }
    }

    /**
     * @return the beginning Y postion of the road
     */
    public double getYStart() {
        if (nS) { // N/S
            if (positiveFlow) { // Southbound
                return yNorthPos;
            } else {
                return ySouthPos;
            }
        } else { // Y is the same for north south roads at beginning and end
            return yNorthPos;
        }
    }

    /**
     * Checks if the road is full and the factory shouldn't make a new car
     * @return whether the road is full or not
     */
    public boolean isFull() {
        Moveable car = getLast();

        if (nS) {
            if (positiveFlow) { // Southbound
                if (car.getYPosition() < getYStart() + 8) { // 8 meters from start of road
                    return true;
                }
            } else { // Northbound
                if (car.getYPosition() > getYStart() - 8) {
                    return true;
                }
            }
        } else { // e/w
            if (positiveFlow) { // Eastbound
                if (car.getXPosition() < getXStart() + 8) {
                    return true;
                }
            } else { // Westbound
                if (car.getXPosition() > getXStart() - 8) {
                    return true;
                }
            }
        }

        return false;
    }
}
