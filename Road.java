/**
 * @author Kendall Merritt
 */

public class Road {
    private int speedLimit;
    private boolean oneWay;
    private LinkedList<LinkedList<Car>> car;

    /**
     * Constructs a new Road object with a speed limit
     * @param speedLimit The road's speed limit
     */
    public Road(int speedLimit) {
        this.speedLimit = speedLimit;
        this.oneWay = false;
        this.cars = new LinkedList<Car>();
    }

    /**
     * Constructs a new Road object with a speed limit and one way arg
     * @param speedLimit The road's speed limit
     * @param oneWay True if one-way road, false if two-way road
     */
    public Road(int speedLimit, boolean oneWay) {
        this.speedLimit = speedLimit;
        this.oneWay = oneWay;
        this.cars = new LinkedList<Car>();
    }

    /**
     * Updates the speed limit
     * @param speedLimit The road's speed limit
     */
    private void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }

    /**
     * Adds a car to the road's linked list of cars
     * @param car The car to add to the road
     */
    private void addCar(Car car) {
        this.cars.addLast(car);
    }

    /**
     * Removes a car from the road's linked list of cars
     */
    private void removeCar(Car car) {
        this.cars.pollFirst();
    }

    /**
     * Returns a boolean of whether the road is one way or not
     */
    private boolean isOneWay() {
        return this.oneWay;
    }

}
