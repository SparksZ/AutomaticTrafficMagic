

/**
 * @author Kendall Merritt
 */
import java.util.concurrent.CopyOnWriteArrayList;

public class Road {
    private double speedLimit; // m/s
    private boolean oneWay;
    private CopyOnWriteArrayList<Moveable> cars;
    private double roadLength;
    private double roadStart;
    private double roadEnd;
    private Intersection intersection; // Intersection at the end of the road

    /**
     * Constructs a new Road object with a speed limit
     * @param speedLimit The road's speed limit
     */
    public Road(double speedLimit, double roadLength, Intersection i,
                double roadStart) {
        this.speedLimit = speedLimit;
        this.oneWay = false;
        cars = new CopyOnWriteArrayList<>();
        this.roadLength = roadLength;
        intersection = i;
        this.roadStart = roadStart;
        roadEnd = roadStart + roadLength;
    }

    public void update() {
        if (!cars.isEmpty()) {
            for (int i = 0; i < cars.size(); i++) {
                cars.get(i).update();
                if (cars.get(i).getPosition() > roadEnd) { // need to account for intersection backing up
                    intersection.addCar(removeCar());
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
     * Removes a car from the road's linked list of cars
     */
    public Moveable removeCar() {
        if (cars.size() > 1) {
            cars.get(1).setLeadingCar(intersection.getLast());
        }
        return cars.remove(0); /* the first car (0) is a dummy to correct
                                  behavior of the cars approaching traffic
                                  lights/intersections */
    }

//    /**
//     * Returns a boolean of whether the road is one way or not
//     */
//    public boolean isOneWay() {
//        return this.oneWay;
//    }

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
     * @return the coordinate of the roads end and the intersections beginning
     */
    public double getRoadEnd() {
        return roadEnd;
    }
}
