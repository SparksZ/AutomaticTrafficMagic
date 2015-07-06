import java.util.LinkedList;

/**
 * @author Kendall Merritt
 */
import java.util.LinkedList;

public class Road {
    private double speedLimit; // m/s
    private boolean oneWay;
    private LinkedList<Car> cars;
    private double roadLength;
    // private LinkedList<LinkedList<Car>> cars;

    /**
     * Constructs a new Road object with a speed limit
     * @param speedLimit The road's speed limit
     */
    public Road(double speedLimit, double roadLength) {
        this.speedLimit = speedLimit;
        this.oneWay = false;
        cars = new LinkedList<>();
        this.roadLength = roadLength;
        //this.cars = new LinkedList<LinkedList<Car>>();
    }

    /**
     * Constructs a new Road object with a speed limit and one way arg
     * @param speedLimit The road's speed limit
     * @param oneWay True if one-way road, false if two-way road
     */
    public Road(double speedLimit, boolean oneWay) {
        this.speedLimit = speedLimit;
        this.oneWay = oneWay;
        cars = new LinkedList<>();
        //this.cars = new LinkedList<LinkedList<Car>>();
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
    public boolean addCar(Car car) {
//    	if(!oneWay){
//    		return false;
//    	}
        //this.cars.get(0).addLast(car);
        cars.addLast(car);
        return true;
    }

    /**
     * Removes a car from the road's linked list of cars
     */
    public Car removeCar(Car car) {
        //return cars.get(0).pollFirst();
        return cars.pollFirst();
    }

    /**
     * Returns a boolean of whether the road is one way or not
     */
    public boolean isOneWay() {
        return this.oneWay;
    }

    public Car getLast() {
        return cars.peekLast();
    }

    public Car getFirst() {
        return cars.peekFirst();
    }

    public double getRoadLength() {
        return roadLength;
    }

    public LinkedList<Car> getCars() {
        return cars;
    }

}
