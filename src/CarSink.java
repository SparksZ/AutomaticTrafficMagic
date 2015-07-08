import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Zack on 7/8/2015.
 */
public class CarSink implements CarContainer {

    CopyOnWriteArrayList<Moveable> cars;
    Road road;

    /**
     * Constructs a new CarSink
     * @param road the road that the sink is at the end of
     */
    public CarSink(Road road) {
        cars = new CopyOnWriteArrayList<>();
        this.road = road;
    }

    /**
     * Adds a car to the sink
     * @param car the car to add to the sink
     */
    public boolean addCar(int dummyInt, Moveable car) {
        cars.add(car);
        return true;
    }

    /**
     * Sets the sink's road
     * @param road road to be set
     */
    public void setRoad(Road road) {
        this.road = road;
    }

    /**
     * @return the average velocity of the cars in the sink
     */
    public double averageV() {
        double total = 0;

        for (Moveable c : cars) {
            Car car = (Car) c;
            double aV = car.getDistanceTravelled() / car.getLifetime();
            total += aV;
        }

        return total / cars.size();
    }
}
