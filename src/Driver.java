
import java.util.LinkedList;

/**
 * Created by Zack on 7/4/2015.
 */
public class Driver {

    private static LinkedList<Car> cars = new LinkedList<>();
    private static double circuitLength = 4000; // meters
    private static Road circuit;

    public static void main(String[] args) {
        circuit = new Road(44, circuitLength);

        addCars(15);

        int i = 0;

        while (i < 7200) {
            circuit.getCars().forEach(Car::update);
            i++;
        }

        i++;
    }

    private static void addCars(int num) {
        double firstPos = num * (Car.cLength + 2); // + 2 for gap between

        circuit.addCar(new Car(firstPos - 0 * (Car.cLength + 2), 0, circuit, null));

        for (int i = 1; i < num; i++) {
            circuit.addCar(new Car(firstPos - i * (Car.cLength + 2), 0, circuit, circuit.getLast()));
        }

        circuit.getFirst().setLeadingCar(circuit.getLast());
    }
}
