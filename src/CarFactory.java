import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Zack on 7/7/2015.
 */
public class CarFactory implements Updateable {
    Road road;
    int secondsPerCar;
    int carsMade;

    public CarFactory(Road road, int secondsPerCar) {
        this.road = road;
        this.secondsPerCar = secondsPerCar;
    }

    /**
     * Check if should add car does if suppose to!
     */
    public void update() {
        if (Visualization.getTimeElapsed() % secondsPerCar == 0) {
            if (!road.isFull()) {
                makeCar();
            }
        }
    }

    /**
     * Makes a new car and adds it to road.  The velocity is set to its lead car
     */
    public void makeCar() {
        CopyOnWriteArrayList<Double> carFacts = road.getStartState();
        Moveable newLC = road.getLast();
        int dir = (carFacts.get(2) > 0) ? 1 : -1;
        Car car = new Car(carFacts.get(0), carFacts.get(1), newLC.getVelocity(),
                road, newLC, road.isNS(), dir,
                Driver.getTimeElapsed());
        road.addCar(car);
        carsMade++;
        Visualization.cars.add(car);
    }
}
