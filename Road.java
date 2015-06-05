/**
 * @author Kendall Merritt
 */
import java.util.LinkedList;

public class Road {
    private int speedLimit;
    private boolean oneWay;
    private LinkedList<LinkedList<Car>> cars;

    /**
     * Constructs a new Road object with a speed limit
     * @param speedLimit The road's speed limit
     */
    public Road(int speedLimit) {
        this.speedLimit = speedLimit;
        this.oneWay = false;
        this.cars = new LinkedList<LinkedList<Car>>();
    }

    /**
     * Constructs a new Road object with a speed limit and one way arg
     * @param speedLimit The road's speed limit
     * @param oneWay True if one-way road, false if two-way road
     */
    public Road(int speedLimit, boolean oneWay) {
        this.speedLimit = speedLimit;
        this.oneWay = oneWay;
        this.cars = new LinkedList<LinkedList<Car>>();
    }

    /**
     * Updates the speed limit
     * @param speedLimit The road's speed limit
     */
    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }

    /**
     * Adds a car to the road's linked list of cars
     * @param car The car to add to the road
     */
    public boolean addCar(Car car) {
    	if(!oneWay){
    		return false;
    	}
        this.cars.get(0).addLast(car);
        return true;
    }

    /**
     * Removes a car from the road's linked list of cars
     */
    public Car removeCar(Car car) {
        return cars.get(0).pollFirst();
    }

    /**
     * Returns a boolean of whether the road is one way or not
     */
    public boolean isOneWay() {
        return this.oneWay;
    }

}
