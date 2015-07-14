import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Zack on 7/7/2015.
 */
public class CarFactory implements Updateable {
    Road road;
    private int carsMade;
    private int lastUpdateTime;

    private final int secondsPerCar;

    public CarFactory(Road road, int secondsPerCar) {
        this.road = road;
        this.secondsPerCar = secondsPerCar;
    }

    /**
     * Check if should add car does if suppose to!
     */
    public void update(int timeElapsed) {
        int deltaT = timeElapsed - lastUpdateTime;
        if (deltaT >= secondsPerCar) {
            if (!road.isFull()) {
                makeCar(timeElapsed);
            }
            lastUpdateTime = timeElapsed;
            // should this be incremented anyway?
            // Driver.carID++;
        }
    }

    /**
     * Makes a new car and adds it to road.  The velocity is set to its lead car
     */
    public void makeCar(int timeElapsed) {
        int newCarID = Simulation.getCarID();
        CopyOnWriteArrayList<Double> carFacts = road.getStartState();
        Moveable newLC = road.getLast();
        int dir = (carFacts.get(2) > 0) ? 1 : -1;
        Car car = new Car(carFacts.get(0), carFacts.get(1), newLC.getVelocity(),
                road, newLC, road.isNS(), dir,
                timeElapsed, newCarID);
        road.addCar(car);
        carsMade++;
    }
}
